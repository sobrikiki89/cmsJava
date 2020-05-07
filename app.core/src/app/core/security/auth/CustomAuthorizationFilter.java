package app.core.security.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.util.Assert;

import app.core.security.UserPrincipal;

public class CustomAuthorizationFilter extends AbstractAuthenticationProcessingFilter {

	private AuthorizationManager authorizationManager;
	private SessionAuthenticationStrategy sessionStrategy = new NullAuthenticatedSessionStrategy();

	public CustomAuthorizationFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
	}

	@Override
	public void afterPropertiesSet() {
		Assert.notNull(this.authorizationManager, "An AuthorizationManager is required");
	}

	public AuthorizationManager getAuthorizationManager() {
		return authorizationManager;
	}

	public void setAuthorizationManager(AuthorizationManager authorizationManager) {
		this.authorizationManager = authorizationManager;
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (!requiresAuthentication(request, response)) {
			chain.doFilter(request, response);
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Request is to process authentication");
		}

		Authentication authResult;

		try {
			authResult = attemptAuthentication(request, response);
			if (authResult == null) {
				chain.doFilter(request, response);
				return;
			}
			sessionStrategy.onAuthentication(authResult, request, response);
			chain.doFilter(request, response);
		} catch (AccessDeniedException e) {
			logger.error("Error on checking user authorization.", e);
			response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			return;
		}
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, IOException, ServletException {
		Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
		// We will only check when the user has successfully login
		if (existingAuth != null && existingAuth.isAuthenticated()) {
			String uri = request.getRequestURI();
			int pathParamIndex = uri.indexOf(';');

			if (pathParamIndex > 0) {
				// strip everything after the first semi-colon
				uri = uri.substring(0, pathParamIndex);
			}

			String path = uri;
			if (path.startsWith(request.getContextPath())) {
				path = path.substring(request.getContextPath().length());
			}

			boolean isAuthorized = authorizationManager.isAuthorized(existingAuth, path);
			if (!isAuthorized) {
				String username = "";
				if (existingAuth.getPrincipal() != null && existingAuth.getPrincipal() instanceof UserPrincipal) {
					username = UserPrincipal.class.cast(existingAuth.getPrincipal()).getUsername();
				}

				throw new AccessDeniedException("User [" + username + "] is no authorized to page [" + uri + "]");
			}
		}

		return existingAuth;
	}
}
