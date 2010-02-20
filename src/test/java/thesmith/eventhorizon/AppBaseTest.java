package thesmith.eventhorizon;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.apphosting.api.ApiProxy;

/**
 * local service test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class AppBaseTest {
  private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  /**
   * setup env
   * 
   * @throws Exception
   *           exception
   */
  @Before
  public void setUp() throws Exception {
    ApiProxy.setEnvironmentForCurrentThread(new TestEnvironment());
    helper.setUp();
    System.setProperty("appengine.orm.disable.duplicate.emf.exception", "true");
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
    ApiProxy.setDelegate(null);
    ApiProxy.setEnvironmentForCurrentThread(null);
  }

  @Test
  public void testShouldPass() throws Exception {
    assertTrue(true);
  }
}