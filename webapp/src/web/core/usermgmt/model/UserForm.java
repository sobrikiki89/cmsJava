package web.core.usermgmt.model;

import java.util.ArrayList;
import java.util.List;

import app.core.usermgmt.model.Role;
import app.core.usermgmt.model.User;
import app.core.usermgmt.model.UserRole;
import web.core.model.AbstractForm;

public class UserForm extends AbstractForm {

	private static final long serialVersionUID = 1L;

	private User user;
	private List<Role> roles;
	private List<Long> roleIds;
	private Boolean allowedChange;

	public UserForm() {
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;

		List<Long> dbRoleIds = new ArrayList<Long>();
		if (user != null && user.getUserRoles() != null) {
			for (UserRole ur : user.getUserRoles()) {
				if (ur != null && ur.getRole() != null && ur.getRole().getId() != null) {
					dbRoleIds.add(ur.getRole().getId());
				}
			}
		}
		roleIds = dbRoleIds;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<Long> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}

	public Boolean getAllowedChange() {
		return allowedChange;
	}

	public void setAllowedChange(Boolean allowedChange) {
		this.allowedChange = allowedChange;
	}

}
