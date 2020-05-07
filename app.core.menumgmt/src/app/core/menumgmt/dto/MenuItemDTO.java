package app.core.menumgmt.dto;

import java.util.List;

import app.core.dto.DTOBase;
import app.core.menumgmt.model.MenuItem;

public class MenuItemDTO extends DTOBase {

	private static final long serialVersionUID = 1L;

	// Field to display in data table
	private Long id;
	private String parentMenuItem;
	private List<MenuItem> childMenuItems;
	private String name;
	private String description;
	private Long sortOrder;
	private Boolean parentFlag;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getParentMenuItem() {
		return parentMenuItem;
	}
	public void setParentMenuItem(String parentMenuItem) {
		this.parentMenuItem = parentMenuItem;
	}

	public List<MenuItem> getChildMenuItems() {
		return childMenuItems;
	}
	public void setChildMenuItems(List<MenuItem> childMenuItems) {
		this.childMenuItems = childMenuItems;
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

	public Boolean getParentFlag() {
		return parentFlag;
	}

	public void setParentFlag(Boolean parentFlag) {
		this.parentFlag = parentFlag;
	}

}
