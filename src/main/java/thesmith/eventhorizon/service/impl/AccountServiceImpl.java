package thesmith.eventhorizon.service.impl;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.service.AccountService;

import com.google.appengine.repackaged.com.google.common.collect.Maps;

/**
 * Implementation of the AccountService
 * 
 * @author bens
 */
@Transactional
@Service
public class AccountServiceImpl implements AccountService {
  private static final Map<String, String> defaults = Maps.newHashMap();
  static {
    defaults.put("twitter", "{ago}, <a href='{userUrl}' rel='me'>I</a> <a href='{titleUrl}'>tweeted</a> '{title}'");
  }
  
  @PersistenceContext
  private EntityManager em;

  /** {@inheritDoc} */
  public void create(Account account) {
    if (null == this.find(account.getPersonId(), account.getDomain())) {
      if (null == account.getTemplate() && defaults.containsKey(account.getDomain()))
        account.setTemplate(defaults.get(account.getDomain()));
      em.persist(account);
    }
  }

  public void createOrUpdate(Account account) {
    if (null == account.getTemplate() && defaults.containsKey(account.getDomain()))
      account.setTemplate(defaults.get(account.getDomain()));
    if (null == this.find(account.getPersonId(), account.getDomain()))
      em.persist(account);
    else
      em.merge(account);
  }

  /** {@inheritDoc} */
  public void delete(String personId, String domain) {
    Account account = this.find(personId, domain);
    if (null != account)
      em.remove(account);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  public List<String> domains(String personId) {
    return em.createQuery("select distinct domain from Account a where a.personId = :personId").setParameter(
        "personId", personId).getResultList();
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
  public void update(Account account) {
    em.merge(account);
  }
}
