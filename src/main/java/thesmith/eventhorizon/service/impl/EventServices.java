package thesmith.eventhorizon.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import thesmith.eventhorizon.service.EventService;
import thesmith.eventhorizon.service.AccountService.DOMAIN;

@Service
public class EventServices {
  @Autowired
  private FlickrEventServiceImpl flickr;
  @Autowired
  private GithubEventServiceImpl github;
  @Autowired
  private LastfmEventServiceImpl lastfm;
  @Autowired
  private TwitterEventServiceImpl twitter;
  @Autowired
  private WordrEventServiceImpl wordr;

  public EventService get(String key) {
    try {
      DOMAIN domain = DOMAIN.valueOf(key);
      switch (domain) {
      case flickr:
        return flickr;
      case github:
        return github;
      case lastfm:
        return lastfm;
      case twitter:
        return twitter;
      case wordr:
        return wordr;
      }
    } catch (Exception e) {}

    return null;
  }
}
