package app.core.usermgmt.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import app.core.model.EntityHistory;
import app.core.utils.AppConstant;

/**
 * Represents a "login account", ie someone who is permitted to use the
 * application.
 */
@Entity
@Table(name = "APP_USER")
public class User extends EntityHistory {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String tempPassword;

	private String username;

	private String salt;

	private String password;

	private String firstName;

	private String lastName;

	private String hintQuestion;

	private String hintAnswer;

	private Long invalidLoginCount;

	private Long maxInvalidLoginCount;

	private Long invalidChangePwdCount;

	private Date pwdUpdateDate;

	private Long pwdExpiryDays;

	private Long sessionTimeoutMinutes;

	private String ipAddress;

	private Date lastAccessTime;

	private Boolean connectedFlag;

	private Boolean activeFlag;

	private Boolean forceChangePwdFlag;

	private String dateFormat;

	private boolean pwdChanged;

	private String confirmPassword;

	private List<PasswordHistory> passwordHistories;

	private List<EventLog> eventLogs;

	private List<AccessLog> accessLogs;

	private List<UserRole> userRoles;

	private List<UserContextVar> userContextVars;

	private Date lastLoginTime;

	private Long signatureStoredFileId;

	private String jobTitle;

	private Long hintAnswerGuessCount;

	private AppUserType appUserType;
	
	private String appUserTypeSrcId;

	private String homePageUrl;

	private String sessionId;

	private String email;
	
	// Constructors
	@Column(name = "JOB_TITLE")
	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	/** default constructor */
	public User() {
	}

	/** constructor with id */
	public User(Long id) {
		this.id = id;
	}

	// Property accessors

