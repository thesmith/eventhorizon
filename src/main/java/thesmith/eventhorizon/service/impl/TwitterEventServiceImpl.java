package thesmith.eventhorizon.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Event;
import thesmith.eventhorizon.service.EventService;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class TwitterEventServiceImpl implements EventService {
  private static final String DOMAIN_URL = "http://twitter.com";
  private final Log logger = LogFactory.getLog(this.getClass());

  public List<Event> events(Account account, Date from) {
    if (!"twitter".equals(account.getDomain()))
      throw new RuntimeException("You can only get events for the twitter domain");

    Twitter twitter = new Twitter();
    twitter.setOAuthConsumer("dYarUJ53cwxmE2asJq3IA", "P44b26H3Hl8lmDiXXzDyYRsucY65rORiVbclKXPgdoc");
    List<Event> events = Lists.newArrayList();

    try {
      List<Status> statuses = twitter.getUserTimeline(account.getUserId(), new Paging());
      if (logger.isInfoEnabled())
        logger.info("Retrieving user timeline for "+account.getUserId()+" and got "+statuses.size());
      for (Status status : statuses) {
        Event event = new Event();
        event.setDomainUrl(DOMAIN_URL);
        event.setTitle(status.getText());
        event.setUserUrl(DOMAIN_URL + "/" + account.getUserId());
        event.setTitleUrl(DOMAIN_URL + "/" + account.getUserId() + "/" + status.getId());
        event.setCreated(status.getCreatedAt());
        events.add(event);
      }
    } catch (TwitterException e) {
      throw new RuntimeException(e);
    }
    return events;
  }
}
