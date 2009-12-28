package thesmith.eventhorizon.controller;

import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import thesmith.eventhorizon.model.Status;
import thesmith.eventhorizon.service.AccountService;
import thesmith.eventhorizon.service.StatusService;
import thesmith.eventhorizon.service.UserService;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class IndexControllerTest {
  private IndexController controller;
  
  private StatusService statusService;
  private UserService userService;
  private AccountService accountService;
  
  @Before
  public void setUp() throws Exception {    
    statusService = createMock(StatusService.class);
    userService = createMock(UserService.class);
    accountService = createMock(AccountService.class);
    
    controller = new IndexController();
    controller.setStatusService(statusService);
    controller.setAccountService(accountService);
    controller.setUserService(userService);
  }
  
  @Test
  public void shouldRedirectToNow() throws Exception {
    Pattern pattern = Pattern.compile("redirect:\\/person\\/\\d+\\/\\d+\\/\\d+\\/\\d+\\/\\d+\\/\\d+\\/");
    String personId = "person";
    
    String view = controller.start(personId);
    assertNotNull(view);
    Matcher matcher = pattern.matcher(view);
    assertTrue(matcher.matches());
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void shouldRetrieveIndexes() throws Exception {
    ModelMap model = new ModelMap();
    List<String> accounts = Lists.newArrayList();
    accounts.add("twitter");
    accounts.add("lastfm");
    EasyMock.expect(accountService.domains("person")).andReturn(accounts);
    
    Status twitterStatus = new Status();
    twitterStatus.setDomain("twitter");
    twitterStatus.setPersonId("person");
    twitterStatus.setStatus("twitter status");
    EasyMock.expect(statusService.find(EasyMock.matches("person"), EasyMock.matches("twitter"), EasyMock.isA(Date.class))).andReturn(twitterStatus);
    
    Status lastfmStatus = new Status();
    lastfmStatus.setDomain("lastfm");
    lastfmStatus.setPersonId("person");
    lastfmStatus.setStatus("lastfm status");
    EasyMock.expect(statusService.find(EasyMock.matches("person"), EasyMock.matches("lastfm"), EasyMock.isA(Date.class))).andReturn(lastfmStatus);
    EasyMock.replay(accountService, statusService, userService);
    
    String view = controller.index("person", 2010, 01, 01, 00, 00, 00, model);
    assertEquals("index/index", view);
    assertEquals("person", model.get("personId"));
    assertNotNull(model.get("from"));
    assertTrue(model.containsKey("statuses"));
    
    List<Status> statuses = (List<Status>) model.get("statuses");
    assertEquals(twitterStatus.getStatus(), statuses.get(0).getStatus());
    assertEquals(lastfmStatus.getStatus(), statuses.get(1).getStatus());
  }
}
