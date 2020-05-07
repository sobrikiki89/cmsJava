package module.setup.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Id;

import app.core.model.EntityHistory;

public class Tax extends EntityHistory {

	private static final long serialVersionUID = 1L;

	private String code;

	private String name;
	
	private BigDecimal value;
	
	@Id
	@Column(name = "CODE")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// 6% = 0.06
	@Column(name = "VALUE", precision = 2, scale = 2)
	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

}
