package web.core.menumgmt.model;

import java.util.List;

import app.core.menumgmt.model.MenuItem;
import app.core.usermgmt.model.Function;
import web.core.model.AbstractForm;

public class MenuItemForm extends AbstractForm {
	private static final long serialVersionUID = 1L;

	private String selected[];
	private MenuItem menuItem;
	private List<Function> functions;
	private List<MenuItem> menus;
	private Long parentId;

	public String[] getSelected() {
		return selected;
	}

	public void setSelected(String[] selected) {
		this.selected = selected;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}

	public List<Function> getFunctions() {
		return functions;
	}

	public void setFunctions(List<Function> functions) {
		this.functions = functions;
	}

	public List<MenuItem> getMenus() {
		return menus;
	}

	public void setMenus(List<MenuItem> menus) {
		this.menus = menus;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

}
