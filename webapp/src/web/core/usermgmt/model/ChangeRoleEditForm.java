package web.core.usermgmt.model;

import web.core.model.AbstractForm;

public class ChangeRoleEditForm extends AbstractForm {
	private static final long serialVersionUID = 1L;

	private Long roleId;

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
}
