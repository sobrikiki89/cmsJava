package web.module.setup.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import module.setup.model.InsuranceClass;
import module.setup.service.InsuranceClassService;
import web.module.setup.model.InsuranceClassForm;

public class InsuranceClassEditFormValidator implements Validator {

	private InsuranceClassService insuranceClassService;

	public InsuranceClassEditFormValidator(InsuranceClassService insuranceClassService) {
		this.insuranceClassService = insuranceClassService;
	}
	
	@Override
	public boolean supports(Class<?> arg0) {
		return InsuranceClassForm.class.equals(arg0);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		InsuranceClassForm insuranceClassForm = InsuranceClassForm.class.cast(obj);
		
		ValidationUtils.rejectIfEmpty(errors, "insuranceClass.name", "setup.insuranceclass.required.name");
		ValidationUtils.rejectIfEmpty(errors, "insuranceClass.sortOrder", "setup.insuranceclass.required.sortOrder");
		ValidationUtils.rejectIfEmpty(errors, "categoryCode", "setup.insuranceclass.required.category");
		
		InsuranceClass insClass = insuranceClassService.getUniqueSortOrder(insuranceClassForm.getInsuranceClass().getSortOrder());
		
		if (insClass != null && !insuranceClassForm.getInsuranceClass().getCode().equalsIgnoreCase(insClass.getCode())) {
			errors.reject("error.exist", new Object[] { "Sort Order", insuranceClassForm.getInsuranceClass().getSortOrder(), "sort order" }, "error");
		}
		
	}
}
