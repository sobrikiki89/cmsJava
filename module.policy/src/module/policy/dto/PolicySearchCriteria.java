package module.policy.dto;

import java.io.Serializable;
import java.util.Date;

import org.joda.time.LocalDateTime;

public class PolicySearchCriteria implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long companyId;

	private String insurerCode;

	private String insuranceClassCode;

	private String policyNo;

	private Integer effectiveYear;

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getInsurerCode() {
		return insurerCode;
	}

	public void setInsurerCode(String insurerCode) {
		this.insurerCode = insurerCode;
	}

	public String getInsuranceClassCode() {
		return insuranceClassCode;
	}

	public void setInsuranceClassCode(String insuranceClassCode) {
		this.insuranceClassCode = insuranceClassCode;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public Integer getEffectiveYear() {
		return effectiveYear;
	}

	public void setEffectiveYear(Integer effectiveYear) {
		this.effectiveYear = effectiveYear;
	}

	public Date getEffectiveStartDate() {
		if (effectiveYear != null) {
			return LocalDateTime.now().toDateTime().withYear(effectiveYear).withDayOfYear(1).withTimeAtStartOfDay()
					.toDate();
		}
		return null;
	}

	public Date getEffectiveEndDate() {
		if (effectiveYear != null) {
			return LocalDateTime.now().toDateTime().withYear(effectiveYear).withDayOfYear(1).withTimeAtStartOfDay()
					.plusYears(1).minusMillis(1).toDate();
		}
		return null;
	}
}
