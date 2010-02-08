package thesmith.eventhorizon.service.impl;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.service.AccountService;

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
  private static final Map<String, String> defaults = Maps.immutableMap(
      AccountService.DOMAIN.twitter.toString(), "{ago}, <a href='{userUrl}' rel='me'>I</a> <a href='{titleUrl}'>tweeted</a> '{title}'.",
      AccountService.DOMAIN.lastfm.toString(), "As far as <a href='{domainUrl}'>last.fm</a> knows, the last thing <a href='{userUrl}' rel='me'>I</a> listened to was <a href='{titleUrl}'>{title}</a>, and that was {ago}.",
      AccountService.DOMAIN.flickr.toString(), "<a href='{userUrl}' rel='me'>I</a> took a <a href='{titleUrl}'>photo</a> {ago} called '{title}' and uploaded it to <a href='{domainUrl}'>flickr</a>.",
      AccountService.DOMAIN.birth.toString(), "I was born {ago} in <a href='{titleUrl}'>{title}</a>",
      AccountService.DOMAIN.lives.toString(), "I now live in <a href='{titleUrl}'>{title}</a> which I moved to {ago}");
  
  private static final Map<String, String> domainUrls = Maps.immutableMap(
      AccountService.DOMAIN.twitter.toString(), "http://twitter.com",
      AccountService.DOMAIN.lastfm.toString(), "http://last.fm",
      AccountService.DOMAIN.flickr.toString(), "http://flickr.com");
  
  private static final Map<String, String> userUrls = Maps.immutableMap(
      AccountService.DOMAIN.twitter.toString(), "http://twitter.com/%s",
      AccountService.DOMAIN.lastfm.toString(), "http://last.fm/user/%s",
      AccountService.DOMAIN.flickr.toString(), "http://flickr.com/people/%s");
  
  @PersistenceContext
  private EntityManager em;

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
    Account account = this.find(personId, domain);
    if (null != account)
      em.remove(account);
  }

  /** {@inheritDoc} */
  public List<String> domains(String personId) {
//    return em.createQuery("select distinct domain from Account a where a.personId = :personId").setParameter(
//        "personId", personId).getResultList();
    return Lists.newArrayList(defaults.keySet());
  }

  /** {@inheritDoc} */
  public Account find(String personId, String domain) {
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
    return em.createQuery("select a from Account a where a.personId = :personId").setParameter("personId", personId)
        .getResultList();
  }
  
  /** {@inheritDoc} */
  public List<Account> listAll(String personId) {
    List<Account> usersAccounts = this.list(personId);
    List<Account> accounts = Lists.newArrayList(usersAccounts);
    
    for (String domain: defaults.keySet()) {
      boolean found = false;
      for (Account account: usersAccounts) {
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
}
