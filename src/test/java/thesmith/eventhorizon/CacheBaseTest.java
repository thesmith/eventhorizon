package thesmith.eventhorizon;

import org.junit.After;
import org.junit.Before;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class CacheBaseTest extends TestBase {
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());

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