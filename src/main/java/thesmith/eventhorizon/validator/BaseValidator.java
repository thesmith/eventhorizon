package thesmith.eventhorizon.validator;

import org.springframework.beans.factory.annotation.Autowired;

import thesmith.eventhorizon.service.UserService;

public abstract class BaseValidator {
  @Autowired
  protected UserService service;
  
  public void setUserService(UserService userService) {
    this.service = userService;
  }
}
