package app.core.usermgmt.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import app.core.model.EntityCode;

/**
 * Represents a severity rating for a logged event.
 */
@Entity
@Table(name = "SEVERITY")
public class Severity extends EntityCode {

	private static final long serialVersionUID = 1L;
	
	private List<EventCategory> categories;

    // Constructors

    /** default constructor */
    public Severity() {
    }

    /** constructor with id */
    public Severity(String code) {
        this.code = code;
    }
    
    /**
     * Get all events with this severity. 
     * <p>
     * This is not a common operation, and is potentially very
     * expensive so this association is set LAZY. 
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "SEVERITY_CODE")
    public List<EventCategory> getEvents() {
        return this.categories;
    }

    public void setEvents(List<EventCategory> categories) {
        this.categories = categories;
    }
}