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
import thesmith.eventhorizon.model.Snapshot;
import thesmith.eventhorizon.model.Status;
import thesmith.eventhorizon.model.User;
import thesmith.eventhorizon.service.AccountService;
import thesmith.eventhorizon.service.SnapshotService;
import thesmith.eventhorizon.service.StatusService;
import thesmith.eventhorizon.service.UserService;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class JobsControllerTest {
  private JobsController controller;
  private UserService userService;
  private AccountService accountService;
  private StatusService statusService;
  private SnapshotService snapshotService;
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
    snapshotService = createMock(SnapshotService.class);
    queue = createMock(Queue.class);
    controller = new JobsController();
    controller.setUserService(userService);
    controller.setAccountService(accountService);
    controller.setStatusService(statusService);
    controller.setSnapshotService(snapshotService);
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

    List<Account> accounts = Lists.newArrayList();
    Account a = new Account();
    a.setDomain("somedomain");
    a.setUserId("userId");
    accounts.add(a);
    EasyMock.expect(accountService.list(account.getPersonId())).andReturn(accounts);

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

    EasyMock.expect(statusService.find(EasyMock.isA(Account.class), EasyMock.isA(Date.class))).andReturn(null).anyTimes();
    EasyMock.expect(statusService.previous(EasyMock.isA(Account.class), EasyMock.isA(Date.class))).andReturn(null);

    EasyMock.expect(queue.add(EasyMock.isA(TaskOptions.class))).andReturn(null);

    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.HOUR, -1);
    List<Snapshot> snapshots = Lists.newArrayList();
    Snapshot snapshot = new Snapshot();
    snapshot.setCreated(cal.getTime());
    snapshot.setPersonId(account.getPersonId());
    snapshot.setStatusIds(Lists.<Key>newArrayList());
    snapshots.add(snapshot);

    EasyMock.expect(
        snapshotService.list(EasyMock.matches(account.getPersonId()), EasyMock.isA(Date.class), EasyMock
            .isA(Date.class))).andReturn(snapshots);
    snapshotService.addStatus(EasyMock.isA(Snapshot.class), EasyMock.isA(Status.class));
    EasyMock.expectLastCall();
    snapshotService.create(EasyMock.isA(Snapshot.class));
    EasyMock.expectLastCall();
    EasyMock.replay(accountService, statusService, snapshotService, queue);

    controller.page(account.getPersonId(), account.getDomain(), "1");
  }

  @Test
  public void shouldProcess() throws Exception {
    Account account = new Account();
    account.setPersonId("person");
    account.setDomain("domain");
    account.setUserId("userId");
    List<Account> accounts = Lists.newArrayList(account);

    EasyMock.expect(accountService.toProcess(JobsController.LIMIT)).andReturn(accounts);
    EasyMock.expect(queue.add(EasyMock.isA(TaskOptions.class))).andReturn(null);
    accountService.update(account);
    EasyMock.expectLastCall();
    EasyMock.replay(accountService, queue);

    controller.process();
  }
}
