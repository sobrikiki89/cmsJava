package module.setup.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import app.core.model.EntityCodeActive;

@Entity
@Table(name = "INSURANCE_CLASS")
public class InsuranceClass extends EntityCodeActive {

	private static final long serialVersionUID = 1L;

	private InsuranceClassCategory category;

	@ManyToOne(optional=true)
	@JoinColumn(name = "CATEGORY")
	public InsuranceClassCategory getCategory() {
		return category;
	}

	public void setCategory(InsuranceClassCategory category) {
		this.category = category;
	}
	
	@Transient
	public String getDropdownLabel() {
		return "(" + getCode() + ") " + StringUtils.defaultString(getName());
	}

}
