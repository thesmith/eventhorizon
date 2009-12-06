package thesmith.eventhorizon.model

import com.google.appengine.api.datastore.Key

import scala.reflect._
import javax.persistence._
import org.joda.time.DateTime

/** Info class represents individual status information */
@Entity
@Table{ val name = "info" }
class Info {
  @Id
  @GeneratedValue{ val strategy=GenerationType.IDENTITY }
  var id : Key = _

  @Version
  var version : Int = _

  @Column{ val name="person_id" }
  @BeanProperty var personId : String = _

  @Column{ val name="domain" }
  @BeanProperty var domain : String = _

  @Column{ val name="title" }
  @BeanProperty var title : String = _

  @Column{ val name="title_url" }
  @BeanProperty var titleUrl : String = _

  @Column{ val name="user_url" }
  @BeanProperty var userUrl : String = _

  @Column{ val name="domain_url" }
  @BeanProperty var domainUrl : String = _

  @Column{ val name="ago" }
  @BeanProperty var ago : Long = _
}

