package thesmith.eventhorizon.service.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Service;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Event;
import thesmith.eventhorizon.service.EventService;
import thesmith.eventhorizon.service.AccountService.DOMAIN;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@Service
public class TumblrEventServiceImpl implements EventService {
  public List<Event> events(Account account, int page) {
    List<Event> events = Lists.newArrayList();
    DOMAIN domain = DOMAIN.valueOf(account.getDomain());
    String userUrl = String.format(domain.getUserUrl(), account.getUserId());
    SyndFeed feed = null;
    XmlReader reader = null;

    try {
      URL url = new URL(userUrl + "/page/" + page + "/rss");
      reader = new XmlReader(url);
      feed = new SyndFeedInput().build(reader);
    } catch (Exception e) {
      XmlReader reader2 = null;
      try {
        URL url = new URL(userUrl + "/rss");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        int code = connection.getResponseCode();
        if (code >= 300 && code < 400) {
          String location = connection.getHeaderField("location");
          if (location != null) {
            location = location.replace("/rss", "/page/"+page+"/rss");
            url = new URL(location);
            reader2 = new XmlReader(url);
            feed = new SyndFeedInput().build(reader2);
          }
        }
      } catch (Exception ex) {
        if (reader2 != null) {
          try {
            reader.close();
          } catch (IOException e2) {}
        }
      }
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }

    if (null != feed) {
      for (Object e : feed.getEntries()) {
        Event event = new Event();
        SyndEntry entry = (SyndEntry) e;
        event.setCreated(entry.getPublishedDate());
        event.setDomainUrl(domain.getDomainUrl());
        event.setTitle(entry.getTitle());
        event.setTitleUrl(entry.getLink());
        event.setUserUrl(userUrl);
        events.add(event);
      }
    }

    return events;
  }
}
