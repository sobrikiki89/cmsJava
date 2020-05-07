package module.setup.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import app.core.model.EntityCodeActive;

@Entity
@Table(name = "LOSS_TYPE")
public class LossType extends EntityCodeActive {

	private static final long serialVersionUID = 1L;
	
	@Transient
	public String getDropdownLabel() {
		return "(" + getCode() + ") " + StringUtils.defaultString(getName());
	}
}
