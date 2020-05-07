package app.core.security.auth;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import app.core.security.ConcurrenLoginException;
import app.core.security.UserPrincipal;
import app.core.service.UserDetailService;
import app.core.utils.MD5Utils;

@Component
public class CustomAuthenticationManager implements AuthenticationManager {

	private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationManager.class);

	@Autowired
	private UserDetailService userService;

	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		// Input is UsernamePasswordAuthenticationToken and credentials =
		// "password", principal = "admin"
		if (logger.isDebugEnabled()) {
			logger.debug("!!! Verifying username [" + auth.getPrincipal() + "]");
		}

		UserPrincipal userEntity = null;

		try {
			if (auth.getPrincipal() == null || "".equals(auth.getPrincipal()) || auth.getCredentials() == null
					|| "".equals(auth.getCredentials())) {
				logger.info("!!! Username or password is empty");
				throw new BadCredentialsException("Username or password is empty, username [" + auth.getPrincipal()
						+ "]");
			}

			try {
				userEntity = userService.getUserByUserName(String.class.cast(auth.getPrincipal()));
			} catch (Exception e) {
				logger.info("!!! Error on finding user entity, username [" + auth.getPrincipal() + "]", e);
				throw new AuthenticationServiceException("Error on finding user entity", e);
			}

			if (userEntity == null) {
				logger.info("!!! No user found with username [" + auth.getPrincipal() + "]");
				throw new UsernameNotFoundException("User does not exists!");
			}

			// Checking account whether it is disabled
			if (1 != userEntity.getActiveFlag()) {
				logger.info("!!! Account has been diabled, username [" + auth.getPrincipal() + "]");
				throw new DisabledException("Account has been diabled");
			}

			// Checking whether account has concurrent login or not
			if (0 != userEntity.getConnectedFlag()) {
				logger.info("!!! User already logged in, username [" + auth.getPrincipal() + "]");
				throw new ConcurrenLoginException("Only one user is be allowed to login at one time");
			}

			// Checking maximum login attempt whether it is exceed
			if (userEntity.getInvalidLoginCount() >= userEntity.getMaxInvalidLoginCount()) {
				logger.info("!!! Maximum login attempts exceeded, Max attempt [" + userEntity.getMaxInvalidLoginCount()
						+ "], username [" + auth.getPrincipal() + "]");
				// We need to update the max login attempt although it is
				// exceeded
				userEntity.setInvalidLoginCount(userEntity.getInvalidLoginCount() + 1);
				throw new LockedException("Maximum login attempts exceeded, Max attempt ["
						+ userEntity.getMaxInvalidLoginCount() + "]");
			}

			// Checking credentials
			if (!userEntity.getPassword().equals(
					MD5Utils.encrypt(userEntity.getSalt(), String.class.cast(auth.getCredentials())))) {
				logger.info("!!! Invalid username or password, username [" + auth.getPrincipal() + "]");
				// We need to update the max login attempt although it is
				// exceeded
				userEntity.setInvalidLoginCount(userEntity.getInvalidLoginCount() + 1);
				throw new BadCredentialsException("Invalid username or password");
			}

			List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
			try {
				// Retrieving all roles that have been assigned to the user
				List<Long> roleIds = userService.getRoleIdByUserId(userEntity.getUserId());
				// Retrieve last role id that the user used
				Long lastRoleId = userService.getUserLastRole(userEntity.getUserId(), roleIds);
				for (Object row : roleIds) {
					SimpleGrantedAuthority authority = new SimpleGrantedAuthority(String.valueOf(row));
					authorities.add(authority);
				}

				// We only render menu, when we found role id
				if (lastRoleId != null && !roleIds.isEmpty()) {
					// Compare against with role id list and make sure the last
					// role id is exists, else we will reset to the first role
					// id from the role id list
					boolean found = false;
					for (Long roleId : roleIds) {
						if (lastRoleId == roleId) {
							found = true;
						}
					}
					if (!found) {
						userEntity.setCurrentRoleId(roleIds.get(0));
					} else {
						userEntity.setCurrentRoleId(lastRoleId);
					}
				}
			} catch (Exception e) {
				logger.info("!!! Error on getting user role and setting up menu [" + auth.getPrincipal() + "]", e);
				throw new AuthenticationServiceException("Error on getting user role and setting up menu", e);
			}

			WebAuthenticationDetails webAuthDetails = (WebAuthenticationDetails) auth.getDetails();
			if (webAuthDetails != null) {
				userEntity.setIpAddress(webAuthDetails.getRemoteAddress());
				userEntity.setSessionId(webAuthDetails.getSessionId());
			}
			userService.updateLoginInfo(userEntity, null);
			return new UsernamePasswordAuthenticationToken(userEntity, auth.getCredentials(), authorities);

		} catch (AuthenticationException e) {
			userService.updateLoginInfo(userEntity, e);
			throw e;
		}
	}
}
