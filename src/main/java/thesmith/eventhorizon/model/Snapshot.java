package thesmith.eventhorizon.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.com.google.common.collect.Maps;

@Entity
@Table
public class Snapshot implements Serializable {
  private static final long serialVersionUID = -2946316378710810137L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key id;

  @Version
  @Column(name = "version")
  protected long version;

  @Basic
  @Column(name = "person_id", length = 255)
  private String personId;

  @Basic
  @Column(name = "status_ids")
  private List<Key> statusIds;

  @Basic
  @Column(name = "created")
  private Date created;

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

  public List<Key> getStatusIds() {
    return statusIds;
  }

  public void setStatusIds(List<Key> statusIds) {
    this.statusIds = statusIds;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }
}
