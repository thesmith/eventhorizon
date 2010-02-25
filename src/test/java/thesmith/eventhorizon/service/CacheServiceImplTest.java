package thesmith.eventhorizon.service;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import thesmith.eventhorizon.CacheBaseTest;
import thesmith.eventhorizon.model.Account;

@Ignore
public class CacheServiceImplTest extends CacheBaseTest {
  @Autowired
  private CacheService cacheService;
  
  @Test
  public void shouldCacheSimple() throws Exception {
    cacheService.put("somekey", "somevalue");
    assertEquals("somevalue", cacheService.get("somekey"));
  }
  
  @Test
  public void shouldCacheEntity() throws Exception {
    Account account = new Account();
    account.setPersonId("personId");
    account.setDomain("domain");
    
    cacheService.put("account", account);
    
    Account cachedAccount = (Account) cacheService.get("account");
    assertEquals(account.getPersonId(), cachedAccount.getPersonId());
    assertEquals(account.getDomain(), cachedAccount.getDomain());
  }
}
