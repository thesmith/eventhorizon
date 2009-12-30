package thesmith.eventhorizon.service;

import java.util.Date;
import java.util.List;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Event;

/**
 * Interface for service that retrieves Event objects
 * @author bens
 */
public interface EventService {
  public List<Event> events(Account account, Date from);
}
