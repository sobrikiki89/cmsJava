package app.core.usermgmt.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import app.core.model.EntityBase;

/**
 * A Function is a logical feature that has associated security rules. Normally,
 * a Function corresponds to a "screen" in the end application.
 * <p>
 * An example of a Function is "create new user".
 */
@Entity
@Table(name = "APP_FUNCTION")
public class Function extends EntityBase {

	private static final long serialVersionUID = 1L;

	private String code;

	private String path;

	private String name;

	private String description;

	private List<RoleFunction> roleFunctions;

	// Constructors

	/** default constructor */
	public Function() {
	}

	/** constructor with code */
	public Function(String code) {
		this.code = code;
	}

	// Property accessors

	/** Synthetic Id */
	@Id
	@Column(name = "CODE")
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * The URL that brings up the screen associated with this Feature.
	 */
	@Column(name = "PATH")
	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * The human-readable name of this feature.
	 */
	@Column(name = "NAME")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * A description of this feature, eg "create new user"
	 */
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the RoleFunctions associated with this function; from a RoleFunction
	 * you can get a security Role that an end user must have in order to be
	 * allowed to access this feature. Set to LAZY because we shuld get the
	 * RoleFunction from a Role and not from the Function class
	 */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "FUNCTION_CODE")
	public List<RoleFunction> getRoleFunctions() {
		return this.roleFunctions;
	}

	public void setRoleFunctions(List<RoleFunction> roleFunctions) {
		this.roleFunctions = roleFunctions;
	}

	@Override
	public String toString() {
		return name + "(" + description + ")";
	}

	@Override
	public boolean equals(Object obj) {
		try {
			Function funcObj = (Function) obj;
			if (this.code == null || funcObj.getCode() == null)
				return false;
			else
				return this.code.equals(funcObj.getCode());
		} catch (Exception e) {
			return false;
		}
	}

}
