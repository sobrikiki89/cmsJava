package app.core.service;

import java.util.Locale;

import app.core.registry.Module;
import app.core.security.UserPrincipal;

public interface MenuService {
	public void getMenu(UserPrincipal userPrincipal, Locale locale);

	public void updateMenus(Module module);
}
