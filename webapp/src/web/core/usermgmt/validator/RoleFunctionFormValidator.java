package web.core.usermgmt.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import app.core.usermgmt.model.RoleFunction;
import app.core.usermgmt.model.RoleFunctionPK;
import app.core.usermgmt.service.UserMgmtService;
import web.core.usermgmt.model.RoleFunctionForm;

public class RoleFunctionFormValidator implements Validator {

	private UserMgmtService userMgmtService;
	
	public RoleFunctionFormValidator(UserMgmtService userMgmtService) {
		this.userMgmtService = userMgmtService;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return RoleFunctionForm.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "roleFunction.role", "usermgmt.rolefunction.required.role_id");
		ValidationUtils.rejectIfEmpty(errors, "roleFunction.function", "usermgmt.rolefunction.required.function_code");		
		//check for duplicate
		
		RoleFunctionForm form = RoleFunctionForm.class.cast(obj);
		RoleFunction roleFunction = form.getRoleFunction();
		
		RoleFunctionPK roleFunctionPK = new RoleFunctionPK();
		roleFunctionPK.setRoleId(roleFunction.getRole().getId());
		roleFunctionPK.setFunctionCode(roleFunction.getFunction().getCode());

		RoleFunction entity = userMgmtService.getRoleFuncEdit(roleFunctionPK);
		if(entity != null) {
			errors.reject("usermgmt.rolefunction.duplicate");
		}
	}

}
