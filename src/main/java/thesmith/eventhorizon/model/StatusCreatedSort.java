package thesmith.eventhorizon.model;

import java.util.Comparator;

public class StatusCreatedSort implements Comparator<Status> {
  public int compare(Status s1, Status s2) {
    if (s1.getCreated() == null && s2.getCreated() == null)
      return 0;
    
    if (s2.getCreated() == null)
      return -1;
    
    if (s1.getCreated() == null)
      return 1;
    
    return s2.getCreated().compareTo(s1.getCreated());
  }
}
