package thesmith.eventhorizon

import com.google.appengine.api.datastore.dev.LocalDatastoreService
import com.google.appengine.tools.development.ApiProxyLocalImpl
import com.google.apphosting.api.ApiProxy

class LocalDatastoreTestCase extends LocalServiceTestCase {
  override def setUp() = {
    super.setUp()
    val proxy: ApiProxyLocalImpl = ApiProxy.getDelegate().asInstanceOf[ApiProxyLocalImpl]
    proxy.setProperty(LocalDatastoreService.NO_STORAGE_PROPERTY, Boolean.TRUE.toString())
  }

  override def tearDown() = {
    val proxy: ApiProxyLocalImpl = ApiProxy.getDelegate().asInstanceOf[ApiProxyLocalImpl]
    val datastoreService: LocalDatastoreService = proxy.getService("datastore_v3").asInstanceOf[LocalDatastoreService]
    datastoreService.clearProfiles()
    super.tearDown()
  }
}
