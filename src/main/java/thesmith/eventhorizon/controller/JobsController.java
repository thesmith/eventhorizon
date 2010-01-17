package thesmith.eventhorizon.controller;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Status;

@Controller
@RequestMapping(value = "/jobs")
public class JobsController extends BaseController {
  public static final String PAGE = "page";
  @RequestMapping(value = "/accounts/{personId}/{domain}/latest")
  public String latest(@PathVariable("personId") String personId, @PathVariable("domain") String domain) {
    queue.add(url("/jobs/accounts/"+personId+"/"+domain+"/").param(PAGE, "1"));
    return "jobs/index";
  }

  @RequestMapping(value = "/accounts/{personId}/{domain}/")
  public String page(@PathVariable("personId") String personId, @PathVariable("domain") String domain,
      @RequestParam("page") String page) {
    Account account = accountService.find(personId, domain);
    if (null != account) {
      Date oldest = new Date();
      int p = Integer.parseInt(page);
      List<Status> statuses = statusService.list(account, p);
      for (Status status : statuses) {
        statusService.create(status);
        if (null != status.getCreated() && oldest.after(status.getCreated()))
          oldest = status.getCreated();
      }
      
      if (statuses.size() > 0) {
        Date d = new Date(oldest.getTime()-1L);
        Status status = statusService.find(personId, domain, d);
        if (null == status) {
          queue.add(url("/jobs/accounts/"+personId+"/"+domain+"/").param(PAGE, String.valueOf(p+1)));
        }
      }
    }
    return "jobs/index";
  }

  @RequestMapping(value = "/accounts/{personId}/{domain}/all")
  public String all(@PathVariable("personId") String personId, @PathVariable("domain") String domain) {
    Account account = accountService.find(personId, domain);
    if (null != account) {
      Date oldest = new Date();
      int page = 1;

      while (true) {
        List<Status> statuses = statusService.list(account, page);
        if (1 > statuses.size()) {
          if (logger.isInfoEnabled())
            logger
                .info("Retrieved 0 statuses for " + account.getDomain() + ":" + account.getUserId() + " page " + page);
          break;
        }

        Date currentOldest = new Date();
        for (Status status : statuses) {
          statusService.create(status);
          if (null != status.getCreated() && status.getCreated().before(currentOldest))
            currentOldest = status.getCreated();
        }

        if (!currentOldest.before(oldest)) {
          if (logger.isInfoEnabled())
            logger.info("currentOldest: " + currentOldest + " not before oldest: " + oldest);
          break;
        }
        oldest = currentOldest;
        page++;
      }
    }
    return "jobs/index";
  }
}
