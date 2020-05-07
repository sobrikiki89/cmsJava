package web.module.policy.model;

import java.util.List;

import module.policy.dto.PolicySearchCriteria;
import module.policy.dto.PolicySearchDTO;
import module.setup.model.Company;
import module.setup.model.InsuranceClass;
import module.setup.model.Insurer;
import web.core.model.AbstractForm;

public class PolicySearchForm extends AbstractForm {
	private static final long serialVersionUID = 1L;

	private boolean searched;

	private PolicySearchCriteria criteria;

	private List<Insurer> insurers;

	private List<InsuranceClass> insuranceClasses;

	private List<Company> companies;

	private List<PolicySearchDTO> policies;

	private boolean sibUser;

	public boolean isSearched() {
		return searched;
	}

	public void setSearched(boolean searched) {
		this.searched = searched;
	}

	public PolicySearchCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(PolicySearchCriteria criteria) {
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

	public List<Company> getCompanies() {
		return companies;
	}

	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}

	public List<PolicySearchDTO> getPolicies() {
		return policies;
	}

	public void setPolicies(List<PolicySearchDTO> policies) {
		this.policies = policies;
	}

	public boolean isSibUser() {
		return sibUser;
	}

	public void setSibUser(boolean sibUser) {
		this.sibUser = sibUser;
	}
}
