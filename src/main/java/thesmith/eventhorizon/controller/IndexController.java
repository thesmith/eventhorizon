package thesmith.eventhorizon.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import thesmith.eventhorizon.model.Status;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

@Controller
public class IndexController extends BaseController {
  private static final DateFormat format = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
  private static final DateFormat urlFormat = new SimpleDateFormat("yyyy/MM/dd/kk/mm/ss");

  @RequestMapping(value = "/{personId}/{year}/{month}/{day}/{hour}/{min}/{sec}", method = RequestMethod.GET)
  public String index(@PathVariable("personId") String personId, @PathVariable("year") int year,
      @PathVariable("month") int month, @PathVariable("day") int day, @PathVariable("hour") int hour,
      @PathVariable("min") int min, @PathVariable("sec") int sec, ModelMap model) {

    try {
      Date from = format.parse(String.format("%d/%d/%d %d:%d:%d", year, month, day, hour, min, sec));
      List<String> domains = accountService.domains(personId);
      List<Status> statuses = Lists.newArrayList();
      for (String domain : domains) {
        Status status = statusService.find(personId, domain, from);
        if (null != status)
          statuses.add(status);
      }
      model.addAttribute("statuses", statuses);
      model.addAttribute("personId", personId);
      model.addAttribute("from", from);

    } catch (ParseException e) {
      if (logger.isWarnEnabled())
        logger.warn(e);
      return "redirect:/error";
    }
    return "index/index";
  }

  @RequestMapping(value = "/{personId}/{year}/{month}/{day}/{hour}/{min}/{sec}/{domain}/previous", method = RequestMethod.GET)
  public String previous(@PathVariable("personId") String personId, @PathVariable("year") int year,
      @PathVariable("month") int month, @PathVariable("day") int day, @PathVariable("hour") int hour,
      @PathVariable("min") int min, @PathVariable("sec") int sec, @PathVariable("domain") String domain) {
    try {
      Date from = format.parse(String.format("%d/%d/%d %d:%d:%d", year, month, day, hour, min, sec));
      Status status = statusService.previous(personId, domain, from);
      if (null == status)
        status = statusService.find(personId, domain, from);
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
      Status status = statusService.next(personId, domain, from);
      if (null == status)
        status = statusService.find(personId, domain, from);
      return String.format("redirect:/%s/%s/", personId, urlFormat.format(status.getCreated()));
    } catch (ParseException e) {
      if (logger.isWarnEnabled())
        logger.warn(e);
      return "redirect:/error";
    }
  }

  @RequestMapping(value = "/{personId}", method = RequestMethod.GET)
  public String start(@PathVariable("personId") String personId) {
    return String.format("redirect:/%s/%s/", personId, urlFormat.format(new Date()));
  }

  @RequestMapping(value = "/error", method = RequestMethod.GET)
  public String error() {
    return "error";
  }
}
