package thesmith.eventhorizon.controller;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Snapshot;
import thesmith.eventhorizon.model.Status;
import thesmith.eventhorizon.model.StatusCreatedSort;
import thesmith.eventhorizon.service.AccountService;
import thesmith.eventhorizon.service.impl.WordrEventServiceImpl;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/jobs")
public class JobsController extends BaseController {
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
    List<Account> accounts = accountService.list(personId);
    Account account = accountService.find(personId, domain);
    if (null != account) {
      int p = Integer.parseInt(page);
      List<Status> statuses = statusService.list(account, p);
      if (!statuses.isEmpty()) {
        Collections.sort(statuses, new StatusCreatedSort());
        Date oldest = oldest(statuses);

        Date nextCreated = null;
        if (p == 1)
          nextCreated = new Date();

        for (Status status: statuses) {
          if (null == nextCreated) {
            Status next = statusService.next(account, status.getCreated());
            if (null != next)
              nextCreated = next.getCreated();
          }
          Date previousCreated = null;
          Status previous = statusService.previous(account, status.getCreated());
          if (null != previous)
            previousCreated = new Date(previous.getCreated().getTime() + 1L);

          statusService.create(status);

          boolean found = false;
          if (null != previousCreated) {
            List<Snapshot> snapshots = snapshotService.list(personId, previousCreated, nextCreated);
            for (Snapshot snapshot: snapshots) {
              snapshotService.addStatus(snapshot, status);
              if (snapshot.getCreated().equals(status.getCreated()))
                found = true;
            }
          }

          if (!found)
            createSnapshot(status, accounts);
          nextCreated = status.getCreated();
        }

        triggerQueue(p, statuses, account, oldest);
      }
    }
    return "jobs/index";
  }

  private void createSnapshot(Status status, List<Account> accounts) {
    List<Key> statusIds = Lists.newArrayList();
    List<String> domains = Lists.newArrayList();
    for (Account acc: accounts) {
      if (null != acc.getUserId()) {
        if (acc.getDomain().equals(status.getDomain())) {
          statusIds.add(status.getId());
          domains.add(status.getDomain());
        } else {
          Status s = statusService.find(acc, status.getCreated());
          if (null != s) {
            statusIds.add(s.getId());
            domains.add(s.getDomain());
          }
        }
      }
    }
    Snapshot snapshot = new Snapshot();
    snapshot.setPersonId(status.getPersonId());
    snapshot.setCreated(status.getCreated());
    snapshot.setStatusIds(statusIds);
    snapshot.setDomains(domains);
    snapshotService.create(snapshot);
  }

  private void triggerQueue(int p, List<Status> statuses, Account account, Date oldest) {
    Date d = new Date(oldest.getTime() - 1L);
    Status status = statusService.find(account, d);
    if (null == status) {
      p = p + 1;
      if (AccountService.DOMAIN.wordr.toString().equals(account.getDomain()))
        p = Integer.valueOf(statuses.get(statuses.size() - 1).getTitleUrl().replace(WordrEventServiceImpl.STATUS_URL,
            ""));
      queue.add(url("/jobs/accounts/" + account.getPersonId() + "/" + account.getDomain() + "/").param(PAGE,
          String.valueOf(p)));
    }
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

    for (Account account: accounts) {
      queue.add(url("/jobs/accounts/" + account.getPersonId() + "/" + account.getDomain() + "/").param(PAGE, "1"));
      account.setProcessed(new Date());
      accountService.update(account);
    }
    return "jobs/index";
  }
}
