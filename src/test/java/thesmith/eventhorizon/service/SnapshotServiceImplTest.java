package thesmith.eventhorizon.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import thesmith.eventhorizon.DataStoreBaseTest;
import thesmith.eventhorizon.model.Snapshot;
import thesmith.eventhorizon.model.Status;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class SnapshotServiceImplTest extends DataStoreBaseTest {
  @Autowired
  private SnapshotService service;
  @Autowired
  private StatusService statusService;
  
  private Snapshot snapshot;
  
  @Before
  public void setUp() throws Exception {
    super.setUp();
    snapshot = new Snapshot();
    snapshot.setPersonId("thesmith"+Math.random());
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.SECOND, -1);
    snapshot.setCreated(cal.getTime());
    snapshot.setStatusIds(Lists.<Key>newArrayList());
    service.create(snapshot);
  }
  
  @Test
  public void shouldCreateAndFind() throws Exception {
    Snapshot snap = service.find(snapshot.getPersonId(), snapshot.getCreated());
    assertEquals(snapshot.getPersonId(), snap.getPersonId());
    assertEquals(snapshot.getCreated(), snap.getCreated());
  }
  
  @Test
  public void shouldAddStatusToSnapshots() throws Exception {
    Snapshot snapshot2 = new Snapshot();
    snapshot2.setPersonId(snapshot.getPersonId());
    Calendar yesturday = Calendar.getInstance();
    yesturday.add(Calendar.DAY_OF_MONTH, -1);
    snapshot2.setCreated(yesturday.getTime());
    snapshot2.setStatusIds(Lists.<Key>newArrayList());
    service.create(snapshot2);
    
    Status status = new Status();
    status.setCreated(new Date());
    status.setDomain(AccountService.DOMAIN.twitter.toString());
    status.setPersonId(snapshot.getPersonId());
    status.setTitle("");
    status.setTitleUrl("");
    statusService.create(status);
    
    List<Snapshot> snapshots = service.list(snapshot.getPersonId(), yesturday.getTime(), new Date());
    assertNotNull(snapshots);
    assertEquals(2, snapshots.size());
    for (Snapshot s: snapshots) {
      service.addStatus(s, status);
    }
    
    Snapshot snap = service.find(snapshot.getPersonId(), snapshot.getCreated());
    assertEquals(snapshot.getPersonId(), snap.getPersonId());
    assertEquals(snapshot.getCreated(), snap.getCreated());
    assertEquals(1, snap.getStatusIds().size());
    assertNotNull(snap.getStatusIds().get(0));
    
    snap = service.find(snapshot.getPersonId(), yesturday.getTime());
    assertEquals(snapshot2.getPersonId(), snap.getPersonId());
    assertEquals(snapshot2.getCreated(), snap.getCreated());
    assertEquals(1, snap.getStatusIds().size());
    assertNotNull(snap.getStatusIds().get(0));
  }
}
