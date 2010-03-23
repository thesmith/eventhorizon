package thesmith.eventhorizon.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import lombok.Data;

import com.google.appengine.api.datastore.Key;

@Entity
@Table(name = "user")
public @Data class User implements Serializable {
  private static final long serialVersionUID = -1071691679726356018L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key id;
  
  @Version
  @Column(name = "version")
  protected long version;
  
  @Basic
  @Column(name = "username", length = 50)
  private String username;
  
  @Basic
  @Column(name = "password", length = 50)
  private String password;
  
  @Transient
  private String email;
  
  @Basic
  @Column(name = "gravatar")
  private String gravatar;
}
