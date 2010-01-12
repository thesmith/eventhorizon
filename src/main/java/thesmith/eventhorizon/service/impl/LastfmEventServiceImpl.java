package thesmith.eventhorizon.service.impl;

import java.util.List;

import net.roarsoftware.lastfm.Caller;
import net.roarsoftware.lastfm.Track;
import net.roarsoftware.lastfm.User;
import thesmith.eventhorizon.cache.AppEngineCache;
import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Event;
import thesmith.eventhorizon.service.EventService;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class LastfmEventServiceImpl implements EventService {
  private static final String API_KEY = "b25b959554ed76058ac220b7b2e0a026";
  private static final String DOMAIN_URL = "http://last.fm";
  
  private final AppEngineCache cache;
  
  public LastfmEventServiceImpl(AppEngineCache cache) {
    this.cache = cache;
  }

  public List<Event> events(Account account, int page) {
    if (!"lastfm".equals(account.getDomain()))
      throw new RuntimeException("You can only get events for the lastfm domain");

    try {
      List<Event> events = Lists.newArrayList();
      Caller.getInstance().setCache(cache);
      for (Track track: User.getRecentTracks(account.getUserId(), API_KEY)) {
        Event event = new Event();
        event.setTitle(track.getArtist()+" - "+track.getName());
        event.setTitleUrl(track.getUrl());
        event.setCreated(track.getPlayedWhen());
        event.setDomainUrl(DOMAIN_URL);
        event.setUserUrl(DOMAIN_URL+"/user/"+account.getUserId());
        events.add(event);
      }

      return events;
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    }
  }
}
