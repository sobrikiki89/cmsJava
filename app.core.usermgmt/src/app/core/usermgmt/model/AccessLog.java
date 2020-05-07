package app.core.usermgmt.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import app.core.model.EntityBase;

/**
 * Used for debugging to trace the URL accesses made by a specific user.
 * <p>
 * When trying to resolve a problem encountered by a specific user, a specific
 * UserContextVar can be added to that user's set of context info which will
 * then cause a filter in the application to record each URL accessed by the
 * user as an AccessLog instance. A developer can then inspect this info to
 * figure out what the problem is.
 */
@Entity
@Table(name = "ACCESS_LOG")
public class AccessLog extends EntityBase {

	private static final long serialVersionUID = 1L;

	private Long id;

	private User user;

	private Date accessed;

	private String requestUrl;

	private String requestParams;

	// Constructors

	/** default constructor */
	public AccessLog() {
	}

	/** constructor with id */
	public AccessLog(Long id) {
		this.id = id;
	}

	// Property accessors

	/** Synthetic id */
	@Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "entityKeys")
	@Column(name = "ID")
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/** User who accessed this URL */
	@ManyToOne
	@JoinColumn(name = "USER_ID")
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/** date/time the URL was accessed */
	@Column(name = "ACCESSED_DATE")
	public Date getAccessedDate() {
		return this.accessed;
	}

	public void setAccessedDate(Date accessed) {
		this.accessed = accessed;
	}

	/** URL that was accessed. */
	@Column(name = "REQUEST_URL")
	public String getRequestUrl() {
		return this.requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	/** Any URL request parameters appended to the URL */
	@Column(name = "REQUEST_PARAMS")
	public String getRequestParams() {
		return this.requestParams;
	}

	public void setRequestParams(String requestParams) {
		this.requestParams = requestParams;
	}
}
