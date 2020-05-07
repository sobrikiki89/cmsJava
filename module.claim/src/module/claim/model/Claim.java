package module.claim.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import app.core.domain.setup.model.Adjuster;
import app.core.domain.setup.model.Contact;
import app.core.domain.setup.model.Solicitor;
import app.core.model.EntityHistory;
import module.policy.model.Policy;
import module.setup.model.CompanyDepartment;
import module.setup.model.LossType;

@Entity
@Table(name = "CLAIM")
public class Claim extends EntityHistory {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String claimNo;

	private Policy policy;

	private String otherPolicyRef;

	private String cmsRefNo;

	private Date notifyDate;

	private Date lossDate;

	private String vehicleRegNo;

	private String lossLocation;

	private String lossDescription;

	private LossType lossType;

	private String contractor;

	private CompanyDepartment department;

	private BigDecimal estLostAmount;

	private BigDecimal excessAmount;

	private String insuredRef;

	private Contact insuredContact;

	private String insurerRef;

	private String adjusterRef;

	private Contact adjusterContact;

	private ClaimStatusEnum status;

	// A.K.A. Offer Date
	private Date approvalDate;

	private BigDecimal offerAmount;

	private Date paidDate;

	// A.K.A. Paid Amount
	private BigDecimal approvalAmount;
	
	private Date docCompletionDate;

	private String remark;

	private List<ClaimRemark> remarks;

	private List<ClaimRelatedPolicy> relatedPolicy;

	private Boolean deleteApproval;

	private String companyCode;

	private Boolean deleted;
	
	private BigDecimal claimReservedAmount; //A.K.A estimated loss amount

	private Adjuster adjuster;

	private Solicitor solicitor;

	private String solicitorRef;

	private Contact solicitorContact;

	private Date adjFinalReportDate;

