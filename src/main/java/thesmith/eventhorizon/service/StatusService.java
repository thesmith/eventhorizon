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
   * @param account 
   * @param from
   */
  public Status find(Account account, Date from);
  
  /**
   * Get the previous status update for a user
   * @param account 
   * @param from
   * @return
   */
  public Status previous(Account account, Date from);
  
  /**
   * Get the next status update for a user
   * @param account 
   * @param from
   * @return
   */
  public Status next(Account account, Date from);
  
  /**
   * List all statuses retrieved for an account
   * @param account
   * @return
   */
  public List<Status> list(Account account, int page);
}
