package module.setup.dto;

import java.io.Serializable;

import app.core.dto.DTOBase;

public class InsuranceClassDTO extends DTOBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;

	private String name;
	
	private String category;

	private Long sortOrder;

	private Boolean activeFlag;

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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Long getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Boolean getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
	}
}
