package app.core.security.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import app.core.security.UserPrincipal;
import app.core.service.FunctionService;
import app.core.spring.UrlPattern;

@Component("customAuthorizationManager")
public class CustomAuthorizationManager implements AuthorizationManager {

	@Autowired
	private FunctionService functionService;

	@Override
	public boolean isAuthorized(Authentication authentication, String uri) {
		int indexParamPrefix = uri.indexOf(UrlPattern.PARAM_PREFIX);
		if (indexParamPrefix > 0) {
			if ("/".equals(uri.substring(indexParamPrefix - 1, indexParamPrefix))) {
				uri = uri.substring(0, indexParamPrefix - 1);
			} else {
				uri = uri.substring(0, indexParamPrefix);
			}
		} else if (indexParamPrefix == 0) {
			uri = "";
		}
		UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
		return functionService.isAuthorizedByPath(principal.getCurrentRoleId(), uri);
	}

	public boolean isAuthorized(Authentication authentication, HttpServletRequest request) {
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

		int indexParamPrefix = path.indexOf(UrlPattern.PARAM_PREFIX);
		if (indexParamPrefix > 0) {
			if ("/".equals(path.substring(indexParamPrefix - 1, indexParamPrefix))) {
				path = path.substring(0, indexParamPrefix - 1);
			} else {
				path = path.substring(0, indexParamPrefix);
			}
		} else if (indexParamPrefix == 0) {
			path = "";
		}
		UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
		return functionService.isAuthorizedByPath(principal.getCurrentRoleId(), path);
	}
}
