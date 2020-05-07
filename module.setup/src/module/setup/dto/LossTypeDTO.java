package module.setup.dto;

import java.io.Serializable;

import app.core.dto.DTOBase;

public class LossTypeDTO extends DTOBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;

	private String name;

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
