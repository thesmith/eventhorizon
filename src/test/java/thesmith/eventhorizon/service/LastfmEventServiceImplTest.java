package thesmith.eventhorizon.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;

import thesmith.eventhorizon.DataStoreBaseTest;
import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Event;
import thesmith.eventhorizon.service.impl.LastfmEventServiceImpl;

public class LastfmEventServiceImplTest extends DataStoreBaseTest {
  @Autowired
  private LastfmEventServiceImpl service;
  @Autowired
  private AccountService accountService;
  
  @Ignore
  public void shouldGetMarksTracks() throws Exception {
    Account account = accountService.account("marks", "lastfm");
    account.setUserId("maktheyak");
    
    List<Event> events = service.events(account, 1);
    assertNotNull(events);
  }
}
