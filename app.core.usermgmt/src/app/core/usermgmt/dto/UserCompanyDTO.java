package app.core.usermgmt.dto;

import app.core.dto.DTOBase;

public class UserCompanyDTO extends DTOBase {
	
	private static final long serialVersionUID = 1L;
	
	private String code;
	
	private String name;
	
	private Boolean active;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
}
