package module.policy.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

import app.core.dto.DTOBase;

public class PolicyRelatedClaimDTO extends DTOBase implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String policyNo;
	
	private String insuranceClass;
	
	private Date startDate;
	
	private Date endDate;
	
	private BigDecimal sumInsured;
	
	private String dropDownLabel;

	DecimalFormat df = new DecimalFormat("#,###.00");
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getInsuranceClass() {
		return insuranceClass;
	}

	public void setInsuranceClass(String insuranceClass) {
		this.insuranceClass = insuranceClass;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getSumInsured() {
		return sumInsured.setScale(0, RoundingMode.DOWN);
	}

	public void setSumInsured(BigDecimal sumInsured) {
		this.sumInsured = sumInsured;
	}

	public String getDropDownLabel() {
		return dropDownLabel;
	}

	public void setDropDownLabel(String dropDownLabel) {
		this.dropDownLabel = dropDownLabel;
	}

	
}
