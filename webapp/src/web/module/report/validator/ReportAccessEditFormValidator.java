package web.module.report.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import web.module.report.model.ReportAccessForm;

public class ReportAccessEditFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return ReportAccessForm.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "accessControl.definition.category.code", "report.required.category");
		ValidationUtils.rejectIfEmpty(errors, "accessControl.definition.id", "report.required.definition");
		ValidationUtils.rejectIfEmpty(errors, "accessControl.role.id", "report.required.role");
	}
}
