package thesmith.eventhorizon.service;

import java.util.List;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Event;

/**
 * Interface for service that retrieves Event objects
 * @author bens
 */
public interface EventService {
  /**
   * Retrieve a specific page of events
   * @param account
   * @param page
   * @return
   */
  public List<Event> events(Account account, int page);
}
