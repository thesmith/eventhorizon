package thesmith.eventhorizon.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

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
public class WordrEventServiceImpl implements EventService {
  public final static String STATUS_URL = "http://wordr.org/status/";
  private final static DateTimeFormatter formater = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  public List<Event> events(Account account, int page) {
    if (!"wordr".equals(account.getDomain()))
      throw new RuntimeException("You can only get events for the wordr domain");
    
    try {
      List<Event> events = Lists.newArrayList();
      String postfix = ".json";
      if (page > 1)
        postfix = postfix+"?from="+page;
      URL url = new URL(account.getUserUrl()+postfix);
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      StringBuffer json = new StringBuffer();
      String line;
      while ((line = reader.readLine()) != null) {
        json.append(line);
      }
      reader.close();
      
      JSONArray words = new JSONArray(json.toString());
      for (int i=0; i<words.length(); i++) {
        JSONObject word = words.getJSONObject(i).getJSONObject("status");
        Event event = new Event();
        event.setTitle(word.getString("message"));
        event.setDomainUrl(account.getDomainUrl());
        event.setTitleUrl(STATUS_URL+word.getString("id"));
        event.setUserUrl(account.getUserUrl());
        event.setCreated(formater.parseDateTime(word.getString("created_at")).toDate());
        
        events.add(event);
      }
      
      return events;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
