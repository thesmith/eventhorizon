package thesmith.eventhorizon.controller;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Snapshot;
import thesmith.eventhorizon.model.Status;
import thesmith.eventhorizon.model.StatusCreatedSort;
import thesmith.eventhorizon.service.AccountService;
import thesmith.eventhorizon.service.CacheService;
import thesmith.eventhorizon.service.StatusService;
import thesmith.eventhorizon.service.impl.WordrEventServiceImpl;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/jobs")
public class JobsController extends BaseController {
  @Autowired
  private CacheService<Status> cache;

  public static final String PAGE = "page";
  public static final int LIMIT = 20;

  @RequestMapping(value = "/accounts/{personId}/{domain}/latest")
  public String latest(@PathVariable("personId") String personId, @PathVariable("domain") String domain) {
    queue.add(url("/jobs/accounts/" + personId + "/" + domain + "/").param(PAGE, "1"));
    return "jobs/index";
  }

  @RequestMapping(value = "/accounts/{personId}/{domain}/")
  public String page(@PathVariable("personId") String personId, @PathVariable("domain") String domain,
      @RequestParam("page") String page) {
    Account account = accountService.find(personId, domain);
    if (null != account) {
      int p = Integer.parseInt(page);
      List<Status> statuses = statusService.list(account, p);
      if (!statuses.isEmpty()) {
        Collections.sort(statuses, new StatusCreatedSort());
        Date oldest = oldest(statuses);

        Date previousCreated = null;
        if (p == 1)
          previousCreated = new Date();

        for (Status status : statuses) {
          if (null == previousCreated) {
            Status next = statusService.next(account, status.getCreated());
            if (null != next)
              previousCreated = next.getCreated();
          }

          statusService.create(status);
          if (null != cache)
            cache.put(StatusService.CACHE_KEY_PREFIX + status.getId(), status);

          List<Snapshot> snapshots = snapshotService.list(personId, status.getCreated(), previousCreated);
          boolean found = false;
          for (Snapshot snapshot : snapshots) {
            snapshotService.addStatus(snapshot, status);
            if (snapshot.getCreated().equals(status.getCreated()))
              found = true;
          }
          if (!found) {
            List<Account> accounts = accountService.list(personId);
            List<Key> statusIds = Lists.newArrayList();
            for (Account acc : accounts) {
              if (null != acc.getUserId()) {
                if (domain.equals(status.getDomain())) {
                  statusIds.add(status.getId());
                } else {
                  Status s = statusService.find(acc, status.getCreated());
                  if (null != s)
                    statusIds.add(s.getId());
                }
              }
            }
            Snapshot snapshot = new Snapshot();
            snapshot.setPersonId(personId);
            snapshot.setCreated(status.getCreated());
            snapshot.setStatusIds(statusIds);
            snapshotService.create(snapshot);
          }
          previousCreated = status.getCreated();
        }

        Date d = new Date(oldest.getTime() - 1L);
        Status status = statusService.find(account, d);
        if (null == status) {
          p = p + 1;
          if (AccountService.DOMAIN.wordr.toString().equals(domain))
            p = Integer.valueOf(statuses.get(statuses.size() - 1).getTitleUrl().replace(
                WordrEventServiceImpl.STATUS_URL, ""));
          queue.add(url("/jobs/accounts/" + personId + "/" + domain + "/").param(PAGE, String.valueOf(p)));
        }
      }
    }
    return "jobs/index";
  }

  private Date oldest(List<Status> statuses) {
    for (int i = (statuses.size() - 1); i >= 0; i--) {
      Date d = statuses.get(i).getCreated();
      if (null != d)
        return d;
    }
    return new Date();
  }

  @RequestMapping(value = "/accounts/process")
  public String process() {
    List<Account> accounts = accountService.toProcess(LIMIT);
    if (logger.isInfoEnabled())
      logger.info("Retrieved " + accounts.size() + " accounts to be processed");

    for (Account account : accounts) {
      queue.add(url("/jobs/accounts/" + account.getPersonId() + "/" + account.getDomain() + "/").param(PAGE, "1"));
      account.setProcessed(new Date());
      accountService.update(account);
    }
    return "jobs/index";
  }
}
