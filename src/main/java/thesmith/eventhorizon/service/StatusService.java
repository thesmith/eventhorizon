package thesmith.eventhorizon.service;

import java.util.Date;
import java.util.List;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Status;

/**
 * Status service defines an interface for creating and retrieving statuses from
 * different domains
 * 
 * @author bens
 */
public interface StatusService {
  /**
   * Create a new status for a person
   * @param status
   */
  public void create(Status status);

  /**
   * Get a specific status update from a user, for a domain, at a time
   * @param personId
   * @param domain
   * @param from
   */
  public Status find(String personId, String domain, Date from);
  
  /**
   * List all statuses retrieved for an account
   * @param account
   * @param from
   * @return
   */
  public List<Status> list(Account account, Date from);
}
