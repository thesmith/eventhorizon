package thesmith.eventhorizon;

import org.junit.After;
import org.junit.Before;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class DataStoreBaseTest extends TestBase {
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  /**
   * setup env
   * 
   * @throws Exception
   *           exception
   */
  @Before
  public void setUp() throws Exception {
    helper.setUp();
  }

  /**
   * cleanup
   * 
   * @throws Exception
   *           exception
   */
  @After
  public void tearDown() throws Exception {
    helper.tearDown();
  }
}