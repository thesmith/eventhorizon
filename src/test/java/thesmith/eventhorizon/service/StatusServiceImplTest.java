package thesmith.eventhorizon.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import thesmith.eventhorizon.AppBaseTest;
import thesmith.eventhorizon.model.Status;

public class StatusServiceImplTest extends AppBaseTest {
  @Autowired
  private StatusService service;
  
  private Status status;
  
  @Override
  public void setUp() throws Exception {
    super.setUp();
    status = new Status();
    status.setDomain("domain");
    status.setPersonId("id"+Math.random());
    status.setCreated(new Date());
    status.setStatus("status");
  }
  
  @Test
  public void testShouldCreateStatus() throws Exception {
    service.create(status);
    assertNotNull(status.getId());
    
    Status stat = service.find(status.getPersonId(), status.getDomain(), status.getCreated());
    assertEquals(status.getStatus(), stat.getStatus());
    
    Date date = new Date(status.getCreated().getTime()+50L);
    stat = service.find(status.getPersonId(), status.getDomain(), date);
  }
}
