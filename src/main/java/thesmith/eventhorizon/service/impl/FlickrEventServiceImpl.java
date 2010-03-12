package thesmith.eventhorizon.service.impl;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Event;
import thesmith.eventhorizon.service.EventService;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.people.PeopleInterface;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.urls.UrlsInterface;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.com.google.common.collect.Sets;

public class FlickrEventServiceImpl implements EventService {
  private static final int PAGE = 10;
  private static final String DOMAIN_URL = "http://flickr.com";
  private static final String KEY = "caf56542180f49cf50019be3a0e290b0";
  private static final String SECRET = "2383b862e64597be";
  private static final String PROFILE = "http://www.flickr.com/people/%s/";

  public List<Event> events(Account account, int page) {
    if (!"flickr".equals(account.getDomain()))
      throw new RuntimeException("You can only get events for the flickr domain");

    Flickr flickr;
    try {
      flickr = new Flickr(KEY, SECRET, new REST());
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
    List<Event> events = Lists.newArrayList();
    PeopleInterface people = flickr.getPeopleInterface();
    UrlsInterface urls = flickr.getUrlsInterface();

    try {
      String url = "";
      try {
        url = urls.getUserProfile(account.getUserId());
      } catch (FlickrException fe) {
        if ("1".equals(fe.getErrorCode())) {
          User user = null;
          try {
            user = people.findByUsername(account.getUserId());
          } catch (FlickrException fe2) {
            String username = urls.lookupUser(String.format(PROFILE, account.getUserId()));
            if (null != username)
              user = people.findByUsername(username);
          }
          if (null == user)
            return events;
          
          account.setUserId(user.getId());
          url = urls.getUserProfile(account.getUserId());
        } else {
          throw new RuntimeException(fe);
        }
      }

      PhotoList photos = people.getPublicPhotos(account.getUserId(), Sets.newHashSet("date_taken"), PAGE, page);
      for (int i = 0; i < photos.size(); i++) {
        Photo photo = (Photo) photos.get(i);

        Event event = new Event();
        event.setCreated(photo.getDateTaken());
        event.setDomainUrl(DOMAIN_URL);
        event.setTitle(photo.getTitle());
        event.setTitleUrl(photo.getUrl());
        event.setUserUrl(url);
        events.add(event);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return events;
  }
}
