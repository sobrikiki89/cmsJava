package app.core.usermgmt.dto;

import app.core.dto.DTOBase;

public class RoleDTO extends DTOBase{
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String description;
	private Long sortOrder;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
	}	
	
}
