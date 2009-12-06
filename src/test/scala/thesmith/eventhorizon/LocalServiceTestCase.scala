package thesmith.eventhorizon

import junit.framework._
import java.io.File

import com.google.appengine.tools.development.ApiProxyLocalImpl
import com.google.apphosting.api.ApiProxy

class LocalServiceTestCase extends TestCase {
  override def setUp() = {
    super.setUp();
    ApiProxy.setEnvironmentForCurrentThread(new TestEnvironment());
    ApiProxy.setDelegate(new ApiProxyLocalImpl(new File(".")){});
  }

  override def tearDown() = {
    // not strictly necessary to null these out but there's no harm either
    ApiProxy.setDelegate(null);
    ApiProxy.setEnvironmentForCurrentThread(null);
    super.tearDown();
  }
}
