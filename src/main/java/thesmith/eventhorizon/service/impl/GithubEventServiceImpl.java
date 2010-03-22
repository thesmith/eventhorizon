package thesmith.eventhorizon.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Event;
import thesmith.eventhorizon.service.EventService;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormat;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormatter;

@Service
public class GithubEventServiceImpl implements EventService {
  private static final String URL = "http://github.com/%s.json";
  private static final String COMMIT_TYPE = "PushEvent";
  private static final DateTimeFormatter formater = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss Z");
  private final Log logger = LogFactory.getLog(this.getClass());

  public List<Event> events(Account account, int page) {
    if (!"github".equals(account.getDomain()))
      throw new RuntimeException("You can only get events for the wordr domain");

    List<Event> events = Lists.newArrayList();
    // Can't currently paginate github
    if (page > 2)
      return events;

    try {
      URL url = new URL(String.format(URL, account.getPersonId()));
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      StringBuffer json = new StringBuffer();
      String line;
      while ((line = reader.readLine()) != null) {
        json.append(line);
      }
      reader.close();

      JSONArray activity = new JSONArray(json.toString());
      for (int i = 0; i < activity.length(); i++) {
        JSONObject act = activity.getJSONObject(i);
        String type = act.getString("type");

        if (COMMIT_TYPE.equals(type)) {
          JSONObject repo = act.getJSONObject("repository");
          String comment = act.getJSONObject("payload").getJSONArray("shas").getJSONArray(0).getString(2);
          String repoName = repo.getString("name");
          String repoUrl = repo.getString("url");
          String head = act.getJSONObject("payload").getString("head");
          Date created = formater.parseDateTime(act.getString("created_at")).toDate();
          String title = "<a href='" + repoUrl + "'>" + repoName + "</a> saying <a href='" + account.getUserUrl() + "/"
              + repoName + "/tree/" + head + "'>'" + comment + "'</a>";

          Event event = new Event();
          event.setCreated(created);
          event.setDomainUrl(account.getDomainUrl());
          event.setTitle(title);
          event.setTitleUrl("");
          event.setUserUrl(account.getUserUrl());
          events.add(event);
        }
      }
    } catch (Exception e) {
      if (logger.isWarnEnabled())
        logger.warn("Problem parsing github. A " + e.getClass() + " was thrown: " + e.getMessage());
      throw new RuntimeException(e);
    }

    return events;
  }
}
