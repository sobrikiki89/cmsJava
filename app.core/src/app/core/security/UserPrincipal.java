package app.core.security;

import java.io.Serializable;
import java.util.Date;

public class UserPrincipal implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long userId;
	private String username;
	private String salt;
	private String password;
	private Integer activeFlag;
	private Date pwdUpdDate;
	private Integer pwdExpiryDays;
	private Integer forceChangePwdFlag;
	private Integer invalidLoginCount;
	private Integer maxInvalidLoginCount;
	private Date lastAccessTime;
	private Integer connectedFlag;
	private String ipAddress;
	private String sessionId;
	private Long currentRoleId;
	private Integer sessionInactiveTimeout;
	private String menuList;
	private String companyCode;
	// This field is important when rendering the menu href
	// It will be set by the CustomLoginSuccessHandler then use at
	// MenuServiceImpl
	private String contextRoot;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
	}

	public Date getPwdUpdDate() {
		return pwdUpdDate;
	}

	public void setPwdUpdDate(Date pwdUpdDate) {
		this.pwdUpdDate = pwdUpdDate;
	}

	public Integer getPwdExpiryDays() {
		return pwdExpiryDays;
	}

	public void setPwdExpiryDays(Integer pwdExpiryDays) {
		this.pwdExpiryDays = pwdExpiryDays;
	}

	public Integer getForceChangePwdFlag() {
		return forceChangePwdFlag;
	}

	public void setForceChangePwdFlag(Integer forceChangePwdFlag) {
		this.forceChangePwdFlag = forceChangePwdFlag;
	}

	public Integer getInvalidLoginCount() {
		return invalidLoginCount;
	}

	public void setInvalidLoginCount(Integer invalidLoginCount) {
		this.invalidLoginCount = invalidLoginCount;
	}

	public Integer getMaxInvalidLoginCount() {
		return maxInvalidLoginCount;
	}

	public void setMaxInvalidLoginCount(Integer maxInvalidLoginCount) {
		this.maxInvalidLoginCount = maxInvalidLoginCount;
	}

	public Date getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public Integer getConnectedFlag() {
		return connectedFlag;
	}

	public void setConnectedFlag(Integer connectedFlag) {
		this.connectedFlag = connectedFlag;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Long getCurrentRoleId() {
		return currentRoleId;
	}

	public void setCurrentRoleId(Long currentRoleId) {
		this.currentRoleId = currentRoleId;
	}

	public Integer getSessionInactiveTimeout() {
		return sessionInactiveTimeout;
	}

	public void setSessionInactiveTimeout(Integer sessionInactiveTimeout) {
		this.sessionInactiveTimeout = sessionInactiveTimeout;
	}

	public String getMenuList() {
		return menuList;
	}

	public void setMenuList(String menuList) {
		this.menuList = menuList;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getContextRoot() {
		return contextRoot;
	}

	public void setContextRoot(String contextRoot) {
		this.contextRoot = contextRoot;
	}
}
