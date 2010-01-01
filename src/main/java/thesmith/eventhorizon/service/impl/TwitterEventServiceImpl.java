package thesmith.eventhorizon.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Event;
import thesmith.eventhorizon.service.EventService;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormat;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormatter;

public class TwitterEventServiceImpl implements EventService {
  private static final String BASE_URL = "http://twitter.com/statuses/user_timeline.json?screen_name=";
  private static final String DOMAIN_URL = "http://twitter.com";
  private static final DateTimeFormatter formater = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy").withLocale(
      Locale.US);
  private final Log logger = LogFactory.getLog(this.getClass());

  public List<Event> events(Account account, Date from) {
    if (!"twitter".equals(account.getDomain()))
      throw new RuntimeException("You can only get events for the twitter domain");

    try {
      URL url = new URL(BASE_URL + account.getUserId());
      if (logger.isInfoEnabled())
        logger.info("Making request to " + url);

      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      StringBuffer json = new StringBuffer();
      String line;
      while ((line = reader.readLine()) != null) {
        json.append(line);
      }
      reader.close();
      if (logger.isDebugEnabled())
        logger.debug(json);

      List<Event> events = Lists.newArrayList();
      try {
        JSONArray statuses = new JSONArray(json.toString());
        if (logger.isInfoEnabled())
          logger.info("Retrieved " + statuses.length() + " from twitter user " + account.getUserId());

        for (int i = 0; i < statuses.length(); i++) {
          JSONObject status = statuses.getJSONObject(i);

          Event event = new Event();
          event.setTitle(status.getString("text"));
          event.setTitleUrl(DOMAIN_URL + "/" + account.getUserId() + "/status/" + status.getString("id"));
          event.setDomainUrl(DOMAIN_URL);
          event.setUserUrl(DOMAIN_URL + "/" + account.getUserId());
          event.setCreated(formater.parseDateTime(status.getString("created_at")).toDate());
          events.add(event);
        }
      } catch (JSONException e) {
        if (logger.isWarnEnabled())
          logger.warn("Can't decode json so gunna try checking the error");
        try {
          JSONObject error = new JSONObject(json.toString());
          if (logger.isWarnEnabled())
            logger.warn("Recieved error from twitter: " + error.getString("error"));
        } catch (JSONException e1) {
          throw new RuntimeException("Can't decode json", e);
        }
      }

      return events;
    } catch (MalformedURLException e) {
      throw new RuntimeException("Can't seem to construct a url out of " + BASE_URL + account.getUserId(), e);
    } catch (IOException e) {
      throw new RuntimeException("Couldn't read from " + BASE_URL + account.getUserId(), e);
    }
  }
}
