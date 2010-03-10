package thesmith.eventhorizon.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import thesmith.eventhorizon.DataStoreBaseTest;
import thesmith.eventhorizon.model.User;

public class UserServiceImplTest extends DataStoreBaseTest {
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
    String hash = service.hash("somepassword");
    assertNotNull(hash);
    assertTrue(hash.length() > 1);
  }
  
  @Test
  public void testShouldUpdateGravatar() throws Exception {
    service.create(user);
    assertNotNull(user.getId());
    
    user.setEmail("ben@thesmith.co.uk");
    service.update(user);
    
    assertNotNull(service.getGravatar(user.getUsername()));
  }
  
  @Test
  public void testShouldCreateUser() throws Exception {
    service.create(user);
    assertNotNull(user.getId());
    
    User u = service.find(user.getUsername());
    assertNotNull(u);
    assertEquals(service.hash("somepassword"), u.getPassword());
  }
  
  @Test
  public void testShouldAuth() throws Exception {
    service.create(user);
    
    User unauthUser = new User();
    unauthUser.setUsername(user.getUsername());
    unauthUser.setPassword("somepassword");
    
    assertNotNull(service.authn(unauthUser));
  }
  
  @Test
  public void testShouldGetToken() throws Exception {
    String token = service.token(user);
    assertNotNull(token);
    assertTrue(token.startsWith(user.getUsername()));
  }
  
  @Test
  public void testShouldAuthByUPAndCookie() throws Exception {
    service.create(user);
    
    User unauthUser = new User();
    unauthUser.setUsername(user.getUsername());
    unauthUser.setPassword("somepassword");
    
    User authUser = service.authn(unauthUser);
    
    String token = service.token(authUser);
    assertNotNull(token);
    authUser = service.authn(token);
    
    assertNotNull(authUser);
    assertEquals(user.getUsername(), authUser.getUsername());
    assertNotSame("somepassword", authUser.getPassword());
  }
  
  @Test
  public void shouldRetrieveUsers() throws Exception {
    for (int i=0; i<20; i++) {
      User user = new User();
      user.setUsername("user"+Math.random());
      user.setPassword("password");
      user.setEmail("ben@thesmith.co.uk");
      service.create(user);
    }
    
    List<User> users = service.randomList();
    assertNotNull(users);
    assertEquals(10, users.size());
    for (User user: users) {
      assertNotNull(user.getGravatar());
    }
  }
}
