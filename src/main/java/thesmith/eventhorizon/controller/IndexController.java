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
      e.printStackTrace();
      return "redirect:/error";
    }
    return "index/index";
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
