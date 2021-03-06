package thesmith.eventhorizon.service.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import thesmith.eventhorizon.model.Snapshot;
import thesmith.eventhorizon.model.Status;
import thesmith.eventhorizon.service.SnapshotService;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

/**
 * Implementation of service for snapshots of statuses
 * 
 * @author bens
 */
@Service
@Transactional
public class SnapshotServiceImpl implements SnapshotService {
  @PersistenceContext
  private EntityManager em;
  private final Log logger = LogFactory.getLog(this.getClass());

  @SuppressWarnings("unchecked")
  public List<Snapshot> list(String personId, Date from, Date to) {
    return em.createQuery(
        "select s from Snapshot s where s.personId = :personId and s.created >= :from"
            + " and s.created < :to order by s.created desc").setParameter("personId", personId).setParameter("from",
        from).setParameter("to", to).getResultList();
  }

  @SuppressWarnings("unchecked")
  public List<Snapshot> list(String personId, Date from, Date to, int page) {
    return em.createQuery(
        "select s from Snapshot s where s.personId = :personId and s.created >= :from"
            + " and s.created < :to order by s.created desc").setParameter("personId", personId).setParameter("from",
        from).setParameter("to", to).setMaxResults(MAX).setFirstResult(page * MAX).getResultList();
  }

  @SuppressWarnings("unchecked")
  public List<Snapshot> list(String personId, int page) {
    return em.createQuery("select s from Snapshot s where s.personId = :personId order by s.created desc")
        .setParameter("personId", personId).setMaxResults(MAX).setFirstResult(page * MAX).getResultList();
  }

  public void addStatus(Snapshot snapshot, Status status) {
    if (null == status || null == status.getId() || null == snapshot.getId())
      return;

    List<Key> statusIds = Lists.newArrayList(snapshot.getStatusIds());
    if (null == snapshot.getStatusIds() || null == snapshot.getDomains()) {
      snapshot.setStatusIds(Lists.<Key> newArrayList());
      snapshot.setDomains(Lists.<String> newArrayList());
    }

    int found = -1;
    for (int i = 0; i < snapshot.getDomains().size(); i++) {
      if (snapshot.getDomains().get(i).equals(status.getDomain())) {
        found = i;
        break;
      }
    }

    if (found >= 0) {
      snapshot.getStatusIds().add(found, status.getId());
      snapshot.getStatusIds().remove(found + 1);
    } else {
      snapshot.getStatusIds().add(status.getId());
      snapshot.getDomains().add(status.getDomain());
    }

    if (! snapshot.getStatusIds().equals(statusIds)) {
      try {
        em.merge(snapshot);
      } catch (IllegalStateException e) {
        if (logger.isWarnEnabled())
          logger.warn("Unable to update snapshot: "+snapshot+" which differed from its previous statusIds: "+statusIds);
      }
    }
  }

  public void create(Snapshot snapshot) {
    if (null == snapshot.getStatusIds())
      snapshot.setStatusIds(Lists.<Key> newArrayList());
    if (null == snapshot.getDomains())
      snapshot.setDomains(Lists.<String> newArrayList());
    em.persist(snapshot);
  }

  public Snapshot find(String personId, Date from) {
    return find(personId, from, "<=", "desc");
  }

  public Snapshot next(String personId, Date from) {
    return find(personId, from, ">", "asc");
  }

  public Snapshot previous(String personId, Date from) {
    return find(personId, from, "<", "desc");
  }

  @SuppressWarnings("unchecked")
  private Snapshot find(String personId, Date from, String created, String order) {
    List<Snapshot> snapshots = em.createQuery(
        "select s from Snapshot s where s.personId = :personId and s.created " + created + " :from order by s.created "
            + order).setParameter("personId", personId).setParameter("from", from).setMaxResults(1).getResultList();
    if (!snapshots.isEmpty()) {
      return snapshots.get(0);
    }

    return null;
  }
}
