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

  private static final String CACHE_KEY_PREFIX = "accounts_";
  private static final String DELIMITER = "_";

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private CacheService<Account> cache;

  /** {@inheritDoc} */
  public void create(Account account) {
    DOMAIN domain = this.validate(account);
    
    if (null == account.getTemplate())
      account.setTemplate(domain.getDefaultTemplate());
    if (null == account.getProcessed()) {
      Calendar agesago = Calendar.getInstance();
      agesago.add(Calendar.DAY_OF_WEEK, -5);
      account.setProcessed(agesago.getTime());
    }
    account.setDomainUrl(domain.getDomainUrl());
    if (null != account.getUserId() && null != domain.getUserUrl())
      account.setUserUrl(String.format(domain.getUserUrl(), account.getUserId()));

    Account existingAccount = this.findLiveAccount(account.getPersonId(), account.getDomain());
    if (null == existingAccount)
      em.persist(account);
    else if (!account.getUserId().equals(existingAccount.getUserId())) {
      existingAccount.setUserId(account.getUserId());
      em.merge(existingAccount);
      account = existingAccount;
    }
    cache.put(key(account.getPersonId(), account.getDomain()), account);
  }

  public Account account(String personId, String d) {
    DOMAIN domain = DOMAIN.valueOf(d);
    Account account = new Account();
    account.setPersonId(personId);
    account.setDomain(domain.toString());
    account.setDomainUrl(domain.getDomainUrl());
    if (null != domain.getUserUrl() && null != domain.getUserUrl())
      account.setUserUrl(String.format(domain.getUserUrl(), account.getUserId()));
    account.setTemplate(domain.getDefaultTemplate());
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
    List<String> domains = Lists.newArrayList();
    for (DOMAIN domain: DOMAIN.values()) {
      domains.add(domain.toString());
    }
    return domains;
  }

  /** {@inheritDoc} */
  public Account find(String personId, String domain) {
    Account account = cache.get(key(personId, domain));
    if (account == null) {
      account = findLiveAccount(personId, domain);
      if (null != account)
        cacheAccounts(personId, Lists.newArrayList(account));
    }
    return account;
  }

  private Account findLiveAccount(String personId, String domain) {
    try {
      return (Account) em.createQuery("select a from Account a where a.personId = :personId and a.domain = :domain")
          .setParameter("personId", personId).setParameter("domain", domain).getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<Account> list(String personId) {
    Map<String, Account> cachedAccounts = cache.getAll(cacheKeys(personId, domains(null)));
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

    for (String domain : domains(null)) {
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
    by.add(Calendar.HOUR, -2);
    List<Account> accounts = em.createQuery("select a from Account a where a.processed < :processed order by a.processed desc")
        .setParameter("processed", by.getTime()).setMaxResults(limit).getResultList();
    
    List<Account> filteredAccounts = Lists.newArrayList();
    for (Account account: accounts) {
      if (!FREESTYLE_DOMAINS.contains(account.getDomain()))
        filteredAccounts.add(account);
    }
    
    return filteredAccounts;
  }

  /** {@inheritDoc} */
  public void update(Account account) {
    em.merge(account);
    cache.put(key(account.getPersonId(), account.getDomain()), account);
  }

  private DOMAIN validate(Account account) {
    if (null == account)
      throw new RuntimeException("You must pass an account");
    if (null == account.getPersonId())
      throw new RuntimeException("You must include a personId");
    if (null == account.getDomain())
      throw new RuntimeException("You must include a domain");
    if (null == account.getUserId())
      throw new RuntimeException("You must include a userId");
    return AccountService.DOMAIN.valueOf(account.getDomain());
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
    for (String domain : domains(null)) {
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
