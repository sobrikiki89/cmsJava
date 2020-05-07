package web.core.usermgmt.model;

import app.core.usermgmt.model.Role;
import web.core.model.AbstractForm;

public class RoleForm extends AbstractForm  {
	private static final long serialVersionUID = 1L;
	
	private Role role;

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}			
}
