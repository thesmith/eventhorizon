package thesmith.eventhorizon.controller;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.Cookie;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import thesmith.eventhorizon.model.User;
import thesmith.eventhorizon.service.UserService;
import thesmith.eventhorizon.validator.LoginValidator;
import thesmith.eventhorizon.validator.RegisterValidator;

public class UsersControllerTest {
  private UsersController controller;
  private UserService userService;
  
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  
  @Before
  public void setUp() throws Exception {
    userService = createMock(UserService.class);
    LoginValidator loginValidator = new LoginValidator();
    loginValidator.setUserService(userService);
    RegisterValidator registerValidator = new RegisterValidator();
    registerValidator.setUserService(userService);
    
    controller = new UsersController();
    controller.setUserService(userService);
    controller.setLoginValidator(loginValidator);
    controller.setRegisterValidator(registerValidator);
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
    
    Cookie cookie = new Cookie(UsersController.COOKIE, token);
    request.setCookies(cookie);
    
    ModelMap model = new ModelMap();
    String view = controller.profile(model, request, response);
    assertEquals("users/profile", view);
    assertTrue(model.containsKey("user"));
    assertEquals(user.getUsername(), ((User) model.get("user")).getUsername());
  }
  
  @Test
  public void shouldLogout() throws Exception {
    String view = controller.logout(response);
    assertEquals("redirect:/users/login", view);
    
    Cookie cookie = response.getCookie(BaseController.COOKIE);
    assertNotNull(cookie);
    assertEquals(0, cookie.getMaxAge());
  }
  
  @Test
  public void shouldLogin() throws Exception {
    String token = "fjkdlsfjlkds";
    User user = new User();
    user.setUsername("jfkdlsjfkl");
    user.setPassword("pass");
    
    User returnUser = new User();
    returnUser.setUsername(user.getUsername());
    returnUser.setPassword("fjkdlsjfklds");
    EasyMock.expect(userService.authn(EasyMock.isA(User.class))).andReturn(returnUser);
    EasyMock.expect(userService.token(EasyMock.isA(User.class))).andReturn(token);
    replay(userService);
    
    BindingResult result = new BeanPropertyBindingResult(user, "user");
    String view = controller.login(user, result, response);
    assertEquals("redirect:/" + user.getUsername() + "/", view);
    
    Cookie cookie = response.getCookie(BaseController.COOKIE);
    assertNotNull(cookie);
    assertEquals(token, cookie.getValue());
  }
  
  @Test
  public void shouldRegister() throws Exception {
    String token = "fjkdlsfjlkds";
    User user = new User();
    user.setUsername("jfkdlsjfkl");
    user.setPassword("pass");
    
    User returnUser = new User();
    returnUser.setUsername(user.getUsername());
    returnUser.setPassword("fjkdlsjfklds");
    EasyMock.expect(userService.find(user.getUsername())).andReturn(null);
    userService.create(user);
    EasyMock.expectLastCall();
    EasyMock.expect(userService.token(EasyMock.isA(User.class))).andReturn(token);
    replay(userService);
    
    BindingResult result = new BeanPropertyBindingResult(user, "user");
    String view = controller.register(user, result, response);
    assertEquals("redirect:/" + user.getUsername() + "/", view);
    
    Cookie cookie = response.getCookie(BaseController.COOKIE);
    assertNotNull(cookie);
    assertEquals(token, cookie.getValue());
  }
}
