package app.core.service;

import java.util.List;

import org.springframework.security.core.AuthenticationException;

import app.core.security.UserPrincipal;

public interface UserDetailService {

	public UserPrincipal getUserByUserName(String username);

	public List<Long> getRoleIdByUserId(Long userId);

	public Long getUserLastRole(Long userId, List<Long> roleIds);

	public void updateLoginInfo(UserPrincipal userEntity, AuthenticationException e);

	public void updateLogoutInfo(UserPrincipal userEntity);
	
	public void updateLogoutInfo(String sessionId);
}
