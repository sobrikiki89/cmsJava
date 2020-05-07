package app.core.service.impl;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.core.security.UserPrincipal;
import app.core.service.UserDetailService;

@Service
public class UserDetailServiceImpl implements UserDetailService {

	private static final Logger logger = LoggerFactory.getLogger(UserDetailServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional(readOnly = true)
	public UserPrincipal getUserByUserName(String username) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT au.id, au.username, au.salt, au.password, au.active_flag, au.pwd_upddate, au.pwd_expiry_days, ");
		sql.append("au.force_change_pwd_flag, au.invalid_login_count, au.max_invalid_login_count, ");
		sql.append("au.last_access_time, au.connected_flag, au.session_timeout_minutes,c.code FROM app_user au ");
		sql.append("LEFT JOIN user_company uc ON uc.user_id = au.id ");
		sql.append("LEFT JOIN company c ON uc.company_id = c.id ");
		sql.append("WHERE username = ?");

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(sql.toString()).setParameter(0, username);
		@SuppressWarnings("unchecked")
		List<Object[]> result = query.list();

		List<UserPrincipal> users = new ArrayList<UserPrincipal>();
		for (Object[] row : result) {
			UserPrincipal user = new UserPrincipal();
			user.setUserId(((BigInteger) row[0]).longValue());
			user.setUsername((String) row[1]);
			user.setSalt((String) row[2]);
			user.setPassword((String) row[3]);
			user.setActiveFlag(((Boolean) row[4]) ? 1 : 0);
			user.setPwdUpdDate((Date) row[5]);
			user.setPwdExpiryDays(((BigInteger) row[6]).intValue());
			user.setForceChangePwdFlag(((Boolean) row[7]) ? 1 : 0);
			user.setInvalidLoginCount(((BigInteger) row[8]).intValue());
			user.setMaxInvalidLoginCount(((BigInteger) row[9]).intValue());
			user.setLastAccessTime((Date) row[10]);
			user.setConnectedFlag(((Boolean) row[11]) ? 1 : 0);
			user.setSessionInactiveTimeout(((BigInteger) row[12]).intValue());
			user.setCompanyCode((String) row[13]);
			users.add(user);
		}

		if (users == null || users.size() == 0) {
			return null;
		}

		return users.get(0);
	}

	@Transactional(readOnly = true)
	public List<Long> getRoleIdByUserId(Long userId) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT role_id FROM user_role WHERE user_id = ?");

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(sql.toString()).setParameter(0, userId);
		@SuppressWarnings("rawtypes")
		List result = query.list();

		List<Long> roleIds = new ArrayList<Long>();
		for (Object row : result) {
			roleIds.add(BigInteger.class.cast(row).longValue());
		}

