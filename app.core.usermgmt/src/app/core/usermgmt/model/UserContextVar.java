package app.core.usermgmt.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import app.core.model.EntityBase;

/**
 * Represents a view-related configuration setting for a specific user. The set
 * of ContextVarType entities represents all the possible view configuration
 * settings for the user; each one that the user has actually got a specific
 * setting for is represented by a UserContextVar object.
 */
@Entity
@Table(name = "USER_CONTEXT_VAR")
public class UserContextVar extends EntityBase {

	private static final long serialVersionUID = 1L;

	private UserContextVarId id;
	private User user;
	private ContextVarType contextVarType;
	private String value;

	// Constructors

	/** default constructor */
	public UserContextVar() {
	}

	/** constructor with id */
	public UserContextVar(UserContextVarId id) {
		this.id = id;
	}

	// Property accessors

	/**
	 * Compound key.
	 */
	@EmbeddedId
	public UserContextVarId getId() {
		return this.id;
	}

	public void setId(UserContextVarId id) {
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
	 * Get the type of this context variable.
	 * <p>
	 * Note that the var type is part of the key of this object, so it can't be
	 * modified.
	 */
	@ManyToOne
	@JoinColumn(name = "CONTEXT_VAR_TYPE_ID", insertable = false, updatable = false)
	public ContextVarType getContextVarType() {
		return this.contextVarType;
	}

	public void setContextVarType(ContextVarType contextVarType) {
		this.contextVarType = contextVarType;
	}

	/**
	 * Get the value for this particular view configuration setting.
	 */
	@Column(name = "VALUE")
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}