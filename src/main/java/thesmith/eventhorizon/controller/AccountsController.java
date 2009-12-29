package thesmith.eventhorizon.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Status;
import thesmith.eventhorizon.model.User;

@Controller
public class AccountsController extends BaseController {

  @RequestMapping(value = "/accounts", method = RequestMethod.GET)
  public String list(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    User user = this.auth(request, response);
    if (null == user)
      return "redirect:/users/login";

    model.addAttribute("accounts", accountService.list(user.getUsername()));

    return "accounts/list";
  }

  @RequestMapping(value = "/accounts/{domain}", method = RequestMethod.GET)
  public String find(@PathVariable("domain") String domain, ModelMap model, HttpServletRequest request,
      HttpServletResponse response) {
    User user = this.auth(request, response);
    if (null == user)
      return "redirect:/users/login";

    Account account = accountService.find(user.getUsername(), domain);
    if (null != account)
      model.addAttribute("account", account);
    else
      model.addAttribute("account", new Account());

    return "accounts/find";
  }

  @RequestMapping(value = "/accounts/{domain}", method = RequestMethod.POST)
  public String update(@PathVariable("domain") String domain, @ModelAttribute("account") Account account,
      ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    User user = this.auth(request, response);
    if (null == user)
      return "redirect:/users/login";

    account.setPersonId(user.getUsername());
    account.setDomain(domain);
    accountService.createOrUpdate(account);

    return "accounts/find";
  }
  
  @RequestMapping(value = "/accounts/{domain}/status", method = RequestMethod.GET)
  public String statusForm(@PathVariable("domain") String domain, ModelMap model, HttpServletRequest request,
      HttpServletResponse response) {
    User user = this.auth(request, response);
    if (null == user)
      return "redirect:/users/login";
    model.addAttribute("status", new Status());

    return "accounts/status";
  }

  @RequestMapping(value = "/accounts/{domain}/status", method = RequestMethod.POST)
  public String status(@PathVariable("domain") String domain, @ModelAttribute("status") Status status,
      ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    User user = this.auth(request, response);
    if (null == user)
      return "redirect:/users/login";

    status.setPersonId(user.getUsername());
    status.setDomain(domain);
    status.setCreated(new Date());
    statusService.create(status);

    return "redirect:/"+user.getUsername()+"/";
  }
}
