package web.module.setup.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import module.setup.service.LossTypeService;
import web.module.setup.model.LossTypeForm;

public class LossTypeNewFormValidator implements Validator {

	private LossTypeService lossTypeService;

	public LossTypeNewFormValidator(LossTypeService lossTypeService) {
		this.lossTypeService = lossTypeService;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return LossTypeForm.class.equals(arg0);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "lossType.code", "setup.losstype.required.code");
		ValidationUtils.rejectIfEmpty(errors, "lossType.name", "setup.losstype.required.name");
		ValidationUtils.rejectIfEmpty(errors, "lossType.sortOrder", "setup.losstype.required.sortOrder");
		LossTypeForm lossTypeForm = LossTypeForm.class.cast(obj);
		if (lossTypeService.getLossType(lossTypeForm.getLossType().getCode()) != null) {
			errors.reject("setup.losstype.code_exists", new Object[] { lossTypeForm.getLossType().getCode() }, "error");
		}
		if (lossTypeService.getUniqueSortOrder(lossTypeForm.getLossType().getSortOrder()) != null){
			errors.reject("error.exist", new Object[] { "Sort Order", lossTypeForm.getLossType().getSortOrder(), "sort order" }, "error");
		}
	}
}
