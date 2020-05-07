package app.core.usermgmt.dto;

import java.text.DateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import app.core.dto.DTOBase;
import app.core.json.JsonDateSerializer;

public class UserDTO extends DTOBase {
	private static final long serialVersionUID = 1L;

	// Field to display in data table
	private Long id;
	private String username;
	private String firstName;
	private String lastName;
	private Boolean activeFlag;
	private Date lastAccessTime;
	private String companyCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Boolean getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

	@app.core.json.DateFormat("dd-MM-yyyy hh:mm:ss")
	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getLastAccessTime() {
		DateFormat.getDateTimeInstance();
		return lastAccessTime;
	}

	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

}
