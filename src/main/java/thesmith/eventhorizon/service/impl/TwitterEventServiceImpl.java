package thesmith.eventhorizon.service.impl;

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
  
  public List<Event> events(Account account, int page) {
    if (!"twitter".equals(account.getDomain()))
      throw new RuntimeException("You can only get events for the twitter domain");

    Twitter twitter = new Twitter();
    twitter.setOAuthConsumer("dYarUJ53cwxmE2asJq3IA", "P44b26H3Hl8lmDiXXzDyYRsucY65rORiVbclKXPgdoc");
    List<Event> events = Lists.newArrayList();

    try {
      Paging paging = new Paging(page);
      List<Status> statuses = twitter.getUserTimeline(account.getUserId(), paging);
      if (logger.isInfoEnabled())
        logger.info("Retrieving user timeline for "+account.getUserId()+" and got "+statuses.size());
      for (Status status : statuses) {
        Event event = new Event();
        event.setDomainUrl(DOMAIN_URL);
        event.setTitle(this.processTweet(status.getText()));
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
  
  private String processTweet(String tweet) {
    return tweet.replaceAll("@([a-zA-Z_-]+)", "<a href='http://www.twitter.com/$1'>@$1</a>")
        .replaceAll("#([a-zA-Z_-]+)", "<a href='http://www.twitter.com/search?q=%23$1'>#$1</a>");
  }
}
