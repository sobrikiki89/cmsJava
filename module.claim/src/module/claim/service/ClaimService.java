package module.claim.service;

import java.util.List;

import module.claim.model.Claim;
import module.claim.model.ClaimFile;
import module.claim.model.ClaimRelatedPolicy;
import module.claim.model.ClaimRemark;
import module.policy.dto.PolicyRelatedClaimDTO;
import module.policy.model.Policy;
import module.upload.model.UploadedFile;

public interface ClaimService {
	public Claim getClaim(Long claimId);

	public Long createClaim(Claim claim);

	public Claim updateClaim(Claim claim);

	public List<Claim> getClaimByClaimNo(String claimNo);

	public List<UploadedFile> getUploadedFile(Claim claim);

	public List<ClaimFile> attachFile(Claim claim, List<UploadedFile> files);

	public List<PolicyRelatedClaimDTO> getOtherPolicy(Policy policy, boolean checkUserCompany);

	public List<ClaimRelatedPolicy> getRelatedPolicy(Claim claim);

	public void deleteUploadedFileById(Long objId);

	public void deleteClaimFilebyFileId(Long objId);

	public Claim getClaimbyClaimFile(Long objId);

	public List<ClaimRemark> getClaimRemarks(Claim claim);

	//public boolean deleteClaimById(Long claimId);

	public Boolean updateDeleteFlag(Long claimId, boolean deleteApproval);

	public Boolean hideClaimById(Long claimId);
	
	public Long getMaxAttachmentsFilesize();

}
