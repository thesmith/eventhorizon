package thesmith.eventhorizon.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import thesmith.eventhorizon.model.User;
import thesmith.eventhorizon.service.AccountService;
import thesmith.eventhorizon.service.StatusService;
import thesmith.eventhorizon.service.UserService;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

public class BaseController {
  public static final String COOKIE = "eventhorizon";
  public static final String USERNAME_COOKIE = "eventhorizon-username";
  protected final Log logger = LogFactory.getLog(this.getClass());

  @Autowired
  protected UserService userService;

  @Autowired
  protected AccountService accountService;

  @Autowired
  protected StatusService statusService;
  
  protected Queue queue = QueueFactory.getDefaultQueue();

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
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
  
  protected boolean isProduction() {
    return ("Production".equals(System.getProperty("com.google.appengine.runtime.environment", "")));
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

  public void setQueue(Queue queue) {
    this.queue = queue;
  }
}
