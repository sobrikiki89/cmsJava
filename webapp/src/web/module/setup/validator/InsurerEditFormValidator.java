package web.module.setup.validator;

import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import web.core.helper.Formatter;
import web.module.setup.model.InsurerForm;

public class InsurerEditFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> arg0) {
		return InsurerForm.class.equals(arg0);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "insurer.name", "setup.insurer.required.name");
		ValidationUtils.rejectIfEmpty(errors, "insurer.contact.email", "error.required", new Object[] { "Email" });

		InsurerForm insurerForm = InsurerForm.class.cast(obj);
		if (insurerForm.getInsurer() != null && insurerForm.getInsurer().getContact() != null
				&& StringUtils.isNotEmpty(insurerForm.getInsurer().getContact().getEmail())) {
			Matcher matcher = Formatter.VALID_EMAIL_ADDRESS_REGEX
					.matcher(insurerForm.getInsurer().getContact().getEmail());
			if (!matcher.find()) {
				errors.reject("setup.insurer.invalidEmailFormat");
			}
		}
	}
}
