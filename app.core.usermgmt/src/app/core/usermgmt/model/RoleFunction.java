package app.core.usermgmt.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import app.core.model.EntityBase;


/**
 * Simple link object to relate Role objects with Function objects.
 */
@Entity
@Table(name = "ROLE_FUNCTION")
public class RoleFunction extends EntityBase {

	private static final long serialVersionUID = 1L;

	private RoleFunctionPK pk;
	private Role role = new Role();
	private Function function = new Function();
	private Boolean createable;
	private Boolean readable;
	private Boolean updateable;
	private Boolean deleteable;

	// Constructors

	/** default constructor */
	public RoleFunction() {
	}

	/** constructor with id */
	public RoleFunction(RoleFunctionPK pk) {
		this.pk = pk;
	}

	// Property accessors

	/**
	 * The ID of this object is a compound key, represented by a RoleFunctionId
	 * instance.
	 */
	@EmbeddedId
	public RoleFunctionPK getPk() {
		return this.pk;
	}

	public void setPk(RoleFunctionPK pk) {
		this.pk = pk;
	}

	/**
	 * Get the Role associated with this object.
	 * <p>
	 * The Role is part of the key of this object so it can't be updated.
	 */
	@ManyToOne
	@JoinColumn(name = "ROLE_ID", insertable = false, updatable = false)
	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	/**
	 * Get the Function associated with this object.
	 * <p>
	 * The Function is part of the key of this object so it can't be updated.
	 */
	@ManyToOne
	@JoinColumn(name = "FUNCTION_CODE", insertable = false, updatable = false)
	public Function getFunction() {
		return this.function;
	}

	public void setFunction(Function function) {
		this.function = function;
	}

	/**
	 * specify if the role can create inside that function The value is 'Y' or
	 * 'N' so the Hibernate specified type yes_no is appropriate 
	 * TODO: check how useful is this stuff. Display or not the Create button on the page
	 * according to this property? Any other way to do this security control?
	 */
	@Column(name = "CREATEABLE")
	public Boolean getCreateable() {
		return this.createable;
	}

	public void setCreateable(Boolean createable) {
		this.createable = createable;
	}

	/**
	 * TODO: what is this???? Why would you not be able to read the execution of
	 * a function if you cannot access to it?
	 * 
	 */
	@Column(name = "READABLE")
	public Boolean getReadable() {
		return this.readable;
	}

	public void setReadable(Boolean readable) {
		this.readable = readable;
	}

	/**
	 * TODO: see getCreateable()
	 */
	@Column(name = "UPDATEABLE")
	public Boolean getUpdateable() {
		return this.updateable;
	}

	public void setUpdateable(Boolean updateable) {
		this.updateable = updateable;
	}

	/**
	 * TODO: see getCreateable()
	 */
	@Column(name = "DELETEABLE")
	public Boolean getDeleteable() {
		return this.deleteable;
	}

	public void setDeleteable(Boolean deleteable) {
		this.deleteable = deleteable;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			RoleFunction roleFunction = (RoleFunction) obj;

			if (this.pk == null || pk.getFunctionCode() == null
					|| pk.getRoleId() == null || roleFunction == null) {
				return false;
			} else {
				return pk.getFunctionCode().equals(
						roleFunction.getPk().getFunctionCode())
						&& pk.getRoleId().equals(
								roleFunction.getPk().getRoleId());
			}
		} catch (Exception e) {
			return false;
		}
	}
}