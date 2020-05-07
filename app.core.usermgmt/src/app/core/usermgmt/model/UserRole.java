package app.core.usermgmt.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import app.core.model.EntityBase;

/**
 * Simple link class between a User entity and a Role entity. A user has a set
 * of associated roles which define what security access rights that user has.
 */
@Entity
@Table(name = "USER_ROLE")
public class UserRole extends EntityBase {

	private static final long serialVersionUID = 1L;

	private UserRoleId id;
	private User user;
	private Role role;
	private Date startDate;
	private Date endDate;

	// Constructors

	/** default constructor */
	public UserRole() {
	}

	/** constructor with id */
	public UserRole(UserRoleId id) {
		this.id = id;
	}

	/** constructor with id */
	public UserRole(Long userId, Long roleId) {

		this.id = new UserRoleId(userId, roleId);
	}

	// Property accessors

	/**
	 * Get compound key for this object.
	 */
	@EmbeddedId
	public UserRoleId getId() {
		return this.id;
	}

	public void setId(UserRoleId id) {
		this.id = id;
	}

	/**
	 * Get the user associated with this object.
	 * <p>
	 * Note that the user_id is part of the key of this object, so it can't be
	 * modified.
	 */
	@ManyToOne
	@JoinColumn(name = "USER_ID", insertable = false, updatable = false)
	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Get the role associated with this object.
	 * <p>
	 * Note that the role_id is part of the key of this object, so it can't be
	 * modified.
	 */
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "ROLE_ID", insertable = false, updatable = false)
	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	/**
	 * The date this user starts to have this role. default to new Date()
	 */
	@Column(name = "START_DATE")
	public Date getStartDate() {
		if (startDate == null)
			startDate = new Date();
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * The date this user ceases to have this role.
	 */
	@Column(name = "END_DATE")
	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}