package thesmith.eventhorizon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.service.AccountService;

@Controller
@RequestMapping(value = "/accounts")
public class AccountsController {
  @Autowired
  private AccountService service;

  @RequestMapping(value = "/{personId}", method = RequestMethod.GET)
  public String list(@PathVariable("personId") String personId, ModelMap model) {
    model.addAttribute("accounts", service.list(personId));

    return "accounts/list";
  }

  @RequestMapping(value = "/{personId}/{domain}/", method = RequestMethod.GET)
  public String find(@PathVariable("personId") String personId,
      @PathVariable("domain") String domain, ModelMap model) {
    Account account = service.find(personId, domain);
    if (null != account)
      model.addAttribute("account", service.find(personId, domain));
    else
      model.addAttribute("account", new Account());
    
    return "accounts/find";
  }

  @RequestMapping(value = "/{personId}/{domain}/", method = RequestMethod.POST)
  public String update(@PathVariable("personId") String personId,
      @PathVariable("domain") String domain,
      @ModelAttribute("account") Account account, ModelMap model) {
    account.setPersonId(personId);
    account.setDomain(domain);
    service.createOrUpdate(account);

    return "accounts/find";
  }
}
