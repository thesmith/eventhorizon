package thesmith.eventhorizon.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import thesmith.eventhorizon.CacheBaseTest;
import thesmith.eventhorizon.model.Account;

import com.google.appengine.repackaged.com.google.common.collect.Maps;

public class CacheServiceImplTest extends CacheBaseTest {
  @Autowired
  private CacheService<Account> cacheService;
  
  @Test
  public void shouldCacheSimple() throws Exception {
    Account account = new Account();
    account.setPersonId("personId");
    account.setDomain("domain");
    
    cacheService.put("somekey", account);
    assertEquals(account.getDomain(), cacheService.get("somekey").getDomain());
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
  
  @Test
  public void shouldPutAndGetAll() throws Exception {
    Map<String, Account> objects = Maps.newHashMap();
    Account account = new Account();
    account.setPersonId("personId");
    account.setDomain("domain");
    objects.put(account.getPersonId(), account);
    
    Account account2 = new Account();
    account2.setPersonId("personId2");
    account2.setDomain("domain2");
    objects.put(account2.getPersonId(), account2);
    
    cacheService.putAll(objects);
    
    Map<String, Account> cachedObjects = cacheService.getAll(objects.keySet());
    assertEquals(account.getDomain(), cachedObjects.get(account.getPersonId()).getDomain());
    assertEquals(account2.getDomain(), cachedObjects.get(account2.getPersonId()).getDomain());
  }
  
  @Test
  public void shouldNullCache() throws Exception {
    Account account = new Account();
    account.setPersonId("personId");
    account.setDomain("domain");
    
    cacheService.put("account", account);
    
    Account cachedAccount = cacheService.get("account");
    assertEquals(account.getPersonId(), cachedAccount.getPersonId());
    assertEquals(account.getDomain(), cachedAccount.getDomain());
    
    cacheService.put("account", null);
    
    Account nullAccount = cacheService.get("account");
    assertNull(nullAccount);
  }
}
