package thesmith.eventhorizon.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.google.appengine.api.datastore.Key;

@Entity
@Table(name = "status")
public class Status implements Serializable {
  private static final long serialVersionUID = 2586035568417551655L;

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
  @Column(name = "person_id", length = 255)
  private String personId;
  
  @Basic
  @Column(name = "status", length = 511)
  private String status;
  
  @Basic
  @Column(name = "created")
  @Temporal(TemporalType.TIMESTAMP)
  protected Date created;

  public Key getId() {
    return id;
  }

  public void setId(Key id) {
    this.id = id;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getPersonId() {
    return personId;
  }

  public void setPersonId(String personId) {
    this.personId = personId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }
}
