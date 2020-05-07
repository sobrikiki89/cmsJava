package web.core.usermgmt.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import app.core.security.UserPrincipal;
import app.core.utils.MD5Utils;
import web.core.usermgmt.model.ChangePasswordForm;
import web.core.usermgmt.model.UserForm;

public class ChangePasswordFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return UserForm.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "currentPassword", "usermgmt.user.required.currentPassword");
		ValidationUtils.rejectIfEmpty(errors, "password", "usermgmt.user.required.password");

		ChangePasswordForm form = ChangePasswordForm.class.cast(obj);

		// We only check confirm password when password is not empty
		if (StringUtils.isNotBlank(form.getPassword()) && !form.getPassword().equals(form.getConfirmPassword())) {
			errors.reject("usermgmt.user.notmatch.new_password");
		}

		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (!principal.getPassword().equals(MD5Utils.encrypt(principal.getSalt(), form.getCurrentPassword()))) {
			errors.reject("usermgmt.user.notmatch.current_password");
		}
	}
}
