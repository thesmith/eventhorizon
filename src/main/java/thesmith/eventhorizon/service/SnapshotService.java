package thesmith.eventhorizon.service;

import java.util.Date;
import java.util.List;

import thesmith.eventhorizon.model.Snapshot;
import thesmith.eventhorizon.model.Status;

/**
 * Service interface to manage indexes of statuses for specific dates
 * 
 * @author bens
 */
public interface SnapshotService {
  public static final int MAX = 20;
  
  /**
   * Snapshot instance that contains the statuses for the nearest date to 'from'
   * @param personId
   * @param from
   * @return
   */
  public Snapshot find(String personId, Date from);
  
  /**
   * List all snapshots between 2 points in time
   * @param personId
   * @param from
   * @param to
   * @return
   */
  public List<Snapshot> list(String personId, Date from, Date to);
  
  /**
   * List all snapshots between 2 points in time
   * @param personId
   * @param from
   * @param to
   * @return
   */
  public List<Snapshot> list(String personId, Date from, Date to, int page);
  
  /**
   * Create a snapshot of statuses
   * @param snapshot
   */
  public void create(Snapshot snapshot);
  
  /**
   * Update a snapshot
   * @param snapshot
   */
  public void addStatus(Snapshot snapshot, Status status);
}
