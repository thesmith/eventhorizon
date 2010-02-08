package thesmith.eventhorizon.controller;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.User;
import thesmith.eventhorizon.service.AccountService;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/accounts")
public class AccountsController extends BaseController {

  @RequestMapping(method = RequestMethod.GET)
  public String list(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    User user = this.auth(request, response);
    if (null == user)
      return "redirect:/users/login";

    this.setupAccounts(user, model);
    return "accounts/list";
  }

  @RequestMapping(method = RequestMethod.POST)
  public String update(@ModelAttribute("account") Account account, ModelMap model, HttpServletRequest request,
      HttpServletResponse response) {
    User user = this.auth(request, response);
    if (null == user)
      return "redirect:/users/login";

    account.setPersonId(user.getUsername());
    accountService.create(account);
    queue.add(url("/jobs/accounts/" + account.getPersonId() + "/" + account.getDomain() + "/").param(
        JobsController.PAGE, "1"));

    this.setupAccounts(user, model);
    return "accounts/list";
  }

  private void setupAccounts(User user, ModelMap model) {
    List<String> domains = Lists.newArrayList();
    for (Account account : accountService.listAll(user.getUsername())) {
      if (!AccountService.FREESTYLE_DOMAINS.contains(account.getDomain())) {
        model.addAttribute("account_" + account.getDomain(), account);
        domains.add(account.getDomain());
      }
    }
    model.addAttribute("domains", domains);
  }
}
