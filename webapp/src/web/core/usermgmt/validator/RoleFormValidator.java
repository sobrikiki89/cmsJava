package web.core.usermgmt.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import app.core.usermgmt.model.Role;
import app.core.usermgmt.service.UserMgmtService;
import web.core.usermgmt.model.RoleForm;

public class RoleFormValidator implements Validator {
	
	private UserMgmtService userMgmtService;
	
	public RoleFormValidator(UserMgmtService userMgmtService) {
		this.userMgmtService = userMgmtService;
	}

	@Override
	public boolean supports(Class<?> arg0) {		
		return RoleForm.class.equals(arg0);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "role.name", "usermgmt.role.required.name");
		ValidationUtils.rejectIfEmpty(errors, "role.description", "usermgmt.role.required.description");
		ValidationUtils.rejectIfEmpty(errors, "role.sortOrder", "usermgmt.role.required.sortOrder");
		
		RoleForm form = RoleForm.class.cast(obj);
		if(form.getRole().getName() != null){
			if(userMgmtService.getRoleByName(form.getRole().getName()) != null){
				errors.reject("usermgmt.role.name_exists");
			}
		}
		
		Role role = userMgmtService.getUniqueSortOrder(form.getRole().getSortOrder());
		
		if (role != null){
			errors.reject("error.duplicate", new Object[] { "Sort Order", form.getRole().getSortOrder() }, "error");
		}
	}

}
