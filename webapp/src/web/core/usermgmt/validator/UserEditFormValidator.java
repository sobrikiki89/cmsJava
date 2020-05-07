package web.core.usermgmt.validator;

import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import web.core.helper.Formatter;
import web.core.usermgmt.model.UserForm;

public class UserEditFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return UserForm.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "user.username", "usermgmt.user.required.username");
		ValidationUtils.rejectIfEmpty(errors, "user.firstName", "usermgmt.user.required.firstname");
		ValidationUtils.rejectIfEmpty(errors, "user.lastName", "usermgmt.user.required.lastname");
		ValidationUtils.rejectIfEmpty(errors, "user.email", "usermgmt.user.required.email");
		ValidationUtils.rejectIfEmpty(errors, "user.pwdExpiryDays", "usermgmt.user.required.password_expire");
		ValidationUtils.rejectIfEmpty(errors, "user.sessionTimeoutMinutes", "usermgmt.user.required.session_timeout");
		ValidationUtils.rejectIfEmpty(errors, "user.maxInvalidLoginCount", "usermgmt.user.required.max_invalid_login");
		ValidationUtils.rejectIfEmpty(errors, "user.invalidLoginCount", "usermgmt.user.required.invalid_login");

		UserForm form = UserForm.class.cast(obj);
		// We only check confirm password when password is not empty
		if (form.getUser().getPassword() != null && !"".equals(form.getUser().getPassword())) {
			// Indicator to check whether need to restore the original password
			// from temp password or not
			form.getUser().setPwdChanged(true);
			if (!form.getUser().getPassword().equals(form.getUser().getConfirmPassword())) {
				errors.reject("usermgmt.user.notmatch.password");
			}
		} else {
			form.getUser().setPwdChanged(false);
		}

		if (form.getRoleIds() == null || form.getRoleIds().size() == 0) {
			errors.reject("usermgmt.user.required.role_assigned");
		}

		if (form.getUser() != null && StringUtils.isNotEmpty(form.getUser().getEmail())) {
			Matcher matcher = Formatter.VALID_EMAIL_ADDRESS_REGEX.matcher(form.getUser().getEmail());
			if (!matcher.find()) {
				errors.reject("usermgmt.user.invalidEmailFormat");
			}
		}
	}
}
