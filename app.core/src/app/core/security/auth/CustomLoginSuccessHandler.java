package app.core.security.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import app.core.security.UserPrincipal;
import app.core.service.MenuService;

public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomLoginSuccessHandler.class);

	@Autowired
	private MenuService menuService;

	// This method will be invoked once user has successfully logout
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// Do whatever necessary step here before it has been redirected to
		// landing page
		if (authentication.getPrincipal() != null && authentication.getPrincipal() instanceof UserPrincipal) {
			// Setting context root
			UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
			principal.setContextRoot(request.getContextPath());
			// Setting session timeout
			LOGGER.info("#### Session id [" + principal.getSessionId() + "] inactive timeout is ["
					+ principal.getSessionInactiveTimeout() + " minutes]");
			request.getSession().setMaxInactiveInterval(60 * principal.getSessionInactiveTimeout());
			// Setting menu
			menuService.getMenu(principal, request.getLocale());
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}
}