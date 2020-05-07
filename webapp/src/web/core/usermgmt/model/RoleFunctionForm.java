package web.core.usermgmt.model;

import java.util.List;

import app.core.usermgmt.model.Function;
import app.core.usermgmt.model.Role;
import app.core.usermgmt.model.RoleFunction;
import web.core.model.AbstractForm;

public class RoleFunctionForm extends AbstractForm {

	private static final long serialVersionUID = 1L;

	private RoleFunction roleFunction;
	private List<Role> roles;
	private List<Function> functions;

	public RoleFunctionForm() {
	}

	public RoleFunction getRoleFunction() {
		return roleFunction;
	}

	public void setRoleFunction(RoleFunction roleFunction) {
		this.roleFunction = roleFunction;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<Function> getFunctions() {
		return functions;
	}

	public void setFunctions(List<Function> functions) {
		this.functions = functions;
	}
}
