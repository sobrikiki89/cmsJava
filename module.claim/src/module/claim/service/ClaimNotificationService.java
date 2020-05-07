package module.claim.service;

import java.util.Map;

import module.claim.model.Claim;
import module.claim.model.ClaimNotificationEmail;

public interface ClaimNotificationService {
	public void prepareEmail(ClaimNotificationEmail notificationEmail);
	
	public void prepareEmailMap(Claim claim, Map<String, Object> map);
	
	public Long createClaimNotificationEmail(ClaimNotificationEmail email);
}
