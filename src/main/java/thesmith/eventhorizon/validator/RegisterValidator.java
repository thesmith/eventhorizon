package thesmith.eventhorizon.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import thesmith.eventhorizon.model.User;

public class RegisterValidator extends BaseValidator implements Validator {
  public boolean supports(Class<?> clazz) {
    return User.class.equals(clazz);
  }

  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmpty(errors, "username", "username.empty");
    ValidationUtils.rejectIfEmpty(errors, "password", "password.empty");
    User user = (User) target;
    if (null != user.getUsername() && null != user.getPassword()) {
      User foundUser = service.find(user.getUsername());
      if (null != foundUser)
        errors.rejectValue("username", "username.taken");
    }
  }
}
