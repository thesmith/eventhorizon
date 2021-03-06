package thesmith.eventhorizon.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Snapshot;
import thesmith.eventhorizon.model.Status;
import thesmith.eventhorizon.model.StatusCreatedSort;
import thesmith.eventhorizon.model.User;
import thesmith.eventhorizon.service.impl.UserServiceImpl;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.com.google.common.collect.Maps;
import com.google.appengine.repackaged.com.google.common.collect.Sets;

@Controller
public class IndexController extends BaseController {
  public static final String FROM = "from";
  private static final DateFormat format = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
  private static final DateFormat urlFormat = new SimpleDateFormat("yyyy/MM/dd/kk/mm/ss");

  @RequestMapping(value = "/{personId}/{year}/{month}/{day}/{hour}/{min}/{sec}", method = RequestMethod.GET)
  public String index(@PathVariable("personId") String personId, @PathVariable("year") int year,
      @PathVariable("month") int month, @PathVariable("day") int day, @PathVariable("hour") int hour,
      @PathVariable("min") int min, @PathVariable("sec") int sec, ModelMap model, HttpServletRequest request) {
    try {
      Date from = format.parse(String.format("%d/%d/%d %d:%d:%d", year, month, day, hour, min, sec));
      this.setModel(personId, from, model);
    } catch (ParseException e) {
      if (logger.isWarnEnabled())
        logger.warn(e);
      return "redirect:/error";
    }
    this.setViewer(request, model);
    return "index/index";
  }

  @RequestMapping(value = "/{personId}/{year}/{month}/{day}/{hour}/{min}/{sec}/{domain}/previous", method = RequestMethod.GET)
  public String previous(@PathVariable("personId") String personId, @PathVariable("year") int year,
      @PathVariable("month") int month, @PathVariable("day") int day, @PathVariable("hour") int hour,
      @PathVariable("min") int min, @PathVariable("sec") int sec, @PathVariable("domain") String domain) {
    try {
      Date from = format.parse(String.format("%d/%d/%d %d:%d:%d", year, month, day, hour, min, sec));
      Account account = accountService.account(personId, domain);
      Status status = statusService.previous(account, from);
      if (null == status)
        status = statusService.find(account, from);
      return String.format("redirect:/%s/%s/", personId, urlFormat.format(status.getCreated()));
    } catch (ParseException e) {
      if (logger.isWarnEnabled())
        logger.warn(e);
      return "redirect:/error";
    }
  }

  @RequestMapping(value = "/{personId}/{year}/{month}/{day}/{hour}/{min}/{sec}/{domain}/next", method = RequestMethod.GET)
  public String next(@PathVariable("personId") String personId, @PathVariable("year") int year,
      @PathVariable("month") int month, @PathVariable("day") int day, @PathVariable("hour") int hour,
      @PathVariable("min") int min, @PathVariable("sec") int sec, @PathVariable("domain") String domain) {
    try {
      Date from = format.parse(String.format("%d/%d/%d %d:%d:%d", year, month, day, hour, min, sec));
      Account account = accountService.account(personId, domain);
      Status status = statusService.next(account, from);
      if (null == status)
        status = statusService.find(account, from);

      return String.format("redirect:/%s/%s/", personId, urlFormat.format(status.getCreated()));
    } catch (ParseException e) {
      if (logger.isWarnEnabled())
        logger.warn(e);
      return "redirect:/error";
    }
  }

  @RequestMapping(value = "/{personId}/{domain}/previous", method = RequestMethod.GET)
  public String previous(@PathVariable("personId") String personId, @PathVariable("domain") String domain,
      @RequestParam("from") String from, @RequestParam("timezone") String timezone, ModelMap model) {
    try {
      Account account = accountService.account(personId, domain);
      Status status = statusService.previous(account, this.parseDate(from, timezone));
      if (null != status)
        this.setModel(personId, status.getCreated(), model);
      else
        this.setModel(personId, this.parseDate(from, timezone), model);

    } catch (ParseException e) {
      if (logger.isWarnEnabled())
        logger.warn(e);
      return "redirect:/error";
    }
    return "index/index";
  }

  @RequestMapping(value = "/{personId}/{domain}/next", method = RequestMethod.GET)
  public String next(@PathVariable("personId") String personId, @PathVariable("domain") String domain,
      @RequestParam("from") String from, @RequestParam("timezone") String timezone, ModelMap model) {
    try {
      Account account = accountService.account(personId, domain);
      Status status = statusService.next(account, this.parseDate(from, timezone));
      if (null != status)
        this.setModel(personId, status.getCreated(), model);
      else
        this.setModel(personId, this.parseDate(from, timezone), model);

    } catch (ParseException e) {
      if (logger.isWarnEnabled())
        logger.warn(e);
      return "redirect:/error";
    }
    return "index/index";
  }

