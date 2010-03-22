package thesmith.eventhorizon.service.impl;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Event;
import thesmith.eventhorizon.service.EventService;
import thesmith.eventhorizon.service.AccountService.DOMAIN;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class TumblrEventServiceImpl implements EventService {
  public List<Event> events(Account account, int page) {
    XmlReader reader = null;
    List<Event> events = Lists.newArrayList();
    DOMAIN domain = DOMAIN.valueOf(account.getDomain());
    
    try {
      String userUrl = String.format(domain.getUserUrl(), account.getUserId());
      URL url = new URL(userUrl+"/rss");
      reader = new XmlReader(url);
      SyndFeed feed = new SyndFeedInput().build(reader);
      
      for (Object e: feed.getEntries()) {
        Event event = new Event();
        SyndEntry entry = (SyndEntry) e;
        event.setCreated(entry.getPublishedDate());
        event.setDomainUrl(domain.getDomainUrl());
        event.setTitle(entry.getTitle());
        event.setTitleUrl(entry.getLink());
        event.setUserUrl(userUrl);
        events.add(event);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
    
    return events;
  }
}
