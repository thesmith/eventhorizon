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
import javax.persistence.Transient;
import javax.persistence.Version;

import lombok.Data;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

@Entity
@Table(name = "status")
public @Data class Status implements Serializable {
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

  @Transient
  private Text status;

  @Basic
  @Column(name = "title_url")
  private String titleUrl;

  @Basic
  @Column(name = "title")
  private String title;

  @Basic
  @Column(name = "created")
  @Temporal(TemporalType.TIMESTAMP)
  protected Date created;
  
  @Transient
  protected String created_at;

  @Transient
  protected String period;
  
  public String getStatus() {
    if (null == status)
      return null;
    return status.getValue();
  }

  public void setStatus(String status) {
    this.status = new Text(status);
  }
}
