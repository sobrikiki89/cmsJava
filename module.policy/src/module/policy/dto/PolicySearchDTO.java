package module.policy.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import app.core.dto.DTOBase;

public class PolicySearchDTO extends DTOBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long policyId;

	private String policyNo;

	private String insuranceClassCode;

	private String companyName;

	private Date startDate;

	private Date endDate;

	private String insurerCode;

	private String insurerName;

	private BigDecimal sumInsured;

	private BigDecimal premiumGross;
	
	private String createBy;
	
	private Date createDate;
	
	private String title;

	public Long getPolicyId() {
		return policyId;
	}

	public void setPolicyId(Long policyId) {
		this.policyId = policyId;
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

	public String getInsurerCode() {
		return insurerCode;
	}

	public void setInsurerCode(String insurerCode) {
		this.insurerCode = insurerCode;
	}

	public String getInsurerName() {
		return insurerName;
	}

	public void setInsurerName(String insurerName) {
		this.insurerName = insurerName;
	}

	public BigDecimal getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(BigDecimal sumInsured) {
		this.sumInsured = sumInsured;
	}

	public BigDecimal getPremiumGross() {
		return premiumGross;
	}

	public void setPremiumGross(BigDecimal premiumGross) {
		this.premiumGross = premiumGross;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
