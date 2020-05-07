package app.core.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/* The purpose of this class id to hold the mapping for all the reference tables.
 * A reference table always contains ID,NAME,SORT_ORDER
 * An Entity will only have to extend this class and just 
 * add a @Table annotation and change the Sequence_Generator
 */

@MappedSuperclass
public class EntityReference extends EntityBase {

	private static final long serialVersionUID = 1L;

	protected Long id;

	protected String name;

	protected Long sortOrder;

	// default constructor
	public EntityReference() {
	}

	// minimal constructor
	public EntityReference(String name) {
		this.name = name;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "entityKeys")
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
		return id + "(" + name + ")";
	}

	@Override
	public boolean equals(Object obj) {
		try {
			EntityReference refObj = (EntityReference) obj;
			if (this.id == null || refObj.getId() == null)
				return false;
			else
				return this.id.equals(refObj.getId());
		} catch (Exception e) {
			return false;
		}
	}
}