		return roleIds;
	}

	@Transactional
	public Long getUserLastRole(Long userId, List<Long> roleIds) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT ucv.value ");
		sql.append(
				"FROM user_context_var ucv LEFT JOIN context_var_type cvt ON ucv.context_var_type_id = cvt.ID AND cvt.name = 'LAST_ROLE_ID'");
		sql.append("WHERE ucv.USER_ID = ?");

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(sql.toString()).setParameter(0, userId);
		@SuppressWarnings("rawtypes")
		List result = query.list();

		// No last role found
		if (result == null || result.size() == 0) {
			if (roleIds != null && roleIds.size() > 0) {
				String roleId = String.valueOf(roleIds.get(0));
				if (logger.isDebugEnabled()) {
					logger.debug("No LAST_ROLE_ID found for user id : [" + userId + "], pick the first role [" + roleId
							+ "] among the roles that have been assigned to this user.");
				}
				// User has been assigned with roles, take the first role and
				// save it to the USER_CONTEXT_VAR
				int row = session
						.createSQLQuery(
								"INSERT INTO user_context_var(context_var_type_id, user_id, value) VALUES ((SELECT id FROM context_var_type WHERE name = 'LAST_ROLE_ID'), ?, ?)")
						.setLong(0, userId).setString(1, roleId).executeUpdate();
				if (logger.isDebugEnabled()) {
					logger.debug("Insert user_context_var, row get updated : [" + row + "]");
				}

				return roleIds.get(0);
			}
		} else {
			return Long.valueOf(String.class.cast(result.get(0)));
		}

		return null;
	}

	@Transactional
	public void updateLoginInfo(UserPrincipal userEntity, AuthenticationException e) {
		if (userEntity != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("User found, update LOGIN info");
			}

			Map<String, Object> parameters = new HashMap<String, Object>();
			StringBuffer sql = new StringBuffer();
			// Here because we didn't update the record via Entity and caused
			// the hibernate intercepter will not update the UPDDATE and UPDUSER
			// column,
			// therefore we need to update manually
			sql.append("UPDATE app_user SET upddate = :upddate, upduser = id");
			parameters.put("upddate", new Timestamp(System.currentTimeMillis()));

			if (e == null) {
				sql.append(", invalid_login_count = :invalid_login_count, connected_flag = :connected_flag")
						.append(", last_access_time = :last_access_time, ip_address = :ip_address")
						.append(", session_id = :session_id");
				parameters.put("invalid_login_count", 0);
				parameters.put("connected_flag", Boolean.TRUE);
				parameters.put("last_access_time", new Timestamp(System.currentTimeMillis()));
				parameters.put("ip_address", userEntity.getIpAddress());
				parameters.put("session_id", userEntity.getSessionId());
			}

			else if (e instanceof LockedException) {
				sql.append(", invalid_login_count = :invalid_login_count");
				parameters.put("invalid_login_count", userEntity.getInvalidLoginCount());
			}

			else if (e instanceof BadCredentialsException) {
				sql.append(", invalid_login_count = :invalid_login_count");
				parameters.put("invalid_login_count", userEntity.getInvalidLoginCount());
			}

			sql.append(" where id = :id");
			parameters.put("id", userEntity.getUserId());

			try {
				Session session = sessionFactory.getCurrentSession();
				Query query = session.createSQLQuery(sql.toString());
				//
				// Set query parameter values
				//
				Iterator<String> iter = parameters.keySet().iterator();
				while (iter.hasNext()) {
					String name = iter.next();
					Object value = parameters.get(name);
					query.setParameter(name, value);
				}
				int row = query.executeUpdate();

				if (logger.isDebugEnabled()) {
					logger.debug("Update LOGIN info success, row updated [" + row + "]");
				}
			} catch (Exception ex) {
				logger.error("Error on updating LOGIN info", ex);
			}
		}
	}

	@Transactional
	public void updateLogoutInfo(UserPrincipal userEntity) {
		if (userEntity != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("User found, update LOGOUT info");
			}

			Map<String, Object> parameters = new HashMap<String, Object>();
			StringBuffer sql = new StringBuffer();

			sql.append("UPDATE app_user SET upddate = :upddate, upduser = :id, connected_flag = :connected_flag")
					.append(", session_id = null where id = :id");
			parameters.put("upddate", new Timestamp(System.currentTimeMillis()));
			parameters.put("connected_flag", Boolean.FALSE);
			parameters.put("id", userEntity.getUserId());

			try {
				Session session = sessionFactory.getCurrentSession();
				Query query = session.createSQLQuery(sql.toString());
				//
				// Set query parameter values
				//
				Iterator<String> iter = parameters.keySet().iterator();
				while (iter.hasNext()) {
					String name = iter.next();
					Object value = parameters.get(name);
					query.setParameter(name, value);
				}
				int row = query.executeUpdate();

				if (logger.isDebugEnabled()) {
					logger.debug("Update LOGOUT info success, row updated [" + row + "]");
				}
			} catch (Exception ex) {
				logger.error("Error on updating LOGOUT info", ex);
			}
		}
	}

	@Transactional
	public void updateLogoutInfo(String sessionId) {
		if (logger.isDebugEnabled()) {
			logger.debug("Update LOGOUT info by session id = [" + sessionId + "]");
		}

		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer();

		sql.append("UPDATE app_user SET upddate = :upddate, connected_flag = :connected_flag")
				.append(", session_id = null where session_id = :session_id");
		parameters.put("upddate", new Timestamp(System.currentTimeMillis()));
		parameters.put("connected_flag", Boolean.FALSE);
		parameters.put("session_id", sessionId);

		try {
			Session session = sessionFactory.getCurrentSession();
			Query query = session.createSQLQuery(sql.toString());
			//
			// Set query parameter values
			//
			Iterator<String> iter = parameters.keySet().iterator();
			while (iter.hasNext()) {
				String name = iter.next();
				Object value = parameters.get(name);
				query.setParameter(name, value);
			}
			int row = query.executeUpdate();

			if (logger.isDebugEnabled()) {
				logger.debug("Update LOGOUT info by session id success, row updated [" + row + "]");
			}
		} catch (Exception ex) {
			logger.error("Error on updating LOGOUT info by session id", ex);
		}
	}
}
