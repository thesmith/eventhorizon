package thesmith.eventhorizon.service.impl;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.service.AccountService;
import thesmith.eventhorizon.service.CacheService;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.com.google.common.collect.Maps;

/**
 * Implementation of the AccountService
 * 
 * @author bens
 */
@Transactional
@Service
public class AccountServiceImpl implements AccountService {
  public static final Map<String, String> defaults = Maps.newConcurrentMap();
  static {
    defaults.put(AccountService.DOMAIN.twitter.toString(),
        "{ago}, <a href='{userUrl}' rel='me'>I</a> <a href='{titleUrl}'>tweeted</a> '{title}'.");
    defaults.put(AccountService.DOMAIN.lastfm.toString(),
        "As far as <a href='{domainUrl}'>last.fm</a> knows, the last thing <a href='{userUrl}' rel='me'>I</a> listened to was <a href='{titleUrl}'>{title}</a>, and that was {ago}.");
    defaults.put(AccountService.DOMAIN.flickr.toString(),
         "<a href='{userUrl}' rel='me'>I</a> took a <a href='{titleUrl}'>photo</a> {ago} called '{title}' and uploaded it to <a href='{domainUrl}'>flickr</a>.");
    defaults.put(AccountService.DOMAIN.birth.toString(), "I was born {ago} in <a href='{titleUrl}'>{title}</a>");
    defaults.put(AccountService.DOMAIN.lives.toString(),
        "I now live in <a href='{titleUrl}'>{title}</a> which I moved to {ago}");
    defaults.put(AccountService.DOMAIN.wordr.toString(),
        "And, {ago}, <a href='{userUrl}'>my</a> last <a href='{domainUrl}'>word</a> was <a href='{titleUrl}'>{title}</a>");
    defaults.put(AccountService.DOMAIN.github.toString(),
        "{ago}, <a href='{userUrl}' rel='me'>I</a> pushed to {title}");
  }

  public static final Map<String, String> domainUrls = Maps.immutableMap(AccountService.DOMAIN.twitter.toString(),
      "http://twitter.com", AccountService.DOMAIN.lastfm.toString(), "http://last.fm", AccountService.DOMAIN.flickr
          .toString(), "http://flickr.com", AccountService.DOMAIN.wordr.toString(), "http://wordr.org",
      AccountService.DOMAIN.github.toString(), "http://github.com");

  public static final Map<String, String> userUrls = Maps.immutableMap(AccountService.DOMAIN.twitter.toString(),
      "http://twitter.com/%s", AccountService.DOMAIN.lastfm.toString(), "http://last.fm/user/%s",
      AccountService.DOMAIN.flickr.toString(), "http://flickr.com/people/%s", AccountService.DOMAIN.wordr.toString(),
      "http://wordr.org/users/%s", AccountService.DOMAIN.github.toString(), "http://github.com/%s");

  private static final String CACHE_KEY_PREFIX = "accounts_";
  private static final String DELIMITER = "_";

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private CacheService<Account> cache;

  /** {@inheritDoc} */
  public void create(Account account) {
    this.validate(account);
    if (null == account.getTemplate() && defaults.containsKey(account.getDomain()))
      account.setTemplate(defaults.get(account.getDomain()));
    if (null == account.getProcessed()) {
      Calendar agesago = Calendar.getInstance();
      agesago.add(Calendar.DAY_OF_WEEK, -5);
      account.setProcessed(agesago.getTime());
    }
    account.setDomainUrl(domainUrls.get(account.getDomain()));
    if (null != account.getUserId())
      account.setUserUrl(String.format(userUrls.get(account.getDomain()), account.getUserId()));

    if (null == this.find(account.getPersonId(), account.getDomain()))
      em.persist(account);
    cache.put(key(account.getPersonId(), account.getDomain()), account);
  }

  public Account account(String personId, String domain) {
    Account account = new Account();
    account.setPersonId(personId);
    account.setDomain(domain);
    account.setDomainUrl(domainUrls.get(account.getDomain()));
    if (userUrls.containsKey(account.getDomain()))
      account.setUserUrl(String.format(userUrls.get(account.getDomain()), account.getUserId()));
    account.setTemplate(defaults.get(account.getDomain()));
    return account;
  }

