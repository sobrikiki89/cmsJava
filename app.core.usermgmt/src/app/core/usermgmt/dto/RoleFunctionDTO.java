package app.core.usermgmt.dto;

import app.core.dto.DTOBase;
import app.core.usermgmt.model.RoleFunctionPK;

public class RoleFunctionDTO extends DTOBase {
	private static final long serialVersionUID = 1L;

	// Field to display in data table
	private RoleFunctionPK pk;
	private String role;
	private String function;
	private Boolean createable;
	private Boolean readable;
	private Boolean updateable;
	private Boolean deleteable;

	public RoleFunctionPK getPk() {
		return pk;
	}

	public void setPk(RoleFunctionPK pk) {
		this.pk = pk;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public Boolean getCreateable() {
		return createable;
	}

	public void setCreateable(Boolean createable) {
		this.createable = createable;
	}

	public Boolean getReadable() {
		return readable;
	}

	public void setReadable(Boolean readable) {
		this.readable = readable;
	}

	public Boolean getUpdateable() {
		return updateable;
	}

	public void setUpdateable(Boolean updateable) {
		this.updateable = updateable;
	}

	public Boolean getDeleteable() {
		return deleteable;
	}

	public void setDeleteable(Boolean deleteable) {
		this.deleteable = deleteable;
	}

}
