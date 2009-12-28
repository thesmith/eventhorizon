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
  public String loginForm(ModelMap model) {
    model.addAttribute("user", new User());

    return "users/login";
  }

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public String login(@ModelAttribute("user") User user, BindingResult result, HttpServletResponse response) {
    loginValidator.validate(user, result);
    if (result.hasErrors())
      return "users/login";

    this.setCookie(response, user);
    return "redirect:/" + user.getUsername() + "/";
  }

  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public String logout(HttpServletResponse response) {
    Cookie cookie = new Cookie(COOKIE, "empty");
    cookie.setMaxAge(0);
    response.addCookie(cookie);

    return "redirect:/users/login";
  }

  @RequestMapping(value = "/register", method = RequestMethod.GET)
  public String registerForm(ModelMap model) {
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
    return "redirect:/" + user.getUsername() + "/";
  }

  @RequestMapping(value = "/profile", method = RequestMethod.GET)
  public String profile(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    User user = this.auth(request, response);
    if (null == user)
      return "redirect:/users/login";
    model.addAttribute("user", user);

    return "users/profile";
  }

  private void setCookie(HttpServletResponse response, User user) {
    Cookie cookie = new Cookie(COOKIE, userService.token(user));
    cookie.setPath("/");
    cookie.setMaxAge(60 * 60 * 24 * 30);
    response.addCookie(cookie);
  }
  
  public void setLoginValidator(LoginValidator loginValidator) {
    this.loginValidator = loginValidator;
  }
  
  public void setRegisterValidator(RegisterValidator registerValidator) {
    this.registerValidator = registerValidator;
  }
}
