package app.core.menumgmt.service;

import java.util.List;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.exception.BaseApplicationException;
import app.core.menumgmt.dto.MenuItemDTO;
import app.core.menumgmt.model.MenuItem;
import app.core.usermgmt.model.Function;

public interface MenuMgmtService {

	// Grid
	public DataSet<MenuItemDTO> getMenuItem(DatatablesCriterias criterias) throws BaseApplicationException;

	// New
	public Long createMenu(MenuItem menuItem);

	public List<MenuItem> getMenuItem();

	// Edit
	public MenuItem getMenuEdit(Long menuId);

	public void updateMenu(MenuItem menuItem);

	// Delete
	public void deleteMenu(MenuItem menuItem);

	public MenuItem getMenuItemByFunction(Function function);
}
