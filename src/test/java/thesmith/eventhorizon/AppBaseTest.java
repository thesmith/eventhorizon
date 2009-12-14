package thesmith.eventhorizon;

import static org.junit.Assert.*;

import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * local service test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class AppBaseTest {

  /**
   * setup env
   * 
   * @throws Exception
   *           exception
   */
  @Before
  public void setUp() throws Exception {
    ApiProxy.setEnvironmentForCurrentThread(new TestEnvironment());
    ApiProxy.setDelegate(new ApiProxyLocalImpl(new File(".")) {
    });
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
    ApiProxy.setDelegate(null);
    ApiProxy.setEnvironmentForCurrentThread(null);
  }

  @Test
  public void testShouldPass() throws Exception {
    assertTrue(true);
  }
}