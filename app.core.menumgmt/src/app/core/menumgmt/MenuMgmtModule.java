package app.core.menumgmt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.core.annotation.Menu;
import app.core.annotation.MenuItem;
import app.core.annotation.Permission;
import app.core.menumgmt.service.MenuMgmtService;
import app.core.registry.Module;
import app.core.utils.AppConstant;

@Component("MenuMgmtModule")
@Menu({ @MenuItem(id = AppConstant.MENU_HOME_ID, sortOrder = 0, isParent = true, name = "Home", function = ""),
		@MenuItem(id = 1000L, sortOrder = 10, isParent = false, parentId = AppConstant.MENU_HOME_ID, name = "Menu Management", description = "Menu Setup", function = MenuMgmtModule.FUNC_MENU_SETUP_LIST) })
public class MenuMgmtModule extends Module {
	@Permission(name = "Menu Setup List", path = "/secured/menumgmt/menu")
	public static final String FUNC_MENU_SETUP_LIST = "CM01.MENU_LIST";

	@Permission(name = "Menu Setup Grid", path = "/secured/menumgmt/menugrid")
	public static final String FUNC_MENU_SETUP_GRID = "CM01.MENU_GRID";

	@Permission(name = "Menu Setup New", path = "/secured/menumgmt/menu/new")
	public static final String FUNC_MENU_SETUP_NEW = "CM01.MENU_NEW";

	@Permission(name = "Menu Setup Edit", path = "/secured/menumgmt/menu/edit")
	public static final String FUNC_MENU_SETUP_EDIT = "CM01.MENU_EDIT";

	@Autowired
	private MenuMgmtService menuMgmtService;

	@Override
	protected void init() throws Exception {
		// Initialization
	}

	@Override
	public String getModuleName() {
		return "[CORE] Menu Management Module / Menu";
	}

}
