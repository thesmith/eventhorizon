package thesmith.eventhorizon.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import thesmith.eventhorizon.AppBaseTest;
import thesmith.eventhorizon.model.User;

public class UserServiceImplTest extends AppBaseTest {
  @Autowired
  private UserService service;
  
  private User user;
  
  @Override
  public void setUp() throws Exception {
    super.setUp();
    user = new User();
    user.setUsername("someusername"+Math.random());
    user.setPassword("somepassword");
  }
  
  @Test
  public void testShouldHashString() throws Exception {
    String hash = service.hashPassword("somepassword");
    assertNotNull(hash);
    assertTrue(hash.length() > 1);
  }
  
  @Test
  public void testShouldCreateUser() throws Exception {
    service.create(user);
    assertNotNull(user.getId());
    
    User u = service.find(user.getUsername());
    assertNotNull(u);
    assertEquals(service.hashPassword("somepassword"), u.getPassword());
  }
}
