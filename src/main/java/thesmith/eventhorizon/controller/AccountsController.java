package thesmith.eventhorizon.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
  public String list(@PathVariable("personId") String personId, Model model) {
    List<Account> accounts = service.list(personId);
    model.addAttribute("accounts-size", accounts.size());
    model.addAttribute("accounts", service.list(personId));
    
    return "accounts/list";
  }
}
