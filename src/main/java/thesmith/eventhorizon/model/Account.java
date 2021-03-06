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

import lombok.Data;

import com.google.appengine.api.datastore.Key;

@Entity
@Table(name = "account")
public @Data class Account implements Serializable, Cloneable {
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
  @Column(name = "person_id", length = 255)
  private String personId;
  
  @Basic
  @Column(name = "template", length = 511)
  private String template;
  
  @Basic
  @Column(name = "user_url", length = 255)
  private String userUrl;
  
  @Basic
  @Column(name = "domain_url", length = 255)
  private String domainUrl;
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "processed")
  private Date processed;
}
