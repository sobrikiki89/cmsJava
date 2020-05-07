package web.core.usermgmt.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import app.core.usermgmt.model.Role;
import app.core.usermgmt.service.UserMgmtService;
import web.core.usermgmt.model.RoleForm;

public class RoleEditValidator implements Validator {
	
	private UserMgmtService userMgmtService;
	
	public RoleEditValidator(UserMgmtService userMgmtService) {
		this.userMgmtService = userMgmtService;
	}
	
	@Override
	public boolean supports(Class<?> arg0) {		
		return RoleForm.class.equals(arg0);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		
		RoleForm roleForm = RoleForm.class.cast(obj);
		
		ValidationUtils.rejectIfEmpty(errors, "role.description", "usermgmt.role.required.description");
		ValidationUtils.rejectIfEmpty(errors, "role.sortOrder", "usermgmt.role.required.sortOrder");
		
		Role role = userMgmtService.getUniqueSortOrder(roleForm.getRole().getSortOrder());
		
		if (role != null && role.getId() != roleForm.getRole().getId()){
			errors.reject("error.duplicate", new Object[] { "Sort Order", roleForm.getRole().getSortOrder() }, "error");
		}
	}

}
