package thesmith.eventhorizon.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;

import thesmith.eventhorizon.DataStoreBaseTest;
import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Event;
import thesmith.eventhorizon.service.impl.FlickrEventServiceImpl;

public class FlickrEventServiceImplTest extends DataStoreBaseTest {
  @Autowired
  private FlickrEventServiceImpl service;
  
  @Autowired
  private AccountService accountService;
  
  @Ignore
  public void shouldFindRich13() throws Exception {
    Account account = accountService.account("rich13", "flickr");
    account.setUserId("northover");
    
    List<Event> events = service.events(account, 1);
    assertNotNull(events);
    assertTrue(events.size() > 0);
  }
}
