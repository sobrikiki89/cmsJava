package web.core.menumgmt.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import app.core.menumgmt.model.MenuItem;
import app.core.menumgmt.service.MenuMgmtService;
import web.core.menumgmt.model.MenuItemForm;

public class MenuFormValidator implements Validator {

	private MenuMgmtService menuMgmtService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return MenuItemForm.class.equals(clazz);
	}

	public MenuFormValidator(MenuMgmtService menuMgmtService) {
		this.menuMgmtService = menuMgmtService;
	}
	
	@Override
	public void validate(Object obj, Errors errors) {
		
		ValidationUtils.rejectIfEmpty(errors, "menuItem.name", "menumgmt.menusetup.required.name");
		ValidationUtils.rejectIfEmpty(errors, "menuItem.function", "menumgmt.menusetup.required.function");
		ValidationUtils.rejectIfEmpty(errors, "menuItem.sortOrder", "menumgmt.menusetup.required.sortOrder");
		
		//check for duplicate
		MenuItemForm form = MenuItemForm.class.cast(obj);
		MenuItem menuItem = form.getMenuItem();
		
		if (menuItem.getFunction().getCode()!=null) {
			MenuItem entity = menuMgmtService.getMenuItemByFunction(menuItem.getFunction());
			if(entity != null) {
				errors.reject("menumgmt.menusetup.error.duplicate");
			}
		}
	}
	
	
}