	private String outstandingDoc;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "entityKeys")
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "CLAIM_NO", nullable = false)
	public String getClaimNo() {
		return claimNo;
	}

	public void setClaimNo(String claimNo) {
		this.claimNo = claimNo;
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "POLICY_ID", nullable = false)
	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}

	@Column(name = "OTHER_POLICY_REFERENCE")
	public String getOtherPolicyRef() {
		return otherPolicyRef;
	}

	public void setOtherPolicyRef(String otherPolicyRef) {
		this.otherPolicyRef = otherPolicyRef;
	}

	@Column(name = "CMS_REFERENCE", nullable = true)
	public String getCmsRefNo() {
		return cmsRefNo;
	}

	public void setCmsRefNo(String cmsRefNo) {
		this.cmsRefNo = cmsRefNo;
	}

	@Column(name = "NOTIFY_DATE", nullable = false)
	public Date getNotifyDate() {
		return notifyDate;
	}

	public void setNotifyDate(Date notifyDate) {
		this.notifyDate = notifyDate;
	}

	@Column(name = "VEHICLE_REG_NO")
	public String getVehicleRegNo() {
		return vehicleRegNo;
	}

	public void setVehicleRegNo(String vehicleRegNo) {
		this.vehicleRegNo = vehicleRegNo;
	}

	@Column(name = "LOSS_DATE", nullable = false)
	public Date getLossDate() {
		return lossDate;
	}

	public void setLossDate(Date lossDate) {
		this.lossDate = lossDate;
	}

	@Column(name = "LOSS_LOC")
	public String getLossLocation() {
		return lossLocation;
	}

	public void setLossLocation(String lossLocation) {
		this.lossLocation = lossLocation;
	}

	@ManyToOne
	@JoinColumn(name = "LOSS_TYPE_CODE", nullable = false)
	public LossType getLossType() {
		return lossType;
	}

	public void setLossType(LossType lossType) {
		this.lossType = lossType;
	}

	@Lob
	@Column(name = "LOSS_DESC")
	public String getLossDescription() {
		return lossDescription;
	}

	public void setLossDescription(String lossDescription) {
		this.lossDescription = lossDescription;
	}

	@Column(name = "CONTRACTOR", nullable = false)
	public String getContractor() {
		return contractor;
	}

	public void setContractor(String contractor) {
		this.contractor = contractor;
	}

	@ManyToOne
	@JoinColumn(name = "DEPARTMENT_ID")
	public CompanyDepartment getDepartment() {
		return department;
	}

	public void setDepartment(CompanyDepartment department) {
		this.department = department;
	}

	@Column(name = "EST_LOST_AMT")
	public BigDecimal getEstLostAmount() {
		return estLostAmount;
	}

	public void setEstLostAmount(BigDecimal estLostAmount) {
		this.estLostAmount = estLostAmount;
	}

	@Column(name = "EXCESS_AMT")
	public BigDecimal getExcessAmount() {
		return excessAmount;
	}

	public void setExcessAmount(BigDecimal excessAmount) {
		this.excessAmount = excessAmount;
	}

	@Column(name = "INSURED_REF")
	public String getInsuredRef() {
		return insuredRef;
	}

	public void setInsuredRef(String insuredRef) {
		this.insuredRef = insuredRef;
	}

	@ManyToOne
	@JoinColumn(name = "INSURED_CONTACT_ID")
	public Contact getInsuredContact() {
		return insuredContact;
	}

	public void setInsuredContact(Contact insuredContact) {
		this.insuredContact = insuredContact;
	}

	@Column(name = "INSURER_REF")
	public String getInsurerRef() {
		return insurerRef;
	}

	public void setInsurerRef(String insurerRef) {
		this.insurerRef = insurerRef;
	}

	@Column(name = "ADJUSTER_REF")
	public String getAdjusterRef() {
		return adjusterRef;
	}

	public void setAdjusterRef(String adjusterRef) {
		this.adjusterRef = adjusterRef;
	}

	@ManyToOne
	@JoinColumn(name = "ADJUSTER_CONTACT_ID")
	public Contact getAdjusterContact() {
		return adjusterContact;
	}

	public void setAdjusterContact(Contact adjusterContact) {
		this.adjusterContact = adjusterContact;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS", nullable = false)
	public ClaimStatusEnum getStatus() {
		return status;
	}

	public void setStatus(ClaimStatusEnum status) {
		this.status = status;
	}

	// A.K.A Offer Date
	@Column(name = "APPROVAL_DATE")
	public Date getApprovalDate() {
		return approvalDate;
	}

	public void setApprovalDate(Date approvalDate) {
		this.approvalDate = approvalDate;
	}

	@Column(name = "OFFER_AMT")
	public BigDecimal getOfferAmount() {
		return offerAmount;
	}

	public void setOfferAmount(BigDecimal offerAmount) {
		this.offerAmount = offerAmount;
	}

	@Column(name = "PAID_DATE")
	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	// A.K.A Paid Amount
	@Column(name = "APPROVAL_AMT")
	public BigDecimal getApprovalAmount() {
		return approvalAmount;
	}

	public void setApprovalAmount(BigDecimal approvalAmount) {
		this.approvalAmount = approvalAmount;
	}

	@Column(name = "DOC_COMPLETION_DATE")
	public Date getDocCompletionDate() {
		return docCompletionDate;
	}

	public void setDocCompletionDate(Date docCompletionDate) {
		this.docCompletionDate = docCompletionDate;
	}

	@Column(name = "REMARK", length = 1000)
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "CLAIM_ID")
	public List<ClaimRemark> getRemarks() {
		return remarks;
	}

	public void setRemarks(List<ClaimRemark> remarks) {
		this.remarks = remarks;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "CLAIM_ID")
	public List<ClaimRelatedPolicy> getRelatedPolicy() {
		return relatedPolicy;
	}

	public void setRelatedPolicy(List<ClaimRelatedPolicy> relatedPolicy) {
		this.relatedPolicy = relatedPolicy;
	}

	@Column(name = "DELETE_APPROVAL")
	public Boolean isDeleteApproval() {
		return deleteApproval;
	}

	public void setDeleteApproval(Boolean deleteApproval) {
		this.deleteApproval = deleteApproval;
	}

	@Column(name = "COMPANY_CODE")
	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	@Column(name = "DELETED")
	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@Column(name = "RESERVE_AMOUNT")
	public BigDecimal getClaimReservedAmount() {
		return claimReservedAmount;
	}

	public void setClaimReservedAmount(BigDecimal claimReservedAmount) {
		this.claimReservedAmount = claimReservedAmount;
	}


	@ManyToOne(optional = true)
	@JoinColumn(name = "ADJUSTER_ID", foreignKey = @ForeignKey(name = "CLAIM_ADJUSTER_FK"), nullable = true)
	public Adjuster getAdjuster() {
		return adjuster;
	}

	public void setAdjuster(Adjuster adjuster) {
		this.adjuster = adjuster;
	}

	@ManyToOne(optional = true)
	@JoinColumn(name = "SOLICITOR_ID", foreignKey = @ForeignKey(name = "CLAIM_SOLICITOR_FK"), nullable = true)
	public Solicitor getSolicitor() {
		return solicitor;
	}

	public void setSolicitor(Solicitor solicitor) {
		this.solicitor = solicitor;
	}

	@Column(name = "SOLICITOR_REF", nullable = true)
	public String getSolicitorRef() {
		return solicitorRef;
	}

	public void setSolicitorRef(String solicitorRef) {
		this.solicitorRef = solicitorRef;
	}

	@ManyToOne(optional = true)
	@JoinColumn(name = "SOLICITOR_CONTACT_ID", foreignKey = @ForeignKey(name = "CLAIM_SOLICITOR_CONTACT_FK"), nullable = true)
	public Contact getSolicitorContact() {
		return solicitorContact;
	}

	public void setSolicitorContact(Contact solicitorContact) {
		this.solicitorContact = solicitorContact;
	}

	@Column(name = "ADJ_FINAL_REPORT_DATE")
	public Date getAdjFinalReportDate() {
		return adjFinalReportDate;
	}

	public void setAdjFinalReportDate(Date adjFinalReportDate) {
		this.adjFinalReportDate = adjFinalReportDate;
	}

	@Column(name = "OUTSTANDING_DOC", length = 1000, nullable = true)
	public String getOutstandingDoc() {
		return outstandingDoc;
	}

	public void setOutstandingDoc(String outstandingDoc) {
		this.outstandingDoc = outstandingDoc;
	}

}
