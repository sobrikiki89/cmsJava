package app.core.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/* The purpose of this class id to hold the mapping for all the code tables.
 * A reference table always contains CODE,NAME,SORT_ORDER
 * An Entity will only have to extend this class and just 
 * add a @Table annotation and change the Sequence_Generator
 */

@MappedSuperclass
public class EntityCode extends EntityBase {

	private static final long serialVersionUID = 1L;

	protected String code;
	protected String name;
	protected Long sortOrder;

	// default constructor
	public EntityCode() {
	}

	// minimal constructor
	public EntityCode(String code, String name) {
		this.code = code;
		this.name = name;
	}

	// minimal constructor
	public EntityCode(String code) {
		this.code = code;
	}

	@Id
	@Column(name = "code", length = 20)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		if (code != null) {
			this.code = code.toUpperCase();
		} else {
			this.code = null;
		}
	}

	@Column(name = "name", length = 100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "SORT_ORDER")
	public Long getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public String toString() {
		return code + "(" + name + ")";
	}

	@Override
	public boolean equals(Object obj) {
		try {
			EntityCode codeObj = (EntityCode) obj;
			if (this.code == null || codeObj.getCode() == null)
				return false;
			else
				return this.code.equals(codeObj.getCode());
		} catch (Exception e) {
			return false;
		}
	}
}
