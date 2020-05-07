package web.module.setup.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import module.setup.service.InsuranceClassService;
import web.module.setup.model.InsuranceClassForm;

public class InsuranceClassNewFormValidator implements Validator {

	private InsuranceClassService insuranceClassService;

	public InsuranceClassNewFormValidator(InsuranceClassService insuranceClassService) {
		this.insuranceClassService = insuranceClassService;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return InsuranceClassForm.class.equals(arg0);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "insuranceClass.code", "setup.insuranceclass.required.code");
		ValidationUtils.rejectIfEmpty(errors, "insuranceClass.name", "setup.insuranceclass.required.name");
		ValidationUtils.rejectIfEmpty(errors, "insuranceClass.category.name", "setup.insuranceclass.required.category");
		ValidationUtils.rejectIfEmpty(errors, "insuranceClass.sortOrder", "setup.insuranceclass.required.sortOrder");
		InsuranceClassForm insuranceClassForm = InsuranceClassForm.class.cast(obj);
		if (insuranceClassService.getInsuranceClass(insuranceClassForm.getInsuranceClass().getCode()) != null) {
			errors.reject("setup.insuranceclass.code_exists",
					new Object[] { insuranceClassForm.getInsuranceClass().getCode() }, "error");
		}
		if (insuranceClassService.getUniqueSortOrder(insuranceClassForm.getInsuranceClass().getSortOrder()) != null) {
			errors.reject("error.exist", new Object[] { "Sort Order", insuranceClassForm.getInsuranceClass().getSortOrder(), "sort order" }, "error");
		}
	}
}
