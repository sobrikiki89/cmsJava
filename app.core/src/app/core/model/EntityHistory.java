package app.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import app.core.annotation.CurrentTemporal;
import app.core.annotation.CurrentUser;
import app.core.annotation.PersistenceType;

/**
 * Stores the history associated with a persistent entity.
 * <p>
 * Almost every database table has the following columns: CREUSER, CREDATE,
 * UPDUSER, UPDDATE. These indicate who created the record and when, and who
 * last updated the record and when. This class represents these common fields;
 * other Entity classes just declare a one-to-one relation with this entity, and
 * because this is marked embeddable it will be persisted into the same table as
 * the referencing entity.
 * <p>
 * Note that there is no real need for tracing from the history info to the
 * actual User object, so this object deals in user IDs rather than references
 * to real User objects.
 */

@MappedSuperclass
public class EntityHistory extends EntityBase {

	private static final long serialVersionUID = 1L;

	private Long createUserId;
	private Date createDate;
	private Long updateUserId;
	private Date updateDate;

	public EntityHistory() {
		super();
	}

	public EntityHistory(Long userId) {
		this.createUserId = userId;
		this.updateUserId = userId;
		this.createDate = new Date();
		this.updateDate = new Date();
	}

	/**
	 * The user who created the parent entity.
	 */
	@CurrentUser(type = PersistenceType.CREATE)
	@Column(name = "CREUSER", nullable = false, updatable = false)
	public Long getCreateUserId() {
		return this.createUserId;
	}

	public void setCreateUserId(Long createUser) {
		this.createUserId = createUser;
	}

	/**
	 * The date at which the parent entity was first persisted into the
	 * database.
	 */
	@CurrentTemporal(type = PersistenceType.CREATE)
	@Column(name = "CREDATE", nullable = false, updatable = false, insertable = true)
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * The user who last updated the parent entity.
	 */
	@CurrentUser(type = PersistenceType.MODIFY)
	@Column(name = "UPDUSER", updatable = true, insertable = true)
	public Long getUpdateUserId() {
		return this.updateUserId;
	}

	public void setUpdateUserId(Long updateUser) {
		this.updateUserId = updateUser;
	}

	/**
	 * The date at which the parent entity was last updated in the database.
	 */
	@CurrentTemporal(type = PersistenceType.MODIFY)
	@Column(name = "UPDDATE", updatable = true, insertable = true)
	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@Override
	public String toString() {
		return createUserId + " (" + createDate + ") / " + updateUserId + " (" + updateDate + ")";
	}
}