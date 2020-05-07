package app.core.usermgmt.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import app.core.model.EntityBase;

/**
 * Represents a grouping of Event records. An Event object represents a kind of
 * event, and they are grouped into categories by this class.
 */
@Entity
@Table(name = "USER_HOME_PAGE")
public class UserHomePage extends EntityBase {

	private static final long serialVersionUID = 1L;

	private String name;
	private String path;

	// Constructors

	/** default constructor */
	public UserHomePage() {
	}

	// Property accessors

	/**
     * 
     */
	@Id
	@Column(name = "NAME")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
     * 
     */
	@Column(name = "PATH")
	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}