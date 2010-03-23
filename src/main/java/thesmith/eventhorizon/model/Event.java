package thesmith.eventhorizon.model;

import java.util.Date;

import lombok.Data;

/**
 * Transitory object that contains an event, the information to create a Status entity
 * @author bens
 */
public @Data class Event {
  private String title;
  private String titleUrl;
  private String userUrl;
  private String domainUrl;
  private Date created;
}
