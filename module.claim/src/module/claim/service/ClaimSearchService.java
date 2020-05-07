package module.claim.service;

import java.util.List;

import app.core.security.UserPrincipal;
import module.claim.dto.ClaimSearchCriteria;
import module.claim.dto.ClaimSearchDTO;
import module.setup.model.UserCompany;

public interface ClaimSearchService {
	public List<ClaimSearchDTO> searchClaim(ClaimSearchCriteria criteria);

	public List<UserCompany> getUserCompany(UserPrincipal principal);
}