  @RequestMapping(value = "/{personId}/now", method = RequestMethod.GET)
  public String now(@PathVariable("personId") String personId, @RequestParam("from") String from,
      @RequestParam("timezone") String timezone, ModelMap model,
      HttpServletRequest request) {
    if (null != from && from.length() > 0) {
      try {
        this.setModel(personId, this.parseDate(from, timezone), model);

      } catch (ParseException e) {
        if (logger.isWarnEnabled())
          logger.warn(e);
        return "redirect:/error";
      }
    }
    this.setViewer(request, model);
    return "index/index";
  }

  @RequestMapping(value = "/{personId}", method = RequestMethod.GET)
  public String start(@PathVariable("personId") String personId, ModelMap model, HttpServletRequest request) {
    this.setModel(personId, null, model);

    this.setViewer(request, model);
    return "index/index";
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public String startNoPath(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    try {
      URL url = new URL(request.getRequestURL().toString());
      String host = url.getHost();
      if (null != host && host.contains(HOST_POSTFIX)) {
        String personId = host.replace(HOST_POSTFIX, "");
        if (!"www".equals(personId)) {
          this.setModel(personId, null, model);

          this.setViewer(request, model);
          return "index/index";
        }
      }
    } catch (MalformedURLException e) {
      if (logger.isInfoEnabled())
        logger.info("Unable to decode url from " + request.getRequestURL().toString());
    }

    model.addAttribute("secureHost", secureHost());
    String view = redirectIndex(request);
    if (null == view)
      view = "index/home";

    model.addAttribute("userLinks", userLinks(userService.randomList()));
    return view;
  }

  @RequestMapping(value = "/error", method = RequestMethod.GET)
  public String error() {
    return "error";
  }
  
  private Map<String, String> userLinks(List<User> users) {
    Map<String, String> userLinks = Maps.newHashMap();
    
    for (User user: users) {
      userLinks.put(userHost(user.getUsername()), String.format(UserServiceImpl.GRAVATAR_URL, user.getGravatar()));
    }
    
    return userLinks;
  }

  private void setModel(String personId, Date from, ModelMap model) {
    List<Status> statuses = Lists.newArrayList();
    Map<String, Account> accounts = accountMap(accountService.list(personId));
    if (null != from) {
      Snapshot snapshot = snapshotService.find(personId, from);
      if (snapshot != null)
        statuses = statusService.list(snapshot.getStatusIds(), from, accounts);
    } else {
      from = new Date();
      for (Account account : accounts.values()) {
        statuses.add(defaultStatus(personId, account.getDomain(), from));
      }
      model.addAttribute("refresh", true);
    }
    Set<String> emptyDomains = emptyDomains(accounts.keySet(), statuses);

    model.addAttribute("statuses", statuses);
    model.addAttribute("first", this.firstDate(Lists.newArrayList(statuses)));
    model.addAttribute("last", this.lastDate(Lists.newArrayList(statuses)));
    model.addAttribute("emptyDomains", emptyDomains);
    model.addAttribute("personId", personId);
    model.addAttribute("gravatar", userService.getGravatar(personId));
    model.addAttribute("from", from);
    model.addAttribute("secureHost", secureHost());
    model.addAttribute("exponentialRange", this.exponentialRange(Lists.newArrayList(statuses)));
  }
  
  private double exponentialRange(List<Status> statuses) {
    if (statuses.size() < 1)
      return 0.0;
    Collections.sort(statuses, new StatusCreatedSort());
    
    double firstDate = Long.valueOf(statuses.get(statuses.size()-1).getCreated().getTime()).doubleValue();
    double lastDate = Long.valueOf(statuses.get(0).getCreated().getTime()).doubleValue();
    if (firstDate == lastDate)
      return 0.0;
    
    return Math.exp((lastDate - firstDate) / 1000000000);
  }
  
  private Date firstDate(List<Status> statuses) {
    if (statuses.size() < 1)
      return null;
    Collections.sort(statuses, new StatusCreatedSort());
    return statuses.get(statuses.size()-1).getCreated();
  }
  
  private Date lastDate(List<Status> statuses) {
    if (statuses.size() < 1)
      return null;
    Collections.sort(statuses, new StatusCreatedSort());
    return statuses.get(0).getCreated();
  }

  private Set<String> emptyDomains(Set<String> domains, List<Status> statuses) {
    Set<String> foundDomains = Sets.newHashSet();
    for (Status status : statuses) {
      foundDomains.add(status.getDomain());
    }

    return Sets.difference(domains, foundDomains);
  }

  private Map<String, Account> accountMap(List<Account> accounts) {
    Map<String, Account> accountMap = Maps.newHashMap();
    for (Account account : accounts) {
      accountMap.put(account.getDomain(), account);
    }
    return accountMap;
  }

  private Status defaultStatus(String personId, String domain, Date from) {
    Status status = new Status();
    status.setDomain(domain);
    status.setPersonId(personId);
    status.setStatus("");
    status.setCreated(from);
    status.setPeriod("today");
    return status;
  }

  private Date parseDate(String from, String timezone) throws ParseException {
    if (from.startsWith("/"))
      from = from.substring(1);
    if (from.endsWith("/"))
      from = from.substring(0, from.length());

    urlFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
    Date date = urlFormat.parse(from);
    return date;
  }
}
