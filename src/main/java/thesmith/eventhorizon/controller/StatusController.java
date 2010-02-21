package thesmith.eventhorizon.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import thesmith.eventhorizon.model.Account;
import thesmith.eventhorizon.model.Status;
import thesmith.eventhorizon.model.User;
import thesmith.eventhorizon.service.AccountService;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/status")
public class StatusController extends BaseController {
  private final static DateFormat df = new SimpleDateFormat("yyyy/MM/dd");

  @RequestMapping(method = RequestMethod.GET)
  public String list(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    User user = this.auth(request, response);
    if (null == user)
      return "redirect:/users/login";
    this.statuses(user.getUsername(), model);

    this.setViewer(request, model);
    return "status/list";
  }

  @RequestMapping(method = RequestMethod.POST)
  public String update(@ModelAttribute("status") Status status, ModelMap model, HttpServletRequest request,
      HttpServletResponse response) {
    User user = this.auth(request, response);
    if (null == user)
      return "redirect:/users/login";
    status.setPersonId(user.getUsername());
    try {
      status.setCreated(df.parse(status.getCreated_at()));
    } catch (ParseException e) {
      throw new RuntimeException("Unable to parse created_at: "+status.getCreated_at());
    }
    status.setCreated_at(null);
    
    if (null == status.getTitleUrl() && null != status.getTitle())
      status.setTitleUrl("http://en.wikipedia.org/wiki/" + status.getTitle());

    if (logger.isDebugEnabled())
      logger.debug("Recieved request to create status: " + status);

    statusService.create(status);
    this.statuses(user.getUsername(), model);

    this.setViewer(request, model);
    return "status/list";
  }

  private List<Status> statuses(String personId, ModelMap model) {
    final List<Status> statuses = Lists.newArrayList();

    for (String domain : AccountService.FREESTYLE_DOMAINS) {
      Account account = accountService.account(personId, domain);
      Status status = statusService.find(account, new Date());
      if (null == status) {
        status = new Status();
        status.setDomain(account.getDomain());
        status.setPersonId(personId);
        status.setCreated(new Date());
      }
      model.addAttribute("status_" + account.getDomain(), status);
      if (logger.isDebugEnabled())
        logger.debug("adding status to model: " + status);
    }
    model.addAttribute("userHost", userHost(personId));

    return statuses;
  }
}
