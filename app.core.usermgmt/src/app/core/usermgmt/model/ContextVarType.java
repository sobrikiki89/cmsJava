package app.core.usermgmt.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import app.core.model.EntityBase;

/**
 * ContextVarType defines a possible user-configuration setting which affects
 * the way the application appears to a user.
 * <p>
 * A user then has a set of UserContextVar objects associated with it, where
 * each UserContextVar has a Type - one of the values defined here.
 */
@Entity
@Table(name = "CONTEXT_VAR_TYPE")
public class ContextVarType extends EntityBase {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private String description;
	private List<UserContextVar> userContextVars;

	// Constructors

	/** default constructor */
	public ContextVarType() {
	}

	/** constructor with id */
	public ContextVarType(Long id) {
		this.id = id;
	}

	// Property accessors

	/** Synthetic Id */
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
	 * The name used to refer to this Type definition.
	 */
	@Column(name = "NAME")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * A description of the purpose of this Type definition.
	 */
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the set of variables (for all users) which have this type. This isn't
	 * a very common operation (in fact, it's rather an odd thing to do) so the
	 * relation is marked as lazy.
	 * <p>
	 * Note that the var type is part of the key of this object, so it can't be
	 * modified.
	 * <p>
	 * Cascading is enabled, so deleting a ContextVarType will delete all
	 * UserContextVar objects with that ContextVarType.
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "CONTEXT_VAR_TYPE_ID", insertable = false, updatable = false)
	public List<UserContextVar> getUserContextVars() {
		return this.userContextVars;
	}

	public void setUserContextVars(List<UserContextVar> userContextVars) {
		this.userContextVars = userContextVars;
	}
}