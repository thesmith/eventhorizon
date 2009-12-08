package thesmith.eventhorizon

import java.io.File
import java.util._

import com.google.appengine.tools.development.ApiProxyLocalImpl
import com.google.apphosting.api.ApiProxy

object SetupEnv {
  def setUp() = {
    val delegate = new ApiProxyLocalImpl(new File(".")){}
    ApiProxy.setEnvironmentForCurrentThread(new TestEnvironment())
    ApiProxy.setDelegate(delegate)
  }

  def tearDown() = {
    // not strictly necessary to null these out but there's no harm either
    ApiProxy.setDelegate(null)
    ApiProxy.setEnvironmentForCurrentThread(null)
  }
}

class TestEnvironment extends ApiProxy.Environment {
  def getAppId() = "Unit Tests"
  def getVersionId() = "1.0"
  def setDefaultNamespace(s: String) = {}
  def getRequestNamespace() = null
  def getDefaultNamespace() = null
  def getAuthDomain() = null
  def isLoggedIn() = false
  def getEmail() = null
  def isAdmin() = false
  def getAttributes(): Map[String, Object] = new HashMap[String, Object]()
}
