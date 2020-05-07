package app.core.usermgmt.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import app.core.model.EntityCode;

/**
 * Defines the categories of events that can occur. Actual occurrences of events
 * of this type are represented by EventLog instances. Event kinds are grouped
 * into a larger type hierarchy via the EventType class.
 */
@Entity
@Table(name = "EVENT_CATEGORY")
public class EventCategory extends EntityCode {

	private static final long serialVersionUID = 1L;

}