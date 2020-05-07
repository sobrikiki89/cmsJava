package app.core.security.auth;

import org.springframework.security.core.Authentication;

public interface AuthorizationManager {
	public boolean isAuthorized(Authentication authentication, String uri);
}
