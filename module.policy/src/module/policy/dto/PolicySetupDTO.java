package module.policy.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import app.core.dto.DTOBase;
import app.core.json.JsonDateSerializer;
import app.core.json.JsonNumberSerializer;

public class PolicySetupDTO extends DTOBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String policyNo;

	private String insuranceClassCode;

	private String companyName;

	private String insurerName;

	private BigDecimal sumInsured;

	private BigDecimal premiumGross;

	private Date startDate;

	private Date endDate;

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

	public String getInsuranceClassCode() {
		return insuranceClassCode;
	}

	public void setInsuranceClassCode(String insuranceClassCode) {
		this.insuranceClassCode = insuranceClassCode;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getInsurerName() {
		return insurerName;
	}

	public void setInsurerName(String insurerName) {
		this.insurerName = insurerName;
	}

	@JsonSerialize(using = JsonNumberSerializer.class)
	public BigDecimal getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(BigDecimal sumInsured) {
		this.sumInsured = sumInsured;
	}

	@JsonSerialize(using = JsonNumberSerializer.class)
	public BigDecimal getPremiumGross() {
		return premiumGross;
	}

	public void setPremiumGross(BigDecimal premiumGross) {
		this.premiumGross = premiumGross;
	}	

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	public Date getEndDate() {

		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
