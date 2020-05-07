package web.module.policy.model;

import java.math.BigDecimal;
import java.util.List;

import module.policy.model.Policy;
import module.policy.model.PolicyEndorsement;
import module.policy.model.PolicyFile;
import module.setup.model.Company;
import module.setup.model.InsuranceClass;
import module.setup.model.Insurer;
import module.upload.model.UploadedFile;
import web.core.model.AbstractForm;

public class PolicySetupForm extends AbstractForm {

	private static final long serialVersionUID = 1L;

	private Policy policy;

	private List<Insurer> insurers;

	private List<InsuranceClass> insuranceClasses;

	private List<Company> companies;

	private List<PolicyFile> policyFiles;

	private List<UploadedFile> filesList;

	private boolean allowedEditPolicyNo;

	// batch 3 enhancement
	private BigDecimal totalGross;

	private BigDecimal totalRebate;

	private BigDecimal totalNet;

	private BigDecimal totalSumInsured;

	private BigDecimal totalTax;

	private List<PolicyEndorsement> policyEndorsements;

	private BigDecimal endorsementTotalGross;

	private BigDecimal endorsementTotalRebate;

	private BigDecimal endorsementTotalNet;

	private BigDecimal endorsementTotalSumInsured;

	private BigDecimal endorsementTotalTax;
	
	private boolean allowedDeleteEndorsement;

	private BigDecimal totalStampDuty;

	private BigDecimal endorsementTotalStampDuty;

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}

	public List<Insurer> getInsurers() {
		return insurers;
	}

	public void setInsurers(List<Insurer> insurers) {
		this.insurers = insurers;
	}

	public List<InsuranceClass> getInsuranceClasses() {
		return insuranceClasses;
	}

	public void setInsuranceClasses(List<InsuranceClass> insuranceClasses) {
		this.insuranceClasses = insuranceClasses;
	}

	public List<Company> getCompanies() {
		return companies;
	}

	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}

	public List<PolicyFile> getPolicyFiles() {
		return policyFiles;
	}

	public void setPolicyFiles(List<PolicyFile> policyFiles) {
		this.policyFiles = policyFiles;
	}

	public List<UploadedFile> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<UploadedFile> filesList) {
		this.filesList = filesList;
	}

	public boolean isAllowedEditPolicyNo() {
		return allowedEditPolicyNo;
	}

	public void setAllowedEditPolicyNo(boolean allowedEditPolicyNo) {
		this.allowedEditPolicyNo = allowedEditPolicyNo;
	}

	public BigDecimal getTotalGross() {
		return totalGross;
	}

	public void setTotalGross(BigDecimal totalGross) {
		this.totalGross = totalGross;
	}

	public BigDecimal getTotalRebate() {
		return totalRebate;
	}

	public void setTotalRebate(BigDecimal totalRebate) {
		this.totalRebate = totalRebate;
	}

	public BigDecimal getTotalNet() {
		return totalNet;
	}

	public void setTotalNet(BigDecimal totalNet) {
		this.totalNet = totalNet;
	}

	public BigDecimal getTotalSumInsured() {
		return totalSumInsured;
	}

	public void setTotalSumInsured(BigDecimal totalSumInsured) {
		this.totalSumInsured = totalSumInsured;
	}

	public BigDecimal getTotalTax() {
		return totalTax;
	}

	public void setTotalTax(BigDecimal totalTax) {
		this.totalTax = totalTax;
	}

	public List<PolicyEndorsement> getPolicyEndorsements() {
		return policyEndorsements;
	}

	public void setPolicyEndorsements(List<PolicyEndorsement> policyEndorsements) {
		this.policyEndorsements = policyEndorsements;
	}

	public BigDecimal getEndorsementTotalGross() {
		return endorsementTotalGross;
	}

	public void setEndorsementTotalGross(BigDecimal endorsementTotalGross) {
		this.endorsementTotalGross = endorsementTotalGross;
	}

	public BigDecimal getEndorsementTotalRebate() {
		return endorsementTotalRebate;
	}

	public void setEndorsementTotalRebate(BigDecimal endorsementTotalRebate) {
		this.endorsementTotalRebate = endorsementTotalRebate;
	}

	public BigDecimal getEndorsementTotalNet() {
		return endorsementTotalNet;
	}

	public void setEndorsementTotalNet(BigDecimal endorsementTotalNet) {
		this.endorsementTotalNet = endorsementTotalNet;
	}

	public BigDecimal getEndorsementTotalSumInsured() {
		return endorsementTotalSumInsured;
	}

	public void setEndorsementTotalSumInsured(BigDecimal endorsementTotalSumInsured) {
		this.endorsementTotalSumInsured = endorsementTotalSumInsured;
	}

	public BigDecimal getEndorsementTotalTax() {
		return endorsementTotalTax;
	}

	public void setEndorsementTotalTax(BigDecimal endorsementTotalTax) {
		this.endorsementTotalTax = endorsementTotalTax;
	}

	public boolean isAllowedDeleteEndorsement() {
		return allowedDeleteEndorsement;
	}

	public void setAllowedDeleteEndorsement(boolean allowedDeleteEndorsement) {
		this.allowedDeleteEndorsement = allowedDeleteEndorsement;
	}

	public BigDecimal getTotalStampDuty() {
		return totalStampDuty;
	}

	public void setTotalStampDuty(BigDecimal totalStampDuty) {
		this.totalStampDuty = totalStampDuty;
	}

	public BigDecimal getEndorsementTotalStampDuty() {
		return endorsementTotalStampDuty;
	}

	public void setEndorsementTotalStampDuty(BigDecimal endorsementTotalStampDuty) {
		this.endorsementTotalStampDuty = endorsementTotalStampDuty;
	}

}
