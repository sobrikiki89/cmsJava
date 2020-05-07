package module.claim.dto;

import java.io.Serializable;
import java.util.Date;

import app.core.dto.DTOBase;

public class ClaimSearchDTO extends DTOBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long claimId;

	private String claimNo;

	private String insuranceClassCode;

	private String contractor;

	private String status;

	private String statusCode;

	private Long policyId;

	private String policyNo;

	private Date notificationDate;

	private Date lossDate;

	private String lossType;

	private String lossTypeCode;

	private String cmsRefNo;

	private String solicitorFirmName;

	private String adjusterFirmName;

	private Boolean deleteApproval;

	private String createBy;

	private Date createDate;

	private String title;

	public Long getClaimId() {
		return claimId;
	}

	public void setClaimId(Long claimId) {
		this.claimId = claimId;
	}

	public String getClaimNo() {
		return claimNo;
	}

	public void setClaimNo(String claimNo) {
		this.claimNo = claimNo;
	}

	public String getInsuranceClassCode() {
		return insuranceClassCode;
	}

	public void setInsuranceClassCode(String insuranceClassCode) {
		this.insuranceClassCode = insuranceClassCode;
	}

	public String getContractor() {
		return contractor;
	}

	public void setContractor(String contractor) {
		this.contractor = contractor;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

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

	public Date getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
	}

	public Date getLossDate() {
		return lossDate;
	}

	public void setLossDate(Date lossDate) {
		this.lossDate = lossDate;
	}

	public String getLossType() {
		return lossType;
	}

	public void setLossType(String lossType) {
		this.lossType = lossType;
	}

	public String getLossTypeCode() {
		return lossTypeCode;
	}

	public void setLossTypeCode(String lossTypeCode) {
		this.lossTypeCode = lossTypeCode;
	}

	public String getCmsRefNo() {
		return cmsRefNo;
	}

	public void setCmsRefNo(String cmsRefNo) {
		this.cmsRefNo = cmsRefNo;
	}

	public String getSolicitorFirmName() {
		return solicitorFirmName;
	}

	public void setSolicitorFirmName(String solicitorFirmName) {
		this.solicitorFirmName = solicitorFirmName;
	}

	public String getAdjusterFirmName() {
		return adjusterFirmName;
	}

	public void setAdjusterFirmName(String adjusterFirmName) {
		this.adjusterFirmName = adjusterFirmName;
	}

	public Boolean getDeleteApproval() {
		return deleteApproval;
	}

	public void setDeleteApproval(Boolean deleteApproval) {
		this.deleteApproval = deleteApproval;
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
