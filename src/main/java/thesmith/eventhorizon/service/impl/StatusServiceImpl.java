package thesmith.eventhorizon.service.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
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

  @SuppressWarnings("unchecked")
  public Status find(String personId, String domain, Date from) {
    List<Status> statuses = em
        .createQuery(
            "select s from Status s where s.personId = :personId and s.domain = :domain and s.created >= :from order by s.created desc")
        .setParameter("personId", personId).setParameter("domain", domain).setParameter("from", from).setMaxResults(1)
        .getResultList();
    if (null != statuses && statuses.size() > 0)
      return statuses.get(0);
    return null;
  }
}
