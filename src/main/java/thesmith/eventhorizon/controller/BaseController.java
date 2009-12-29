package thesmith.eventhorizon.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import thesmith.eventhorizon.model.User;
import thesmith.eventhorizon.service.AccountService;
import thesmith.eventhorizon.service.StatusService;
import thesmith.eventhorizon.service.UserService;

public class BaseController {
  public static final String COOKIE = "eventhorizon";

  @Autowired
  protected UserService userService;

  @Autowired
  protected AccountService accountService;
  
  @Autowired
  protected StatusService statusService;
  
  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
  }

  protected User auth(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();
    if (null != cookies) {
      for (Cookie cookie : cookies) {
        if (COOKIE.equalsIgnoreCase(cookie.getName())) {
          User user = userService.authn(cookie.getValue());
          if (null != user)
            return user;
          
          cookie.setValue("");
          cookie.setMaxAge(0);
          cookie.setPath("/");
          response.addCookie(cookie);
        }
      }
    }

    return null;
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
}
