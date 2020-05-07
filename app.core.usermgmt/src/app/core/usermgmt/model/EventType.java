package app.core.usermgmt.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import app.core.model.EntityBase;

/**
 * Represents a grouping of Event records. An Event object represents a kind of
 * event, and they are grouped into categories by this class.
 */
@Entity
@Table(name = "EVENT_TYPE")
public class EventType extends EntityBase {

	private static final long serialVersionUID = 1L;

	private String code;
	private String description;
	private EventCategory category;
	private Severity severity = new Severity();

	// Constructors

	/** default constructor */
	public EventType() {
	}

	/** constructor with id */
	public EventType(String code) {
		this.code = code;
	}

	// Property accessors

	/**
     * 
     */
	@Id
	@Column(name = "CODE")
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/**
     * 
     */
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne
	@JoinColumn(name = "SEVERITY_CODE")
	public Severity getSeverity() {
		return this.severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	/**
	 * get the Category of this Type
	 */
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "EVENT_CATEGORY_CODE")
	public EventCategory getCategory() {
		return this.category;
	}

	public void setCategory(EventCategory category) {
		this.category = category;
	}
}