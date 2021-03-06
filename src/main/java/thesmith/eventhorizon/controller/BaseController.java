package thesmith.eventhorizon.controller;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Snapshot;
import thesmith.eventhorizon.model.Status;
import thesmith.eventhorizon.model.User;
import thesmith.eventhorizon.service.AccountService;
import thesmith.eventhorizon.service.SnapshotService;
import thesmith.eventhorizon.service.SocialGraphApiService;
import thesmith.eventhorizon.service.StatusService;
import thesmith.eventhorizon.service.UserService;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

public class BaseController {
  public static final String VIEWER = "viewer";
  public static final String COOKIE = "eventhorizon";
  public static final String USERNAME_COOKIE = "eventhorizon-username";
  public static final String HOST_POSTFIX = ".eventhorizon.me";
  public static final String SECURE_HOST = "https://event-horizon.appspot.com";
  public static final String REDIRECT = "redirect:";
  protected final Log logger = LogFactory.getLog(this.getClass());

  @Autowired
  protected SocialGraphApiService socialGraphApi;

  @Autowired
  protected UserService userService;

  @Autowired
  protected AccountService accountService;

  @Autowired
  protected StatusService statusService;

  @Autowired
  protected SnapshotService snapshotService;

  protected Queue queue = QueueFactory.getDefaultQueue();

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
  }

  protected Date createStatus(Date nextCreated, Status status, List<Account> accounts) {
    Account account = getAccount(accounts, status);
    if (null == nextCreated) {
      Status next = statusService.next(account, status.getCreated());
      if (null != next)
        nextCreated = next.getCreated();
      else
        nextCreated = new Date();
    }
    Date previousCreated = status.getCreated();
    Status previous = statusService.previous(account, status.getCreated());
    if (null != previous)
      previousCreated = new Date(previous.getCreated().getTime() + 1L);

    if (statusService.create(status)) {
      boolean found = false;
      int size = 0;
      if (null != previousCreated) {
        List<Snapshot> snapshots = snapshotService.list(status.getPersonId(), previousCreated, nextCreated, 0);
        size = snapshots.size();
        for (Snapshot snapshot : snapshots) {
          snapshotService.addStatus(snapshot, status);
          if (snapshot.getCreated().equals(status.getCreated()))
            found = true;
        }
      }

      if (!found)
        createSnapshot(status, accounts);

      if (size >= SnapshotService.MAX) {
        queue.add(url("/jobs/snapshots/" + account.getPersonId() + "/" + account.getDomain() + "/create").param(
            "created", String.valueOf(status.getCreated().getTime())).param("previous",
            String.valueOf(previousCreated.getTime())).param("next", String.valueOf(nextCreated.getTime())).param(
            "page", String.valueOf(1)));
      }
    }

    nextCreated = status.getCreated();
    return nextCreated;
  }

  private Account getAccount(List<Account> accounts, Status status) {
    for (Account account : accounts) {
      if (account.getDomain().equals(status.getDomain()))
        return account;
    }
    return null;
  }

  private void createSnapshot(Status status, List<Account> accounts) {
    List<Key> statusIds = Lists.newArrayList();
    List<String> domains = Lists.newArrayList();
    for (Account acc : accounts) {
      if (null != acc.getUserId()) {
        if (acc.getDomain().equals(status.getDomain()) && null != status && null != status.getId()) {
          statusIds.add(status.getId());
          domains.add(status.getDomain());
        } else {
          Status s = statusService.find(acc, status.getCreated());
          if (null != s && null != s.getId()) {
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

  protected User auth(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();
    if (null != cookies) {
      for (Cookie cookie : cookies) {
        if (COOKIE.equalsIgnoreCase(cookie.getName())) {
          if (logger.isDebugEnabled())
            logger.debug("Authenticating cookie: " + cookie.getValue());

          User user = userService.authn(cookie.getValue());
          if (null != user) {
            if (logger.isDebugEnabled())
              logger.debug("Cookie authenticated as user: " + user.getUsername());
            return user;
          }
        }
      }
    }

    return null;
  }

  protected void setViewer(HttpServletRequest request, ModelMap model) {
    Cookie[] cookies = request.getCookies();
    if (null != cookies) {
      for (Cookie cookie : cookies) {
        if (BaseController.USERNAME_COOKIE.equalsIgnoreCase(cookie.getName())) {
          model.addAttribute(BaseController.VIEWER, cookie.getValue());
          model.addAttribute("userHost", userHost(cookie.getValue()));
        }
      }
    }
  }

  protected String redirectIndex(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (null != cookies) {
      for (Cookie cookie : cookies) {
        if (BaseController.USERNAME_COOKIE.equalsIgnoreCase(cookie.getName())) {
          return "redirect:" + userHost(cookie.getValue());
        }
      }
    }
    return null;
  }

  protected boolean isProduction() {
    return ("Production".equals(System.getProperty("com.google.appengine.runtime.environment", "")));
  }

  protected String secureHost() {
    if (isProduction())
      return SECURE_HOST;
    return "";
  }

  protected void setUserCookie(HttpServletResponse response, String personId) {
    Cookie username = new Cookie(USERNAME_COOKIE, personId);
    username.setPath("/");
    username.setMaxAge(60 * 60 * 24 * 30);
    if (isProduction())
      username.setDomain(HOST_POSTFIX);
    response.addCookie(username);
  }

  protected void unsetCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie(COOKIE, "empty");
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);

    Cookie username = new Cookie(USERNAME_COOKIE, "empty");
    username.setMaxAge(0);
    username.setPath("/");
    response.addCookie(username);

    Cookie u = new Cookie(USERNAME_COOKIE, "empty");
    u.setMaxAge(0);
    u.setPath("/");
    if (isProduction())
      u.setDomain(HOST_POSTFIX);
    response.addCookie(u);
  }

  protected String userHost(String personId) {
    if (isProduction())
      return "http://" + personId + HOST_POSTFIX;
    return "/" + personId;
  }

  protected String authUrl(String personId, String ptrt) {
    if (null == ptrt) {
      if (isProduction())
        return "http://" + personId + HOST_POSTFIX + AuthController.AUTH_URL + "?ptrt=";
      return AuthController.AUTH_URL + "?ptrt=/" + personId;
    } else {
      if (isProduction())
        return "http://" + personId + HOST_POSTFIX + AuthController.AUTH_URL + "?ptrt=" + ptrt;
      return AuthController.AUTH_URL + "?ptrt=" + ptrt;
    }
  }

  protected String unauthUrl(String ptrt) {
    if (null == ptrt) {
      if (isProduction())
        return "http://www" + HOST_POSTFIX + AuthController.UNAUTH_URL + "?ptrt=";
      return AuthController.UNAUTH_URL + "?ptrt=/";
    } else {
      if (isProduction())
        return "http://www" + HOST_POSTFIX + AuthController.UNAUTH_URL + "?ptrt=" + ptrt;
      return AuthController.UNAUTH_URL + "?ptrt=" + ptrt;
    }
  }

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public void setAccountService(AccountService accountService) {
    this.accountService = accountService;
  }

  public void setStatusService(StatusService statusService) {
    this.statusService = statusService;
  }

  public void setSnapshotService(SnapshotService snapshotService) {
    this.snapshotService = snapshotService;
  }

  public void setSocialGraphApiService(SocialGraphApiService socialGraphApi) {
    this.socialGraphApi = socialGraphApi;
  }

  public void setQueue(Queue queue) {
    this.queue = queue;
  }
}
