package thesmith.eventhorizon.service.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  private static final int PAGE = 10;
  private final Log logger = LogFactory.getLog(this.getClass());
  private static final Pattern p = Pattern
      .compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

  public List<Event> events(Account account, int page) {
    if (!"twitter".equals(account.getDomain()))
      throw new RuntimeException("You can only get events for the twitter domain");

    Twitter twitter = new Twitter();
    twitter.setOAuthConsumer("dYarUJ53cwxmE2asJq3IA", "P44b26H3Hl8lmDiXXzDyYRsucY65rORiVbclKXPgdoc");
    List<Event> events = Lists.newArrayList();

    try {
      Paging paging = new Paging(page, PAGE);
      List<Status> statuses = twitter.getUserTimeline(account.getUserId(), paging);
      if (logger.isInfoEnabled())
        logger.info("Retrieving user timeline for " + account.getUserId() + " and got " + statuses.size());
      for (Status status: statuses) {
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
    tweet = replaceUrl(tweet);
    return tweet.replaceAll("@([a-zA-Z0-9_-]+)", "<a href='http://www.twitter.com/$1'>@$1</a>").replaceAll(
        "#([a-zA-Z0-9_-]+)", "<a href='http://www.twitter.com/search?q=%23$1'>#$1</a>");
  }

  private String replaceUrl(String tweet) {
    Matcher m = p.matcher(tweet);
    if (null != tweet && m.find()) {
      String url = m.group();

      int start = tweet.indexOf(url);
      String replacement = String.format("<a href='%s'>%s</a>", url, url);
      String beginning = tweet.substring(0, start);
      String end = tweet.substring(start + url.length());
      tweet = beginning + replacement + end;
    }

    return tweet;
  }
}
