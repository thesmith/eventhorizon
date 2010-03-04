package thesmith.eventhorizon.service.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

  @SuppressWarnings("unchecked")
  public List<Snapshot> list(String personId, Date from, Date to) {
    return em.createQuery(
        "select s from Snapshot s where s.personId = :personId and s.created >= :from"
            + " and s.created < :to order by s.created desc").setParameter("personId", personId).setParameter("from",
        from).setParameter("to", to).getResultList();
  }

  public void addStatus(Snapshot snapshot, Status status) {
    if (null == status || null == status.getId())
      return;

    if (null == snapshot.getStatusIds() || null == snapshot.getDomains()) {
      snapshot.setStatusIds(Lists.<Key>newArrayList());
      snapshot.setDomains(Lists.<String>newArrayList());
    }
    
    int found = -1;
    for (int i=0; i<snapshot.getDomains().size(); i++) {
      if (snapshot.getDomains().get(i).equals(status.getDomain())) {
        found = i;
        break;
      }
    }
    
    if (found >= 0) {
      snapshot.getStatusIds().add(found, status.getId());
      snapshot.getDomains().add(found, status.getDomain());
    } else {
      snapshot.getStatusIds().add(status.getId());
      snapshot.getDomains().add(status.getDomain());
    }

    em.merge(snapshot);
  }

  public void create(Snapshot snapshot) {
    if (null == snapshot.getStatusIds())
      snapshot.setStatusIds(Lists.<Key>newArrayList());
    if (null == snapshot.getDomains())
      snapshot.setDomains(Lists.<String>newArrayList());
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
