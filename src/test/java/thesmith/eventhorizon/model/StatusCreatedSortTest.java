package thesmith.eventhorizon.model;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.google.appengine.repackaged.com.google.common.collect.Lists;


public class StatusCreatedSortTest {
  @Test
  public void shouldSortNewestDescending() throws Exception {
    List<Status> statuses = Lists.newArrayList();
    
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_MONTH, -1);
    
    Status status = new Status();
    status.setCreated(cal.getTime());
    statuses.add(status);
    
    cal.add(Calendar.DAY_OF_MONTH, -4);
    
    Status status2 = new Status();
    status2.setCreated(cal.getTime());
    statuses.add(status2);
    
    Status status3 = new Status();
    status3.setCreated(new Date());
    statuses.add(status3);
    
    Collections.sort(statuses, new StatusCreatedSort());
    
    assertTrue(statuses.get(0).getCreated().getTime() >= statuses.get(1).getCreated().getTime());
    assertTrue(statuses.get(1).getCreated().getTime() >= statuses.get(2).getCreated().getTime());
  }
  
  @Test
  public void shouldSortNewestDescendingWithNull() throws Exception {
    List<Status> statuses = Lists.newArrayList();
    
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_MONTH, -1);
    
    Status status = new Status();
    status.setCreated(cal.getTime());
    statuses.add(status);
    
    Status status2 = new Status();
    status2.setCreated(null);
    statuses.add(status2);
    
    Status status3 = new Status();
    status3.setCreated(new Date());
    statuses.add(status3);
    
    Collections.sort(statuses, new StatusCreatedSort());
    
    assertTrue(statuses.get(0).getCreated().getTime() >= statuses.get(1).getCreated().getTime());
    assertNull(statuses.get(2).getCreated());
  }
}
