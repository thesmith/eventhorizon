package thesmith.eventhorizon.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Data;

import com.google.appengine.api.datastore.Key;

@Entity
@Table
public @Data class Snapshot implements Serializable {
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
  @Column(name = "domains")
  private List<String> domains;

  @Basic
  @Column(name = "created")
  private Date created;
}
