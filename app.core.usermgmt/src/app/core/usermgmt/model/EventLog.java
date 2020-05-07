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
import javax.persistence.Transient;

import app.core.model.EntityBase;




/**
 * Represents a single occurrence of an event of interest.
 */
@Entity
@Table(name = "EVENT_LOG")
public class EventLog extends EntityBase {

	private static final long serialVersionUID = 1L;
	
	private Long id;
    private User appUser = new User();
    private EventType type = new EventType();
    private Date eventLogDate;
    private String note;
    
    // For the search date filtering
    private Date eventLogFromDate;
    private Date eventLogToDate;

    // Constructors

    /** default constructor */
    public EventLog() {
    }

    /** constructor with id */
    public EventLog(Long id) {
        this.id = id;
    }

    // Property accessors

    /**
     * 
     */
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
     * 
     */
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    public User getAppUser() {
        return this.appUser;
    }

    public void setAppUser(User appUser) {
        this.appUser = appUser;
    }

    /**
     * 
     */
    @ManyToOne
    @JoinColumn(name = "EVENT_TYPE_CODE")
    public EventType getType() {
        return this.type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    /**
     * 
     */
    @Column(name = "EVENT_LOG_DATE")
    public Date getEventLogDate() {
        return this.eventLogDate;
    }

    public void setEventLogDate(Date eventLogDate) {
        this.eventLogDate = eventLogDate;
    }

    /**
     * 
     */
    @Column(name = "NOTE")
    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    
    @Transient
    public Date getEventLogFromDate() {
		return eventLogFromDate;
	}

	public void setEventLogFromDate(Date eventLogFromDate) {
		this.eventLogFromDate = eventLogFromDate;
	}

	
    @Transient
	public Date getEventLogToDate() {
		return eventLogToDate;
	}

	public void setEventLogToDate(Date eventLogToDate) {
		this.eventLogToDate = eventLogToDate;
	}
}
