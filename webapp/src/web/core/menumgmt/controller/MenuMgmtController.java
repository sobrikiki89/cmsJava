package web.core.menumgmt.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.github.dandelion.datatables.core.ajax.DatatablesResponse;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;

import app.core.exception.BaseApplicationException;
import app.core.menumgmt.dto.MenuItemDTO;
import app.core.menumgmt.model.MenuItem;
import app.core.menumgmt.service.MenuMgmtService;
import app.core.spring.UrlPattern;
import app.core.usermgmt.service.UserMgmtService;
import web.core.controller.BaseController;
import web.core.helper.WebConstant;
import web.core.menumgmt.model.MenuItemForm;
import web.core.menumgmt.validator.MenuFormValidator;

@Controller
@RequestMapping("/secured/menumgmt")
@SessionAttributes({ "menuForm" })
public class MenuMgmtController extends BaseController {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(MenuMgmtController.class);

	private String entityParentMenuItemId;
	private String entityFunctionId;
	private String criteriaParentMenuItemId;
	private String criteriaFunctionId;

	@Autowired
	private MenuMgmtService menuMgmtService;

	@Autowired
	private UserMgmtService userMgmtService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	}

	/*
	 * MENU_ITEM
	 */
	// 1. Menus Listing
	@RequestMapping(value = "/menu", method = RequestMethod.GET)
	public String getMenuItem(Locale locale, HttpServletRequest req, Model model) {
		model.addAttribute("menuForm", new MenuItemForm());
		return "secured.menumgmt.menu";
	}

	@RequestMapping(value = "/menu", method = RequestMethod.POST)
	public String menuPostByAction(Locale locale, HttpServletRequest req, HttpServletResponse resp, Model model,
			@ModelAttribute("menuForm") MenuItemForm menuForm, BindingResult result) {
		// We will determine the action
		if (WebConstant.ACTION_NEW.equalsIgnoreCase(menuForm.getAction())) {
			menuForm.setMenuItem(new MenuItem());
			menuForm.setMenus(menuMgmtService.getMenuItem());
			menuForm.setFunctions(userMgmtService.getFunction());
			return "secured.menumgmt.menu.new";
		}

		if (WebConstant.ACTION_BACK.equalsIgnoreCase(menuForm.getAction())) {
			return "redirect:/secured";
		}

		if (WebConstant.ACTION_DELETE.equalsIgnoreCase(menuForm.getAction())) {
			if (menuForm.getSelected() != null) {
				for (String id : menuForm.getSelected()) {
					try {
						MenuItem menuItem = new MenuItem(Long.parseLong(id));
						menuMgmtService.deleteMenu(menuItem);
					} catch (HibernateException e) {
						result.reject("menumgmt.menusetup.error.delete", new Object[] { id, e.getMessage() }, "");
					}
				}
			} else {
				result.reject("menumgmt.menusetup.required.deleteRecord");
			}
		}

		return "secured.menumgmt.menu";
	}

	@RequestMapping(value = "/menugrid", method = RequestMethod.GET)
	public @ResponseBody DatatablesResponse<MenuItemDTO> getMenuSetupGrid(
			@DatatablesParams DatatablesCriterias criterias) {
		try {
			DataSet<MenuItemDTO> dataSet = menuMgmtService.getMenuItem(criterias);
			return DatatablesResponse.build(dataSet, criterias);
		} catch (BaseApplicationException e) {
			logger.error("Error on getting menu item", e);
		}
		return null;
	}

	// 2. Save New Menu
	@RequestMapping(value = "/menu/new", method = RequestMethod.POST)
	public String saveMenuNew(Model model, @ModelAttribute("menuForm") MenuItemForm menuForm, BindingResult result) {
		MenuFormValidator validator = new MenuFormValidator(menuMgmtService);
		validator.validate(menuForm, result);

		if (result.hasErrors()) {
			menuForm.setMenus(menuMgmtService.getMenuItem());
			menuForm.setFunctions(userMgmtService.getFunction());
			return "secured.menumgmt.menu.new";
		} else {
			MenuItem menuItem = menuForm.getMenuItem();
			Long parentId = menuForm.getParentId();
			if (parentId != null) {
				menuItem.setParentMenuItem(new MenuItem(new Long(parentId)));
			}
			menuMgmtService.createMenu(menuItem);
			return "secured.menumgmt.menu";
		}
	}

	// 3. Edit Menu
	@RequestMapping(value = "/menu/edit/" + UrlPattern.PARAM_PREFIX + "/{menuId}", method = RequestMethod.GET)
	public String getMenuEdit(@PathVariable("menuId") Long menuId, Model model) {
		MenuItem menuItem = menuMgmtService.getMenuEdit(menuId);
		if (menuItem != null) {
			MenuItemForm menuForm = new MenuItemForm();
			menuForm.setMenus(menuMgmtService.getMenuItem());
			menuForm.setFunctions(userMgmtService.getFunction());
			menuForm.setMenuItem(menuItem);
			if (menuForm.getMenuItem().getParentMenuItem() != null) {
				menuForm.setParentId(menuForm.getMenuItem().getParentMenuItem().getId());
			}
			model.addAttribute("menuForm", menuForm);
			return "secured.menumgmt.menu.edit";
		}
		return "secured.menumgmt.menu";
	}

	@RequestMapping(value = "/menu/edit", method = RequestMethod.POST)
	public String saveMenuEdit(Model model, @ModelAttribute("menuForm") MenuItemForm menuEditForm,
			BindingResult result) {
		MenuFormValidator validator = new MenuFormValidator(menuMgmtService);
		validator.validate(menuEditForm, result);

		if (result.hasErrors()) {
			menuEditForm.setMenus(menuMgmtService.getMenuItem());
			menuEditForm.setFunctions(userMgmtService.getFunction());
			return "secured.menumgmt.menu.edit";
		} else {
			MenuItem menuItem = menuEditForm.getMenuItem();
			Long parentId = menuEditForm.getParentId();
			if (parentId != null) {
				menuItem.setParentMenuItem(new MenuItem(new Long(parentId)));
			}
			menuMgmtService.updateMenu(menuItem);
			return "secured.menumgmt.menu";
		}
	}

	/*
	 * MENU FOREIGN KEY
	 */
	public String getCriteriaFunctionId() {
		return criteriaFunctionId;
	}

	public void setCriteriaFunctionId(String criteriaFunctionId) {
		this.criteriaFunctionId = criteriaFunctionId;
	}

	public String getCriteriaParentMenuItemId() {
		return criteriaParentMenuItemId;
	}

	public void setCriteriaParentMenuItemId(String criteriaParentMenuItemId) {
		this.criteriaParentMenuItemId = criteriaParentMenuItemId;
	}

	public String getEntityFunctionId() {
		return entityFunctionId;
	}

	public void setEntityFunctionId(String entityFunctionId) {
		this.entityFunctionId = entityFunctionId;
	}

	public String getEntityParentMenuItemId() {
		return entityParentMenuItemId;
	}

	public void setEntityParentMenuItemId(String entityParentMenuItemId) {
		this.entityParentMenuItemId = entityParentMenuItemId;
	}
}
