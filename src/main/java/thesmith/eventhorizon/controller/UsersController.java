package thesmith.eventhorizon.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import thesmith.eventhorizon.model.User;
import thesmith.eventhorizon.validator.LoginValidator;
import thesmith.eventhorizon.validator.RegisterValidator;

@Controller
@RequestMapping(value = "/users")
public class UsersController extends BaseController {
  @Autowired
  private LoginValidator loginValidator;
  @Autowired
  private RegisterValidator registerValidator;

  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String loginForm(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    User user = this.auth(request, response);
    if (null != user)
      return "redirect:/users/logout";
    model.addAttribute("user", new User());

    return "users/login";
  }

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public String login(@ModelAttribute("user") User user, BindingResult result, HttpServletResponse response) {
    loginValidator.validate(user, result);
    if (result.hasErrors())
      return "users/login";

    this.setCookie(response, userService.find(user.getUsername()));
    return REDIRECT + this.authUrl(user.getUsername(), null);
  }

  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public String logout(HttpServletResponse response) {
    Cookie cookie = new Cookie(COOKIE, "empty");
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);

    Cookie username = new Cookie(USERNAME_COOKIE, "empty");
    username.setMaxAge(0);
    username.setPath("/");
    response.addCookie(username);

    return REDIRECT + "/users/login";
  }

  @RequestMapping(value = "/register", method = RequestMethod.GET)
  public String registerForm(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    User user = this.auth(request, response);
    if (null != user)
      return "redirect:/users/logout";
    model.addAttribute("user", new User());

    return "users/register";
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public String register(@ModelAttribute("user") User user, BindingResult result, HttpServletResponse response) {
    registerValidator.validate(user, result);
    if (result.hasErrors())
      return "users/register";

    userService.create(user);
    this.setCookie(response, user);
    String ptrt = (isProduction() ? "https://event-horizon.appspot.com/accounts" : "/accounts");
    return REDIRECT + this.authUrl(user.getUsername(), ptrt);
  }

  @RequestMapping(value = "/gravatar", method = RequestMethod.GET)
  public String gravatar(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
    User user = this.auth(request, response);
    if (null == user)
      return "redirect:/users/login";

    model.addAttribute("gravatar", userService.getGravatar(user.getUsername()));
    this.setViewer(request, model);
    model.addAttribute("userHost", userHost(user.getUsername()));
    return "users/gravatar";
  }

  @RequestMapping(value = "/gravatar", method = RequestMethod.POST)
  public String setGravatar(@RequestParam("email") String email, HttpServletRequest request,
      HttpServletResponse response, ModelMap model) {
    User user = this.auth(request, response);
    if (null == user)
      return "redirect:/users/login";

    user.setEmail(email);
    userService.update(user);
    model.addAttribute("gravatar", userService.getGravatar(user.getUsername()));
    this.setViewer(request, model);
    model.addAttribute("userHost", userHost(user.getUsername()));
    return "users/gravatar";
  }

  private void setCookie(HttpServletResponse response, User user) {
    Cookie cookie = new Cookie(COOKIE, userService.token(user));
    cookie.setPath("/");
    cookie.setMaxAge(60 * 60 * 24 * 30);
    if (isProduction())
      cookie.setSecure(true);
    if (logger.isInfoEnabled())
      logger.info("Setting cookie for user " + user.getUsername() + ": " + cookie.getValue());
    response.addCookie(cookie);

    this.setUserCookie(response, user.getUsername());
  }

  public void setLoginValidator(LoginValidator loginValidator) {
    this.loginValidator = loginValidator;
  }

  public void setRegisterValidator(RegisterValidator registerValidator) {
    this.registerValidator = registerValidator;
  }
}
