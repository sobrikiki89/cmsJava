package web.module.claim.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import module.claim.model.Claim;
import module.claim.model.ClaimRelatedPolicy;
import module.claim.model.ClaimStatusEnum;
import module.policy.dto.PolicyRelatedClaimDTO;
import module.policy.dto.PolicySearchCriteria;
import module.policy.dto.PolicySearchDTO;
import module.setup.model.Company;
import module.setup.model.CompanyDepartment;
import module.setup.model.InsuranceClass;
import module.setup.model.Insurer;
import module.setup.model.LossType;
import module.upload.model.UploadedFile;
import web.core.model.AbstractForm;

public class ClaimSetupForm extends AbstractForm {

	private static final long serialVersionUID = 1L;

	private String selectedTab;

	private Claim claim;

	private PolicySearchCriteria policySearchCriteria;

	private boolean policySearched;

	private List<PolicySearchDTO> policies;

	private List<Insurer> insurers;

	private List<InsuranceClass> insuranceClasses;

	private List<Company> companies;

	private List<CompanyDepartment> departments;

	private ClaimStatusEnum[] statuses;

	private List<LossType> lossTypes;

	// Upload Form
	private UploadedFile uploadedFile;

	private List<UploadedFile> filesList;

	private List<MultipartFile> files;

	// Claim Related Policy
	private String relatedPolicyRef2Flag;
	private String relatedPolicyRef3Flag;
	private String relatedPolicyRef4Flag;

	private List<ClaimRelatedPolicy> relatedPolicy;

	private List<PolicyRelatedClaimDTO> otherPolicy;

	private String companyCode;

	private boolean sibUser;

	// Field to be rendered in input text instead of drop down when login user
	// is SIB user
	private String ddRelatedPolicyRef1;
	private String ddRelatedPolicyRef2;
	private String ddRelatedPolicyRef3;
	private String ddRelatedPolicyRef4;
	private String ddDepartment;
	private String ddLossType;

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public PolicySearchCriteria getPolicySearchCriteria() {
		return policySearchCriteria;
	}

	public void setPolicySearchCriteria(PolicySearchCriteria policySearchCriteria) {
		this.policySearchCriteria = policySearchCriteria;
	}

	public boolean isPolicySearched() {
		return policySearched;
	}

	public void setPolicySearched(boolean policySearched) {
		this.policySearched = policySearched;
	}

	public List<PolicySearchDTO> getPolicies() {
		return policies;
	}

	public void setPolicies(List<PolicySearchDTO> policies) {
		this.policies = policies;
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

	public List<CompanyDepartment> getDepartments() {
		return departments;
	}

	public void setDepartments(List<CompanyDepartment> departments) {
		this.departments = departments;
	}

	public ClaimStatusEnum[] getStatuses() {
		return statuses;
	}

	public void setStatuses(ClaimStatusEnum[] statuses) {
		this.statuses = statuses;
	}

	public List<LossType> getLossTypes() {
		return lossTypes;
	}

	public void setLossTypes(List<LossType> lossTypes) {
		this.lossTypes = lossTypes;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public List<UploadedFile> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<UploadedFile> filesList) {
		this.filesList = filesList;
	}

	public List<MultipartFile> getFiles() {
		return files;
	}

	public void setFiles(List<MultipartFile> files) {
		this.files = files;
	}

	public String getRelatedPolicyRef2Flag() {
		return relatedPolicyRef2Flag;
	}

	public void setRelatedPolicyRef2Flag(String relatedPolicyRef2Flag) {
		this.relatedPolicyRef2Flag = relatedPolicyRef2Flag;
	}

	public String getRelatedPolicyRef3Flag() {
		return relatedPolicyRef3Flag;
	}

	public void setRelatedPolicyRef3Flag(String relatedPolicyRef3Flag) {
		this.relatedPolicyRef3Flag = relatedPolicyRef3Flag;
	}

	public String getRelatedPolicyRef4Flag() {
		return relatedPolicyRef4Flag;
	}

	public void setRelatedPolicyRef4Flag(String relatedPolicyRef4Flag) {
		this.relatedPolicyRef4Flag = relatedPolicyRef4Flag;
	}

	public List<ClaimRelatedPolicy> getRelatedPolicy() {
		return relatedPolicy;
	}

	public void setRelatedPolicy(List<ClaimRelatedPolicy> relatedPolicy) {
		this.relatedPolicy = relatedPolicy;
	}

	public List<PolicyRelatedClaimDTO> getOtherPolicy() {
		return otherPolicy;
	}

	public void setOtherPolicy(List<PolicyRelatedClaimDTO> otherPolicy) {
		this.otherPolicy = otherPolicy;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public boolean isSibUser() {
		return sibUser;
	}

	public void setSibUser(boolean sibUser) {
		this.sibUser = sibUser;
	}

	public String getDdRelatedPolicyRef1() {
		return ddRelatedPolicyRef1;
	}

	public void setDdRelatedPolicyRef1(String ddRelatedPolicyRef1) {
		this.ddRelatedPolicyRef1 = ddRelatedPolicyRef1;
	}

	public String getDdRelatedPolicyRef2() {
		return ddRelatedPolicyRef2;
	}

	public void setDdRelatedPolicyRef2(String ddRelatedPolicyRef2) {
		this.ddRelatedPolicyRef2 = ddRelatedPolicyRef2;
	}

	public String getDdRelatedPolicyRef3() {
		return ddRelatedPolicyRef3;
	}

	public void setDdRelatedPolicyRef3(String ddRelatedPolicyRef3) {
		this.ddRelatedPolicyRef3 = ddRelatedPolicyRef3;
	}

	public String getDdRelatedPolicyRef4() {
		return ddRelatedPolicyRef4;
	}

	public void setDdRelatedPolicyRef4(String ddRelatedPolicyRef4) {
		this.ddRelatedPolicyRef4 = ddRelatedPolicyRef4;
	}

	public String getDdDepartment() {
		return ddDepartment;
	}

	public void setDdDepartment(String ddDepartment) {
		this.ddDepartment = ddDepartment;
	}

	public String getDdLossType() {
		return ddLossType;
	}

	public void setDdLossType(String ddLossType) {
		this.ddLossType = ddLossType;
	}
}
