package thesmith.eventhorizon.service.impl;

import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import thesmith.eventhorizon.model.User;
import thesmith.eventhorizon.service.UserService;

import com.google.appengine.repackaged.com.google.common.util.Base64;

/**
 * Implementation of UserService
 * 
 * @author bens
 */
@Transactional
@Service
public class UserServiceImpl implements UserService {
  private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
  private static final String DELIMITOR = "|";
  
  @PersistenceContext
  private EntityManager em;

  /** {@inheritDoc} */
  public void create(User user) {
    if (null == this.find(user.getUsername())) {
      user.setPassword( this.hash(user.getPassword()) );
      em.persist(user);
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
    if (null == unauthToken) return null;
    
    String[] values = unauthToken.split("\\"+DELIMITOR, 2);
    if (values.length < 1) return null;
    
    User user = this.find(values[0]);
    if (null == user) return null;
    String token = this.token(user);
    
    if (!token.equalsIgnoreCase(unauthToken)) return null;
    return user;
  }
  
  /** {@inheritDoc} */
  public String token(User user) {
    if (null == user) return null;
    return user.getUsername()+DELIMITOR+this.hash(user.getUsername()+DELIMITOR+user.getPassword());
  }

  /** {@inheritDoc} */
  public String hash(String string) {
    try {
    return this.calculateRFC2104HMAC(string, "eventhorizonsalt");
    } catch (SignatureException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Computes RFC 2104-compliant HMAC signature. * @param data The data to be
   * signed. Mostly borrowed from
   * http://docs.amazonwebservices.com/AWSSimpleQueueService
   * /latest/SQSDeveloperGuide/index.html?AuthJavaSampleHMACSignature.html
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
}