	/** Synthetic ID */
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "entityKeys")
	@Column(name = "ID")
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * User type for the particular user
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = "APP_USER_TYPE_CODE")
	public AppUserType getAppUserType() {
		return this.appUserType;
	}

	public void setAppUserType(AppUserType appUserType) {
		this.appUserType = appUserType;
	}

	/**
	 * The name this user will use to log in to the system.
	 */
	@Column(name = "USERNAME")
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * The oracle date format the user will use throughout the application ex :
	 * DD/MM/YYY
	 */
	@Column(name = "DATE_FORMAT")
	public String getDateFormat() {
		if (dateFormat == null)
			dateFormat = "dd/MM/yyyy";
		return this.dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * The "salt" used when encrypting the user's password.
	 * <p>
	 * All encryption algorithms are vulnerable to table-lookup-based attacks,
	 * ie where someone precomputes the encrypted form of thousands of common
	 * passwords, then looks at the encrypted data to see whether any of the
	 * encrypted patterns match something in their lookup table. This attack can
	 * be foiled by using a random "salt" together with the user password when
	 * encrypting it. The salt doesn't need to remain secret, it just needs to
	 * be different for each user so that the same common password encrypted
	 * with different salts results in different output, thereby foiling any
	 * lookups.
	 */
	@Column(name = "SALT")
	public String getSalt() {
		return this.salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	/**
	 * The encrypted form of the user's original password.
	 * <p>
	 * TODO: what is the encryption algorithm used??
	 */
	@Column(name = "PASSWORD")
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "FIRST_NAME")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "LAST_NAME")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Used to prompt the user's memory of their password if they forget it.
	 */
	@Column(name = "HINT_QUESTION")
	public String getHintQuestion() {
		return this.hintQuestion;
	}

	public void setHintQuestion(String hintQuestion) {
		this.hintQuestion = hintQuestion;
	}

	/**
	 * Used to prompt the user's memory of their password if they forget it.
	 */
	@Column(name = "HINT_ANSWER")
	public String getHintAnswer() {
		return this.hintAnswer;
	}

	public void setHintAnswer(String hintAnswer) {
		this.hintAnswer = hintAnswer;
	}

	/**
	 * The number of times this user has entered an invalid password. this is
	 * reset on successful login
	 */
	@Column(name = "INVALID_LOGIN_COUNT")
	public Long getInvalidLoginCount() {
		return this.invalidLoginCount;
	}

	public void setInvalidLoginCount(Long invalidLoginCount) {
		this.invalidLoginCount = invalidLoginCount;
	}

	@Column(name = "HINT_ANSWER_GUESS_COUNT", insertable = true, updatable = true)
	public Long getHintAnswerGuessCount() {
		if (hintAnswerGuessCount == null)
			hintAnswerGuessCount = new Long(0);
		return hintAnswerGuessCount;
	}

	public void setHintAnswerGuessCount(Long hintAnswerGuessCount) {
		this.hintAnswerGuessCount = hintAnswerGuessCount;
	}

	/**
	 * The maximum number of times a user can enter an invalid password before
	 * they get locked out of the system. This is used to prevent
	 * password-guessing attacks on the system.
	 */
	@Column(name = "MAX_INVALID_LOGIN_COUNT", insertable = true, updatable = true)
	public Long getMaxInvalidLoginCount() {
		return this.maxInvalidLoginCount;
	}

	public void setMaxInvalidLoginCount(Long maxInvalidLoginCount) {
		this.maxInvalidLoginCount = maxInvalidLoginCount;
	}

	/**
	 * The number of times a user has entered an invalid password when trying to
	 * change their password.
	 * <p>
	 * TODO: what is this used for??
	 */
	@Column(name = "INVALID_CHANGE_PWD_COUNT")
	public Long getInvalidChangePwdCount() {
		return this.invalidChangePwdCount;
	}

	public void setInvalidChangePwdCount(Long invalidChangePwdCount) {
		this.invalidChangePwdCount = invalidChangePwdCount;
	}

	/**
	 * The date at which the user must change their password.
	 */
	@Column(name = "PWD_UPDDATE", insertable = true, updatable = true)
	@DateTimeFormat(pattern = AppConstant.DATE_TIME_FORMAT)
	public Date getPwdUpdateDate() {
		return this.pwdUpdateDate;
	}

	public void setPwdUpdateDate(Date pwdUpdateDate) {
		this.pwdUpdateDate = pwdUpdateDate;
	}

	/**
	 * The number of days that a user is allowed to use a password before they
	 * are forced to select a new one. When a password is changed, this value is
	 * used to determine the value for property pwdUpdateDate.
	 */
	@Column(name = "PWD_EXPIRY_DAYS")
	public Long getPwdExpiryDays() {
		return this.pwdExpiryDays;
	}

	public void setPwdExpiryDays(Long pwdExpiryDays) {
		this.pwdExpiryDays = pwdExpiryDays;
	}

	/**
	 * The number of minutes that a user's HTTP browser session should remain
	 * active before they are automatically logged out and the session memory
	 * reclaimed.
	 */
	@Column(name = "SESSION_TIMEOUT_MINUTES", insertable = true, updatable = true)
	public Long getSessionTimeoutMinutes() {
		return this.sessionTimeoutMinutes;
	}

	public void setSessionTimeoutMinutes(Long sessionTimeoutMinutes) {
		this.sessionTimeoutMinutes = sessionTimeoutMinutes;
	}

	/**
	 * The IP address that the user is currently logged in from. If the user is
	 * not currently logged-in then this is the last ip address they logged in
	 * from.
	 */
	@Column(name = "IP_ADDRESS")
	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * The time the user last successfully logged on to the system. See also
	 * property ipAddress.
	 */
	@Column(name = "LAST_ACCESS_TIME")
	public Date getLastAccessTime() {
		return this.lastAccessTime;
	}

	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	@Column(name = "CONNECTED_FLAG")
	// @Type(type = "org.hibernate.type.NumericBooleanType")
	public Boolean getConnectedFlag() {
		if (connectedFlag == null)
			connectedFlag = false;
		return this.connectedFlag;
	}

	public void setConnectedFlag(Boolean b) {
		this.connectedFlag = b;
	}

	@Column(name = "ACTIVE_FLAG")
	// @Type(type = "org.hibernate.type.NumericBooleanType")
	public Boolean getActiveFlag() {
		if (activeFlag == null)
			activeFlag = true;
		return this.activeFlag;
	}

	public void setActiveFlag(Boolean b) {
		this.activeFlag = b;
	}

	@Column(name = "APP_USER_TYPE_SRC_ID")
	public String getAppUserTypeSrcId() {
		return this.appUserTypeSrcId;
	}

	public void setAppUserTypeSrcId(String appUserTypeSrcId) {
		this.appUserTypeSrcId = appUserTypeSrcId;
	}

	@Column(name = "HOME_PAGE_URL")
	public String getHomePageUrl() {
		return this.homePageUrl;
	}

	public void setHomePageUrl(String homePageUrl) {
		this.homePageUrl = homePageUrl;
	}

	@Column(name = "SIGNATURE_STORED_FILE_ID")
	public Long getSignatureStoredFileId() {
		return signatureStoredFileId;
	}

	public void setSignatureStoredFileId(Long signatureStoredFileId) {
		this.signatureStoredFileId = signatureStoredFileId;
	}

	@Column(name = "SESSION_ID")
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Column(name = "FORCE_CHANGE_PWD_FLAG")
	// @Type(type = "org.hibernate.type.NumericBooleanType")
	public Boolean getForceChangePwdFlag() {
		return this.forceChangePwdFlag;
	}

	public void setForceChangePwdFlag(Boolean b) {
		this.forceChangePwdFlag = b;
	}

	@Column(name = "EMAIL")	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * The encrypted form of passwords that the user has used in the past. This
	 * is used to prevent users from changing their password to one that they
	 * have used in the recent past.
	 * <p>
	 * Looking up old passwords is done infrequently (only when a user changes
	 * their current password) so this relation is lazy.
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	public List<PasswordHistory> getPasswordHistories() {
		return this.passwordHistories;
	}

	public void setPasswordHistories(List<PasswordHistory> passwordHistories) {
		this.passwordHistories = passwordHistories;
	}

	/**
	 * An EventLog persistent object is created each time the user does
	 * "something interesting". This method returns the set of EventLog objects
	 * created due to this user's actions.
	 */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	public List<EventLog> getEventLogs() {
		return this.eventLogs;
	}

	public void setEventLogs(List<EventLog> eventLogs) {
		this.eventLogs = eventLogs;
	}

	/**
	 * to make sure the password is what a user want
	 * 
	 * @return
	 */
	@Transient
	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	@Transient
	public boolean isPwdChanged() {
		return pwdChanged;
	}

	public void setPwdChanged(boolean pwdChanged) {
		this.pwdChanged = pwdChanged;
	}

	/**
	 * The set of URLs that the user has accessed in the past. This information
	 * is only stored when a "debugging" flag is enabled for this user; see the
	 * AccessLog class for more info.
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	public List<AccessLog> getAccessLogs() {
		return this.accessLogs;
	}

	public void setAccessLogs(List<AccessLog> accessLogs) {
		this.accessLogs = accessLogs;
	}

	/**
	 * The set of security roles that are associated with this user. This
	 * defines what functionality of the application user can access.
	 */
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "USER_ID")
	public List<UserRole> getUserRoles() {
		return this.userRoles;
	}

	public void setUserRoles(List<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	/**
	 * The set of view-related configuration settings for this user. These
	 * affect how the application is presented to the user.
	 */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	public List<UserContextVar> getUserContextVars() {
		return this.userContextVars;
	}

	public void setUserContextVars(List<UserContextVar> userContextVars) {
		this.userContextVars = userContextVars;
	}

	/**
	 * List of the todos to be displayed on the home page Not yet implemented in
	 * the schema but we'll need it later
	 * 
	 * @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	 * @JoinColumn(name = "USER_ID") public List<Todo> getTodos() { return
	 *                  this.todos; }
	 * 
	 *                  public void setTodos(List<Todo> todos) { this.todos =
	 *                  todos; }
	 */

	/**
	 * Returns the last time the user logged in. This is not retrieved from the
	 * underlying table (APP_USER) but rather is populated when a user logs in
	 * with the most recent "VALID LOGIN" code event from eventLog.
	 */
	@Transient
	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	@Transient
	public Role getDefaultRole() {
		for (UserRole userRole : userRoles) {
			if (userRole != null && userRole.getRole().isAssociated()) {
				return userRole.getRole();
			}
		}
		return new Role();
	}

	@Transient
	public Integer getRoleCount() {
		return (userRoles == null ? 0 : userRoles.size());
	}

	/**
	 * Returns true if this user account is locked due to too many failed login
	 * attempts. See also getMaxInvalidLoginCount, getInvalidLoginCount.
	 */
	@Transient
	public boolean isLockedOut() {
		Long maxInvalid = getMaxInvalidLoginCount();
		Long invalid = getInvalidLoginCount();

		if (maxInvalid == null) {
			// there is no max limit, so account not locked
			return false;
		}

		if (invalid == null) {
			// there have been no invalid attempts, so account not locked
			return false;
		}

		return (invalid.longValue() >= maxInvalid.longValue());
	}

	@Transient
	public String getTempPassword() {
		return tempPassword;
	}

	@Transient
	public void setTempPassword(String tempPassword) {
		this.tempPassword = tempPassword;
	}

	@Override
	public String toString() {
		return username;
	}
}
