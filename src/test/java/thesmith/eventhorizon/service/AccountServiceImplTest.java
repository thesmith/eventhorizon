package thesmith.eventhorizon.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

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
  
  @Test
  public void testShouldOnlyCreateAccountOnce() throws Exception {
    String personId = "blah"+Math.random();
    for (int i=0; i<5; i++) {
      this.createAccount(personId, "somedomain");
    }
    
    List<String> domains = service.domains(personId);
    assertNotNull(domains);
    assertEquals(1, domains.size());
  }
  
  private void createAccount(String personId, String domain) {
    Account account = new Account();
    account.setDomain(domain);
    account.setPersonId(personId);
    account.setUserId("id");
    account.setTemplate("template");
    service.create(account);
  }
  
  @Test
  public void testShouldGetDomains() throws Exception {
    String personId = "blah"+Math.random();
    for (int i=0; i<5; i++) {
      this.createAccount(personId, "somedomain"+i);
    }
    
    List<String> domains = service.domains(personId);
    assertNotNull(domains);
    assertEquals(5, domains.size());
  }
}
