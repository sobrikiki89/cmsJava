package module.policy.service;

import java.util.List;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.exception.BaseApplicationException;
import module.policy.dto.PolicySetupDTO;
import module.policy.model.Policy;
import module.policy.model.PolicyEndorsement;
import module.policy.model.PolicyExcessDeductible;
import module.policy.model.PolicyFile;
import module.policy.model.PolicyInterestInsured;
import module.upload.model.UploadedFile;

public interface PolicyService {
	public DataSet<PolicySetupDTO> getPoliciesForSetup(DatatablesCriterias criterias) throws BaseApplicationException;

	public List<Policy> getPolicyByPolicyNo(String policyNo);

	public Policy getPolicy(Long policyId);

	public List<Policy> getAllPolicy();

	public Long createPolicy(Policy policy);

	public Policy updatePolicy(Policy policy);

	public List<PolicyFile> getPolicyFileByPolicyId(Policy policy);

	public List<UploadedFile> getUploadedFile(Policy policy);

	public Policy getPolicyByFileId(Long objId);

	public List<PolicyFile> attachFile(Policy policy, List<UploadedFile> attached);

	public void deleteUploadedFileById(Long objId);

	public void deletePolicyFilebyFileId(Long objId);

	public List<PolicyInterestInsured> getInterestInsuredByPolicy(Policy policy);

	public List<PolicyExcessDeductible> getExcessDeductible(Policy policy);

	public List<PolicyEndorsement> getAllPolicyEndorsements(Policy policy);
}
