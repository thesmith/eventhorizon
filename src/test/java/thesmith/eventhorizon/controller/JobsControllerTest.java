package thesmith.eventhorizon.controller;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Status;
import thesmith.eventhorizon.model.User;
import thesmith.eventhorizon.service.AccountService;
import thesmith.eventhorizon.service.StatusService;
import thesmith.eventhorizon.service.UserService;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class JobsControllerTest {
  private JobsController controller;
  private UserService userService;
  private AccountService accountService;
  private StatusService statusService;
  private Queue queue;
  
  private MockHttpServletRequest request;
  
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
    controller = new JobsController();
    controller.setUserService(userService);
    controller.setAccountService(accountService);
    controller.setStatusService(statusService);
    controller.setQueue(queue);
    
    request = new MockHttpServletRequest();
    Cookie cookie = new Cookie(JobsController.COOKIE, token);
    request.setCookies(cookie);
  }
  
  @Test
  public void shouldPage() throws Exception {
    Account account = new Account();
    account.setPersonId("person");
    account.setDomain("domain");
    account.setUserId("userId");
    EasyMock.expect(accountService.find(account.getPersonId(), account.getDomain())).andReturn(account);
    
    Calendar yesturday = Calendar.getInstance();
    yesturday.add(Calendar.DAY_OF_WEEK, -1);
    
    Status status = new Status();
    status.setCreated(yesturday.getTime());
    status.setDomain(account.getDomain());
    status.setPersonId(account.getPersonId());
    status.setStatus("some status");
    List<Status> statuses = Lists.newArrayList(status);
    EasyMock.expect(statusService.list(account, 1)).andReturn(statuses);
    statusService.create(status);
    EasyMock.expectLastCall();
    
    Date d = new Date(yesturday.getTimeInMillis()-1L);
    EasyMock.expect(statusService.find(account.getPersonId(), account.getDomain(), d)).andReturn(null);
    
    EasyMock.expect(queue.add(EasyMock.isA(TaskOptions.class))).andReturn(null);
    EasyMock.replay(accountService, statusService, queue);
    
    controller.page(account.getPersonId(), account.getDomain(), "1");
  }
}
