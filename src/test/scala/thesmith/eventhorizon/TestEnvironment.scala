package thesmith.eventhorizon

import com.google.apphosting.api.ApiProxy

import java.util._

class TestEnvironment extends ApiProxy.Environment {
  def getAppId(): String = "event-horizon"
  def getVersionId(): String = "1.0"
  def getEmail(): String = throw new UnsupportedOperationException()
  def isLoggedIn(): Boolean = throw new UnsupportedOperationException()
  def isAdmin(): Boolean = throw new UnsupportedOperationException()
  def getAuthDomain(): String = throw new UnsupportedOperationException()
  def getRequestNamespace(): String = ""
  def getAttributes(): Map[String, Object] = new HashMap[String, Object]()
}