  /** {@inheritDoc} */
  public void delete(String personId, String domain) {
    Account account = this.findLiveAccount(personId, domain);
    if (null != account)
      em.remove(account);
    cache.put(key(personId, domain), null);
  }

  /** {@inheritDoc} */
  public List<String> domains(String personId) {
    return Lists.newArrayList(defaults.keySet());
  }

  /** {@inheritDoc} */
  public Account find(String personId, String domain) {
    try {
      Account account = cache.get(key(personId, domain));
      if (account == null) {
        account = findLiveAccount(personId, domain);
        cacheAccounts(personId, Lists.newArrayList(account));
      }
      return account;
    } catch (NoResultException e) {
      return null;
    }
  }

  private Account findLiveAccount(String personId, String domain) {
    return (Account) em.createQuery("select a from Account a where a.personId = :personId and a.domain = :domain")
        .setParameter("personId", personId).setParameter("domain", domain).getSingleResult();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<Account> list(String personId) {
    Map<String, Account> cachedAccounts = cache.getAll(cacheKeys(personId, Lists.newArrayList(defaults.keySet())));
    List<String> missingKeys = missingKeys(personId, cachedAccounts);

    if (!missingKeys.isEmpty()) {
      List<Account> liveAccounts = em.createQuery("select a from Account a where a.personId = :personId").setParameter(
          "personId", personId).getResultList();
      cachedAccounts.putAll(cacheAccounts(personId, liveAccounts));
    }

    return Lists.newArrayList(cachedAccounts.values());
  }

  /** {@inheritDoc} */
  public List<Account> listAll(String personId) {
    List<Account> usersAccounts = this.list(personId);
    List<Account> accounts = Lists.newArrayList(usersAccounts);

    for (String domain : defaults.keySet()) {
      boolean found = false;
      for (Account account : usersAccounts) {
        if (domain.equals(account.getDomain()))
          found = true;
      }

      if (!found) {
        accounts.add(account(personId, domain));
      }
    }
    return accounts;
  }

  @SuppressWarnings("unchecked")
  public List<Account> toProcess(int limit) {
    Calendar by = Calendar.getInstance();
    by.add(Calendar.HOUR, -1);
    return em.createQuery("select a from Account a where a.processed < :processed order by a.processed desc")
        .setParameter("processed", by.getTime()).setMaxResults(limit).getResultList();
  }

  /** {@inheritDoc} */
  public void update(Account account) {
    em.merge(account);
    cache.put(key(account.getPersonId(), account.getDomain()), account);
  }

  private void validate(Account account) {
    if (null == account)
      throw new RuntimeException("You must pass an account");
    if (null == account.getPersonId())
      throw new RuntimeException("You must include a personId");
    if (null == account.getDomain())
      throw new RuntimeException("You must include a domain");
    if (null == account.getUserId())
      throw new RuntimeException("You must include a userId");
    AccountService.DOMAIN.valueOf(account.getDomain());
  }

  private Map<String, Account> cacheAccounts(String personId, List<Account> accounts) {
    Map<String, Account> cacheAccounts = Maps.newHashMap();
    if (accounts.isEmpty())
      return cacheAccounts;

    for (Account account : accounts) {
      cacheAccounts.put(key(personId, account.getDomain()), account);
    }
    cache.putAll(cacheAccounts);
    return cacheAccounts;
  }

  private List<String> cacheKeys(String personId, List<String> domains) {
    List<String> keys = Lists.newArrayList();
    for (String domain : domains) {
      keys.add(key(personId, domain));
    }
    return keys;
  }

  private List<String> missingKeys(String personId, Map<String, Account> cachedAccounts) {
    List<String> missing = Lists.newArrayList();
    for (String domain : Lists.newArrayList(defaults.keySet())) {
      String key = key(personId, domain);
      if (!cachedAccounts.containsKey(key))
        missing.add(key);
    }
    return missing;
  }

  private String key(String personId, String domain) {
    return new StringBuilder(CACHE_KEY_PREFIX).append(personId).append(DELIMITER).append(domain).toString();
  }
}
