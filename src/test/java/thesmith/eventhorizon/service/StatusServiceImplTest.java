package thesmith.eventhorizon.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import thesmith.eventhorizon.DataStoreBaseTest;
import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Status;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.com.google.common.collect.Maps;

public class StatusServiceImplTest extends DataStoreBaseTest {
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
  public void shouldCreateAndDelete() throws Exception {
    service.create(status);
    assertNotNull(status.getId());
    
    Account account = accountService.account(status.getPersonId(), status.getDomain());
    account.setUserId("userId");
    
    List<Status> statuses = service.list(account);
    assertEquals(1, statuses.size());
    
    service.delete(status.getId());
    
    statuses = service.list(account);
    assertEquals(0, statuses.size());
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
  
  @Test
  public void shouldGetStatusFromId() throws Exception {
    service.create(status);
    
    Map<String, Account> accounts = Maps.newHashMap();
    Account account = new Account();
    account.setDomain(status.getDomain());
    accounts.put(status.getDomain(), account);
    
    Status stat = service.find(status.getId(), new Date(), accounts);
    assertEquals(status.getTitle(), stat.getTitle());
  }
  
  @Test
  public void shouldUpdateStatus() throws Exception {
    service.create(status);
    status.setTitle("new title");
    service.create(status);
    assertNotNull(status.getId());
    
    Status stat = service.find(status.getId(), new Date(), Maps.<String, Account>newHashMap());
    assertNotNull(stat);
    assertEquals("new title", stat.getTitle());
    assertNotNull(stat.getId());
  }
  
  @Test
  public void shouldListStatuses() throws Exception {
    Collection<Key> ids = Lists.newArrayList();
    service.create(status);
    ids.add(status.getId());
    
    Status s = new Status();
    s.setDomain(AccountService.DOMAIN.flickr.toString());
    s.setPersonId("id"+Math.random());
    s.setCreated(new Date());
    s.setTitle("title");
    s.setTitleUrl("titleUrl");
    service.create(s);
    ids.add(s.getId());
    
    List<Status> statuses = service.list(ids, new Date(),  Maps.<String, Account>newHashMap());
    assertEquals(2, statuses.size());
  }
}
