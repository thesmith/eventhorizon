package thesmith.eventhorizon.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import thesmith.eventhorizon.AppBaseTest;
import thesmith.eventhorizon.model.Account;

public class AccountServiceImplTest extends AppBaseTest {
  @Autowired
  private AccountService service;
  
  private Account account;
  
  @Override
  public void setUp() throws Exception {
    super.setUp();
    account = new Account();
    account.setDomain("domain");
    account.setPersonId("id"+Math.random());
    account.setUserId("id");
    account.setTemplate("template");
  }
  
  @Test
  public void testShouldCreateAccount() throws Exception {
    service.create(account);
    assertNotNull(account.getId());
    
    Account acc = service.find(account.getPersonId(), account.getDomain());
    assertEquals(account.getUserId(), acc.getUserId());
    
    assertEquals(1, service.list(account.getPersonId()).size());
    
    service.delete(account.getPersonId(), account.getDomain());
    
    assertEquals(0, service.list(account.getPersonId()).size());
  }
}
