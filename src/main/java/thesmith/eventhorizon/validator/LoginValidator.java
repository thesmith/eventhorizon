package thesmith.eventhorizon.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import thesmith.eventhorizon.model.User;

public class LoginValidator extends BaseValidator implements Validator {
  public boolean supports(Class<?> clazz) {
    return User.class.equals(clazz);
  }

  public void validate(Object target, Errors errors) {
    ValidationUtils.rejectIfEmpty(errors, "username", "username.empty");
    ValidationUtils.rejectIfEmpty(errors, "password", "password.empty");
    User user = (User) target;
    if (null != user.getUsername() && null != user.getPassword()) {
      try {
        user = service.authn(user);
      } catch (Exception e) {
        errors.rejectValue("password", "password.invalid");
      }
    }
  }
}
