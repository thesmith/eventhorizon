package thesmith.eventhorizon.model;

import java.io.Serializable;

import javax.persistence.*;

import com.google.appengine.api.datastore.Key;

/**
 * snippet object
 */
@Entity
@Table(name = "account")
public class Account implements Serializable {
  private static final long serialVersionUID = -1071691679726356019L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key id;
  
  @Version
  @Column(name = "version")
  protected long version;

  @Basic
  @Column(name = "domain", length = 255)
  protected String domain;

  @Basic
  @Column(name = "user_id", length = 255)
  protected String userId;

  @Basic
  @Column(name = "username", length = 255)
  protected String username;

  @Basic
  @Column(name = "person_id", length = 255)
  private String personId;
  
  @Basic
  @Column(name = "template", length = 511)
  private String template;

  public Key getId() {
    return id;
  }

  public void setId(Key id) {
    this.id = id;
  }

  public String getPersonId() {
    return personId;
  }

  public void setPersonId(String personId) {
    this.personId = personId;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }
}
