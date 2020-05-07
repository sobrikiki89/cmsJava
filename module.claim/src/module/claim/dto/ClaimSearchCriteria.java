package module.claim.dto;

import java.io.Serializable;
import java.util.Date;

import module.claim.model.ClaimStatusEnum;

public class ClaimSearchCriteria implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long companyId;

	private String contractor;

	private String insurerCode;

	private String insuranceClassCode;

	private String policyNo;

	private ClaimStatusEnum claimStatus;

	private Date startDate;

	private Date endDate;

	private String cmsRefNo;

	private Date fromLossDate;

	private Date toLossDate;

	private String claimNo;

	private Boolean deletedOnly;

	private String insurerRef;

	private String vehicleRegNo;

	private long adjusterId;

	private long solicitorId;

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getContractor() {
		return contractor;
	}

	public void setContractor(String contractor) {
		this.contractor = contractor;
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

	public ClaimStatusEnum getClaimStatus() {
		return claimStatus;
	}

	public void setClaimStatus(ClaimStatusEnum claimStatus) {
		this.claimStatus = claimStatus;
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

	public String getCmsRefNo() {
		return cmsRefNo;
	}

	public void setCmsRefNo(String cmsRefNo) {
		this.cmsRefNo = cmsRefNo;
	}

	public Date getFromLossDate() {
		return fromLossDate;
	}

	public void setFromLossDate(Date fromLossDate) {
		this.fromLossDate = fromLossDate;
	}

	public Date getToLossDate() {
		return toLossDate;
	}

	public void setToLossDate(Date toLossDate) {
		this.toLossDate = toLossDate;
	}

	public String getClaimNo() {
		return claimNo;
	}

	public void setClaimNo(String claimNo) {
		this.claimNo = claimNo;
	}

	public Boolean getDeletedOnly() {
		return deletedOnly;
	}

	public void setDeletedOnly(Boolean deletedOnly) {
		this.deletedOnly = deletedOnly;
	}

	public String getInsurerRef() {
		return insurerRef;
	}

	public void setInsurerRef(String insurerRef) {
		this.insurerRef = insurerRef;
	}

	public String getVehicleRegNo() {
		return vehicleRegNo;
	}

	public void setVehicleRegNo(String vehicleRegNo) {
		this.vehicleRegNo = vehicleRegNo;
	}

	public long getAdjusterId() {
		return adjusterId;
	}

	public void setAdjusterId(long adjusterId) {
		this.adjusterId = adjusterId;
	}

	public long getSolicitorId() {
		return solicitorId;
	}

	public void setSolicitorId(long solicitorId) {
		this.solicitorId = solicitorId;
	}

}
