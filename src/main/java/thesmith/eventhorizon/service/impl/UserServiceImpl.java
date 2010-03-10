package thesmith.eventhorizon.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import thesmith.eventhorizon.model.User;
import thesmith.eventhorizon.service.CacheService;
import thesmith.eventhorizon.service.UserService;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.com.google.common.util.Base64;

/**
 * Implementation of UserService
 * 
 * @author bens
 */
@Transactional
@Service
public class UserServiceImpl implements UserService {
  public static final String GRAVATAR_URL = "http://www.gravatar.com/avatar/%s?s=52";
  
  private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
  private static final String DELIMITOR = "|";
  private static final String CACHE_KEY_PREFIX = "gravatar_";
  private static final String USERS_KEY = "random_users";
  private final Log logger = LogFactory.getLog(this.getClass());
  private static final int MAX = 10;

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private CacheService<String> cache;
  @Autowired
  private CacheService<List<User>> userCache;

  /** {@inheritDoc} */
  public void create(User user) {
    if (null == this.find(user.getUsername())) {
      if (null != user.getEmail()) {
        user.setGravatar(md5Hex(user.getEmail()));
        if (null != cache && null != user.getGravatar())
          cache.put(CACHE_KEY_PREFIX + user.getUsername(), user.getGravatar());
      }
      user.setPassword(this.hash(user.getPassword()));
      em.persist(user);
    }
  }

  /** {@inheritDoc} */
  public void update(User user) {
    User u = this.find(user.getUsername());
    if (null != u) {
      if (null != user.getEmail()) {
        u.setGravatar(md5Hex(user.getEmail()));
        if (null != cache && null != user.getGravatar())
          cache.put(CACHE_KEY_PREFIX + user.getUsername(), user.getGravatar());
      }
      em.merge(u);
      user = u;
    }
  }

  /** {@inheritDoc} */
  public void delete(String username) {
    User user = this.find(username);
    if (null != user)
      em.remove(user);
  }

  /** {@inheritDoc} */
  public User find(String username) {
    try {
      return (User) em.createQuery("select u from User u where u.username = :username").setParameter("username",
          username).getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }
  
  /** {@inheritDoc} */
  public String getGravatar(String username) {
    String gravatar = null;
    if (null != cache)
      gravatar = cache.get(CACHE_KEY_PREFIX+username);
    
    if (null == gravatar) {
      User user = find(username);
      if (null != user && null != user.getGravatar()) {
        gravatar = user.getGravatar();
        if (null != cache)
          cache.put(CACHE_KEY_PREFIX + user.getUsername(), gravatar);
      }
    }
    
    return String.format(GRAVATAR_URL, gravatar);
  }

  /** {@inheritDoc} */
  public User authn(User unauthUser) throws NoResultException, SecurityException {
    User user = this.find(unauthUser.getUsername());
    if (null == user)
      throw new NoResultException("Unable to find user: " + unauthUser.getUsername());

    if (null == unauthUser.getPassword() || !this.hash(unauthUser.getPassword()).equals(user.getPassword()))
      throw new SecurityException("Invalid Password");

    return user;
  }

  /** {@inheritDoc} */
  public User authn(String unauthToken) {
    if (null == unauthToken)
      return null;

    String[] values = unauthToken.split("\\" + DELIMITOR, 2);
    if (logger.isDebugEnabled())
      logger.debug("Split cookie token " + unauthToken + " into: " + values + " with " + values.length + " elements");
    if (values.length < 1)
      return null;

    if (logger.isDebugEnabled())
      logger.debug("Retrieving user for " + values[0]);
    User user = this.find(values[0]);
    if (null == user)
      return null;
    String token = this.token(user);
    if (logger.isDebugEnabled())
      logger.debug("Getting expected token for " + user.getUsername() + ": " + token);

    if (!token.equalsIgnoreCase(unauthToken))
      return null;
    if (logger.isDebugEnabled())
      logger.debug("Successfully authenticated " + user.getUsername());
    return user;
  }

  /** {@inheritDoc} */
  public String token(User user) {
    if (null == user)
      return null;
    if (logger.isDebugEnabled())
      logger.debug("Token for username: " + user.getUsername() + " and password: " + user.getPassword());
    return user.getUsername() + DELIMITOR + this.hash(user.getUsername() + DELIMITOR + user.getPassword());
  }

  /** {@inheritDoc} */
  public String hash(String string) {
    try {
      return this.calculateRFC2104HMAC(string, "eventhorizonsalt");
    } catch (SignatureException e) {
      throw new RuntimeException(e);
    }
  }
  
  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<User> randomList() {
    List<User> users = null;
    if (null != cache)
      users = userCache.get(USERS_KEY);
    
    if (null == users) {
      users = em.createQuery("select u from User u where u.gravatar is not null").setMaxResults(MAX).getResultList();
      userCache.put(USERS_KEY, Lists.newArrayList(users));
    }
    
    if (null == users)
      users = Lists.newArrayList();
    
    return users;
  }

  /**
   * Computes RFC 2104-compliant HMAC signature.
   * 
   * @param data
   *          The data to be signed. Mostly borrowed from
   *          http://docs.amazonwebservices.com/AWSSimpleQueueService
   *          /latest/SQSDeveloperGuide
   *          /index.html?AuthJavaSampleHMACSignature.html
   * 
   * @param key
   *          The signing key.
   * @return The Base64-encoded RFC 2104-compliant HMAC signature.
   * @throws java.security.SignatureException
   *           when signature generation fails
   */
  private String calculateRFC2104HMAC(String data, String key) throws SignatureException {
    try {
      // get an hmac_sha1 key from the raw key bytes
      SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

      // get an hmac_sha1 Mac instance and initialize with the signing key
      Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
      mac.init(signingKey);

      // compute the hmac on input data bytes
      byte[] rawHmac = mac.doFinal(data.getBytes());

      // base64-encode the hmac
      return Base64.encode(rawHmac);

    } catch (Exception e) {
      throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
    }
  }

  private String hex(byte[] array) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < array.length; ++i) {
      sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
    }
    return sb.toString();
  }

  private String md5Hex(String message) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      return hex(md.digest(message.getBytes("CP1252")));
    } catch (NoSuchAlgorithmException e) {
    } catch (UnsupportedEncodingException e) {
    }
    return null;
  }
}
