package app.core.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/* The purpose of this class id to hold the mapping for all the code tables.
 * A reference table always contains CODE,NAME,SORT_ORDER,ACTIVE_FLAG
 * An Entity will only have to extend this class and just 
 * add a @Table annotation and change the Sequence_Generator
 */

@MappedSuperclass
public class EntityCodeActive extends EntityCode {

	private static final long serialVersionUID = 1L;
	/**
	 * default activeFlag to true
	 */
	protected Boolean activeFlag = true;

	public EntityCodeActive() {
		super();
	}

	public EntityCodeActive(String code, String name) {
		super(code, name);
	}

	public EntityCodeActive(String code) {
		super(code);
	}

	@Column(name = "ACTIVE_FLAG", length = 20)
	public Boolean getActiveFlag() {
		return this.activeFlag;
	}

	public void setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
	}
}
