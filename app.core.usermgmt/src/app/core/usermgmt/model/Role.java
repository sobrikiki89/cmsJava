package app.core.usermgmt.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import app.core.model.EntityReference;



/**
 * Represents a security-related role that this user has. Specific functionality
 * requires specific roles in order for a user to be allowed access.
 */
@Entity
@Table(name = "APP_ROLE")
public class Role extends EntityReference {

	private static final long serialVersionUID = 1L;

	private boolean associated;
	private List<RoleFunction> roleFunctions;
	private List<UserRole> userRoles;
	
	private List<RoleFunction> requestedRoleFunctions;
	private String description;

	// Constructors

	/** default constructor */
	public Role() {
	}

	/** constructor with id */
	public Role(Long id) {
		this.id = id;
	}

	// Property accessors

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get all the RoleFunctions associated with this role. From a RoleFunction
	 * you can find a Function that requires this Role in order to be accessed.
	 * <p>
	 * There are potentially many Functions with the same role, so this
	 * association has been declared lazy.
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "ROLE_ID")
	public List<RoleFunction> getRoleFunctions() {
		return this.roleFunctions;
	}

	public void setRoleFunctions(List<RoleFunction> roleFunctions) {
		this.roleFunctions = roleFunctions;
	}

	/**
	 * Get the UserRole objects associated with this Role. From a UserRole you
	 * can find a User that has this Role.
	 * <p>
	 * Potentially many users have the same Role, so this association has been
	 * declared lazy.
	 */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROLE_ID")
	public List<UserRole> getUserRoles() {
		return this.userRoles;
	}

	public void setUserRoles(List<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	@Transient
	public boolean isAssociated() {
		return associated;
	}

	public void setAssociated(boolean associated) {
		this.associated = associated;
	}

	@Transient
	public List<RoleFunction> getRequestedRoleFunctions() {
		return requestedRoleFunctions;
	}

	public void setRequestedRoleFunctions(
			List<RoleFunction> requestedRoleFunctions) {
		this.requestedRoleFunctions = requestedRoleFunctions;
	}

	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof Role))
			return false;

		Role castOther = (Role) other;
		return this.id.equals(castOther.id);
	}

}