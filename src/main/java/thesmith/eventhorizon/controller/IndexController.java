package thesmith.eventhorizon.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Snapshot;
import thesmith.eventhorizon.model.Status;
import thesmith.eventhorizon.service.CacheService;
import thesmith.eventhorizon.service.StatusService;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.com.google.common.collect.Maps;

@Controller
public class IndexController extends BaseController {
  public static final String FROM = "from";
  private static final DateFormat format = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
  private static final DateFormat urlFormat = new SimpleDateFormat("yyyy/MM/dd/kk/mm/ss");
  
  @Autowired
  private CacheService<Status> cache;

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
      @RequestParam("from") String from, ModelMap model) {
    try {
      Account account = accountService.account(personId, domain);
      Status status = statusService.previous(account, this.parseDate(from));
      if (null != status)
        this.setModel(personId, status.getCreated(), model);
      else
        this.setModel(personId, this.parseDate(from), model);

    } catch (ParseException e) {
      if (logger.isWarnEnabled())
        logger.warn(e);
      return "redirect:/error";
    }
    return "index/index";
  }

  @RequestMapping(value = "/{personId}/{domain}/next", method = RequestMethod.GET)
  public String next(@PathVariable("personId") String personId, @PathVariable("domain") String domain,
      @RequestParam("from") String from, ModelMap model) {
    try {
      Account account = accountService.account(personId, domain);
      Status status = statusService.next(account, this.parseDate(from));
      if (null != status)
        this.setModel(personId, status.getCreated(), model);
      else
        this.setModel(personId, this.parseDate(from), model);

    } catch (ParseException e) {
      if (logger.isWarnEnabled())
        logger.warn(e);
      return "redirect:/error";
    }
    return "index/index";
  }

  @RequestMapping(value = "/{personId}/now", method = RequestMethod.GET)
  public String now(@PathVariable("personId") String personId, @RequestParam("from") String from, ModelMap model,
      HttpServletRequest request) {
    if (null != from && from.length() > 0) {
      try {
        this.setModel(personId, this.parseDate(from), model);

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
        this.setModel(personId, null, model);

        this.setViewer(request, model);
        return "index/index";
      }
    } catch (MalformedURLException e) {
      if (logger.isInfoEnabled())
        logger.info("Unable to decode url from " + request.getRequestURL().toString());
    }

    return "index/front";
  }

  @RequestMapping(value = "/error", method = RequestMethod.GET)
  public String error() {
    return "error";
  }

  private void setModel(String personId, Date from, ModelMap model) {
    List<Status> statuses = Lists.newArrayList();
    Map<String, Account> accounts = accountMap( accountService.listAll(personId) );
    if (null != from) {
      Snapshot snapshot = snapshotService.find(personId, from);
      Map<String, Key> cacheKeys = cacheKeys(snapshot.getStatusIds());
      Map<String, Status> cachedStatuses = Maps.newHashMap();
      if (null != cache)
        cachedStatuses = cache.getAll(cacheKeys.keySet());
      statuses.addAll(cachedStatuses.values());

      for (Key id: missingKeys(cacheKeys, cachedStatuses.keySet())) {
        Status status = statusService.find(id, from, accounts);
        if (null != status) {
          statuses.add(status);
          if (null != cache)
            cache.put(StatusService.CACHE_KEY_PREFIX+status.getId(), status);
        }
      }
    } else {
      from = new Date();
      for (Account account : accounts.values()) {
        statuses.add(defaultStatus(personId, account.getDomain(), from));
      }
      model.addAttribute("refresh", true);
    }
    model.addAttribute("statuses", statuses);
    model.addAttribute("personId", personId);
    model.addAttribute("from", from);
    model.addAttribute("secureHost", secureHost());

    if (logger.isDebugEnabled())
      logger.debug("Setting the model: " + model);
  }
  
  private Map<String, Key> cacheKeys(List<Key> keys) {
    Map<String, Key> cacheKeys = Maps.newHashMap();
    for (Key key: keys) {
      cacheKeys.put(StatusService.CACHE_KEY_PREFIX+key, key);
    }
    return cacheKeys;
  }
  
  private List<Key> missingKeys(Map<String, Key> cacheKeys, Collection<String> foundKeys) {
    List<Key> missingKeys = Lists.newArrayList();
    for(String key: cacheKeys.keySet()) {
      if (! foundKeys.contains(key))
        missingKeys.add(cacheKeys.get(key));
    }
    return missingKeys;
  }
  
  private Map<String, Account> accountMap(List<Account> accounts) {
    Map<String, Account> accountMap = Maps.newHashMap();
    for (Account account: accounts) {
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

  private Date parseDate(String from) throws ParseException {
    if (from.startsWith("/"))
      from = from.substring(1);
    if (from.endsWith("/"))
      from = from.substring(0, from.length());

    return urlFormat.parse(from);
  }
}
