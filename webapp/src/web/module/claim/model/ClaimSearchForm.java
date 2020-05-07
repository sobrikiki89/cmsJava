
package web.module.claim.model;

import java.util.List;

import module.claim.dto.ClaimSearchCriteria;
import module.claim.dto.ClaimSearchDTO;
import module.claim.model.ClaimStatusEnum;
import module.setup.dto.AdjusterDTO;
import module.setup.dto.SolicitorDTO;
import module.setup.model.Company;
import module.setup.model.InsuranceClass;
import module.setup.model.Insurer;
import web.core.model.AbstractForm;

public class ClaimSearchForm extends AbstractForm {
	private static final long serialVersionUID = 1L;

	private boolean searched;

	private ClaimSearchCriteria criteria;

	private List<Insurer> insurers;

	private List<InsuranceClass> insuranceClasses;

	private ClaimStatusEnum[] statuses;

	private List<ClaimSearchDTO> claims;

	private List<Company> companies;
	
	private List<SolicitorDTO> solicitors;
	
	private List<AdjusterDTO> adjusters;

	// Claim Deletion
	private Boolean deletaApproval;

	private boolean sibUser;

	private boolean hasDeletePermission;
	
	private boolean hasRevertPermission;

	public boolean isSearched() {
		return searched;
	}

	public void setSearched(boolean searched) {
		this.searched = searched;
	}

	public ClaimSearchCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ClaimSearchCriteria criteria) {
		this.criteria = criteria;
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

	public ClaimStatusEnum[] getStatuses() {
		return statuses;
	}

	public void setStatuses(ClaimStatusEnum[] statuses) {
		this.statuses = statuses;
	}

	public List<ClaimSearchDTO> getClaims() {
		return claims;
	}

	public void setClaims(List<ClaimSearchDTO> claims) {
		this.claims = claims;
	}

	public List<Company> getCompanies() {
		return companies;
	}

	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}

	public Boolean getDeletaApproval() {
		return deletaApproval;
	}

	public void setDeletaApproval(Boolean deletaApproval) {
		this.deletaApproval = deletaApproval;
	}

	public boolean isSibUser() {
		return sibUser;
	}

	public void setSibUser(boolean sibUser) {
		this.sibUser = sibUser;
	}

	public boolean isHasDeletePermission() {
		return hasDeletePermission;
	}

	public void setHasDeletePermission(boolean hasDeletePermission) {
		this.hasDeletePermission = hasDeletePermission;
	}

	public boolean isHasRevertPermission() {
		return hasRevertPermission;
	}

	public void setHasRevertPermission(boolean hasRevertPermission) {
		this.hasRevertPermission = hasRevertPermission;
	}

	public List<SolicitorDTO> getSolicitors() {
		return solicitors;
	}

	public void setSolicitors(List<SolicitorDTO> solicitors) {
		this.solicitors = solicitors;
	}

	public List<AdjusterDTO> getAdjusters() {
		return adjusters;
	}

	public void setAdjusters(List<AdjusterDTO> adjusters) {
		this.adjusters = adjusters;
	}
}
