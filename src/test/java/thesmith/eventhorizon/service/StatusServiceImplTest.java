package thesmith.eventhorizon.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import thesmith.eventhorizon.AppBaseTest;
import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Status;

public class StatusServiceImplTest extends AppBaseTest {
  @Autowired
  private StatusService service;
  
  @Autowired
  private AccountService accountService;
  
  private Status status;
  
  @Override
  public void setUp() throws Exception {
    super.setUp();
    status = new Status();
    status.setDomain(AccountService.DOMAIN.twitter.toString());
    status.setPersonId("id"+Math.random());
    status.setCreated(new Date());
    status.setTitle("title");
    status.setTitleUrl("titleUrl");
  }
  
  @Test
  public void testShouldCreateStatus() throws Exception {
    service.create(status);
    assertNotNull(status.getId());
    
    Account account = accountService.account(status.getPersonId(), status.getDomain());
    account.setUserId("userId");
    
    Status stat = service.find(account, status.getCreated());
    assertNotNull(stat.getStatus());
    
    Date date = new Date(status.getCreated().getTime()+50L);
    stat = service.find(account, date);
  }
  
  @Test
  public void testShouldGetStatusFromWeekAgo() throws Exception {
    Calendar created = Calendar.getInstance();
    created.add(Calendar.DAY_OF_MONTH, -7);
    
    Status status = new Status();
    status.setDomain(AccountService.DOMAIN.flickr.toString());
    status.setPersonId("id"+Math.random());
    status.setCreated(created.getTime());
    status.setTitle("title");
    status.setTitleUrl("titleUrl");
    service.create(status);
    
    Account account = accountService.account(status.getPersonId(), status.getDomain());
    account.setUserId("userId");
    
    Status stat = service.find(account, created.getTime());
    assertNotNull(stat.getStatus());
    assertEquals(status.getCreated(), stat.getCreated());
  }
  
  @Test
  public void testShouldCreateStatusesFromTwitter() throws Exception {
    Account account = accountService.account(status.getPersonId(), status.getDomain());
    account.setUserId("thesmith");
    
    List<Status> statuses = service.list(account, 1);
    assertNotNull(statuses);
    assertTrue(statuses.size() > 0);
    Status stat = statuses.get(0);
    assertNotNull(stat);
    assertNotNull(stat.getTitle());
  }
}
