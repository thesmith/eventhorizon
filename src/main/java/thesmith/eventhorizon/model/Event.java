package thesmith.eventhorizon.model;

import java.util.Date;

/**
 * Transitory object that contains an event, the information to create a Status entity
 * @author bens
 */
public class Event {
  private String title;
  private String titleUrl;
  private String userUrl;
  private String domainUrl;
  private Date created;
  
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getTitleUrl() {
    return titleUrl;
  }
  public void setTitleUrl(String titleUrl) {
    this.titleUrl = titleUrl;
  }
  public String getUserUrl() {
    return userUrl;
  }
  public void setUserUrl(String userUrl) {
    this.userUrl = userUrl;
  }
  public String getDomainUrl() {
    return domainUrl;
  }
  public void setDomainUrl(String domainUrl) {
    this.domainUrl = domainUrl;
  }
  public Date getCreated() {
    return created;
  }
  public void setCreated(Date created) {
    this.created = created;
  }
}
