package thesmith.eventhorizon.controller;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.servlet.http.Cookie;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.User;
import thesmith.eventhorizon.service.AccountService;
import thesmith.eventhorizon.service.StatusService;
import thesmith.eventhorizon.service.UserService;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class AccountsControllerTest {
  private AccountsController controller;
  private UserService userService;
  private AccountService accountService;
  private StatusService statusService;
  private Queue queue;
  
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  
  private String token = "validToken";
  private User user;
  
  @Before
  public void setUp() throws Exception {
    user = new User();
    user.setUsername("personId");
    user.setPassword("somepass");
    
    userService = createMock(UserService.class);
    EasyMock.expect(userService.authn(token)).andReturn(user);
    replay(userService);
    
    accountService = createMock(AccountService.class);
    statusService = createMock(StatusService.class);
    queue = createMock(Queue.class);
    controller = new AccountsController();
    controller.setUserService(userService);
    controller.setAccountService(accountService);
    controller.setStatusService(statusService);
    controller.setQueue(queue);
    
    request = new MockHttpServletRequest();
    Cookie cookie = new Cookie(AccountsController.COOKIE, token);
    request.setCookies(cookie);
    response = new MockHttpServletResponse();
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void shouldListAccounts() throws Exception {
    List<Account> accounts = Lists.newArrayList();
    Account account = new Account();
    account.setPersonId(user.getUsername());
    account.setDomain("domain");
    
    EasyMock.expect(accountService.list(user.getUsername())).andReturn(accounts);
    replay(accountService);
    
    ModelMap model = new ModelMap();
    String view = controller.list(model, request, response);
    assertEquals("accounts/list", view);
    assertTrue(model.containsKey("accounts"));
    assertEquals(accounts.size(), ((List<Account>) model.get("accounts")).size());
  }
  
  @Test
  public void shouldFindAccount() throws Exception {
    Account account = new Account();
    account.setPersonId(user.getUsername());
    account.setDomain("domain");
    
    EasyMock.expect(accountService.find(user.getUsername(), account.getDomain())).andReturn(account);
    replay(accountService, statusService);
    
    ModelMap model = new ModelMap();
    String view = controller.find(account.getDomain(), model, request, response);
    assertEquals("accounts/find", view);
    assertTrue(model.containsKey("account"));
    assertEquals(account.getPersonId(), ((Account) model.get("account")).getPersonId());
  }
  
  @Test
  public void shouldCreateAccount() throws Exception {
    Account account = new Account();
    account.setPersonId(user.getUsername());
    account.setDomain("domain");
    
    EasyMock.expect(queue.add(EasyMock.isA(TaskOptions.class))).andReturn(null);
    accountService.create(account);
    EasyMock.expectLastCall();
    replay(queue, accountService);
    
    ModelMap model = new ModelMap();
    String view = controller.update(account.getDomain(), account, model, request, response);
    assertEquals("accounts/find", view);
    assertFalse(model.containsKey("account"));
  }
}
