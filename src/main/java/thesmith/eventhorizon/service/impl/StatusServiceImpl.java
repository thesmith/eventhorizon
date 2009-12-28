package thesmith.eventhorizon.service.impl;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import thesmith.eventhorizon.model.Status;
import thesmith.eventhorizon.service.StatusService;

/**
 * Implementation of StatusService
 * 
 * @author bens
 */
@Transactional
@Service
public class StatusServiceImpl implements StatusService {
  @PersistenceContext
  private EntityManager em;

  public void create(Status status) {
    if (null == this.find(status.getPersonId(), status.getDomain(), status.getCreated()))
      em.persist(status);
  }

  public Status find(String personId, String domain, Date from) {
    try {
      return (Status) em
          .createQuery(
              "select s from Status s where s.personId = :personId and s.domain = :domain and s.created >= :from order by s.created desc")
          .setParameter("personId", personId).setParameter("domain", domain).setParameter("from", from)
          .getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }
}
