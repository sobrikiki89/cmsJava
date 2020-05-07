package web.module.setup.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import module.setup.model.LossType;
import module.setup.service.LossTypeService;
import web.module.setup.model.LossTypeForm;

public class LossTypeEditFormValidator implements Validator {

	private LossTypeService lossTypeService;

	public LossTypeEditFormValidator(LossTypeService lossTypeService) {
		this.lossTypeService = lossTypeService;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return LossTypeForm.class.equals(arg0);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		LossTypeForm lossTypeForm = LossTypeForm.class.cast(obj);
		
		ValidationUtils.rejectIfEmpty(errors, "lossType.name", "setup.losstype.required.name");
		ValidationUtils.rejectIfEmpty(errors, "lossType.sortOrder", "setup.losstype.required.sortOrder");
		
		LossType lossType = lossTypeService.getUniqueSortOrder(lossTypeForm.getLossType().getSortOrder());
		
		if (lossType != null && !lossType.getCode().equalsIgnoreCase(lossTypeForm.getLossType().getCode())){
			errors.reject("error.exist", new Object[] { "Sort Order", lossTypeForm.getLossType().getSortOrder(), "sort order" }, "error");
		}
	}
}
