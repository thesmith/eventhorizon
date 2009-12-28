package thesmith.eventhorizon.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import javax.servlet.http.Cookie;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import thesmith.eventhorizon.model.User;
import thesmith.eventhorizon.service.UserService;

public class BaseControllerTest {
  private BaseController controller;
  private UserService userService;
  
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  
  @Before
  public void setUp() throws Exception {
    userService = createMock(UserService.class);
    controller = new BaseController();
    controller.setUserService(userService);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }
  
  @Test
  public void shouldAuthUser() throws Exception {
    String token = "validToken";
    User user = new User();
    user.setUsername("personId");
    user.setPassword("somepass");
    EasyMock.expect(userService.authn(token)).andReturn(user);
    replay(userService);
    
    Cookie cookie = new Cookie(BaseController.COOKIE, token);
    request.setCookies(cookie);
    
    User returnedUser = controller.auth(request, response);
    assertNotNull(returnedUser);
    assertEquals(user.getUsername(), returnedUser.getUsername());
  }
  
  @Test
  public void shouldNotAuthInvalidUser() throws Exception {
    String token = "invalidToken";
    EasyMock.expect(userService.authn(token)).andReturn(null);
    replay(userService);
    
    Cookie cookie = new Cookie(BaseController.COOKIE, token);
    request.setCookies(cookie);
    
    User returnedUser = controller.auth(request, response);
    assertNull(returnedUser);
  }
}
