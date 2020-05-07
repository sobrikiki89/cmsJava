package module.setup.dto;

import java.io.Serializable;

import app.core.dto.DTOBase;

public class UserCompanyDTO extends DTOBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long companyId;

	private String companyName;

	private Long userId;

	private String userName;
	
	private String firstName;
	
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
}
