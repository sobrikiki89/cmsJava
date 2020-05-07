package module.policy.service;

import java.util.List;

import module.policy.dto.PolicySearchCriteria;
import module.policy.dto.PolicySearchDTO;

public interface PolicySearchService {
	public List<PolicySearchDTO> searchPolicy(PolicySearchCriteria criteria);
}
