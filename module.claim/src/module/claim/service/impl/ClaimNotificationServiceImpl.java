package module.claim.service.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dandelion.core.util.StringUtils;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;

import app.core.service.impl.AbstractServiceImpl;
import module.claim.model.Claim;
import module.claim.model.ClaimNotificationEmail;
import module.claim.model.ClaimRemark;
import module.claim.service.ClaimNotificationService;
import module.claim.service.ClaimService;
import module.notification.model.EmailType;
import module.notification.object.EmailContentBO;
import module.notification.object.NotificationTransaction;
import module.notification.service.EmailContentService;

@Service
public class ClaimNotificationServiceImpl extends AbstractServiceImpl implements ClaimNotificationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClaimNotificationServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private ClaimService claimService;

	@Autowired
	private EmailContentService emailContentService;

	@Transactional(readOnly = true)
	public void prepareEmail(ClaimNotificationEmail notificationEmail) {
		if (notificationEmail != null && notificationEmail.getClaim() != null) {
			Claim claim = claimService.getClaim(notificationEmail.getClaim().getId());

			// Read out the template
			List<EmailContentBO> templates = emailContentService.getActiveContent(
					NotificationTransaction.ClaimAcknowledgment.getCode(),
					NotificationTransaction.ClaimAcknowledgment.getType(), EmailType.NOTIFICATION);

			if (!templates.isEmpty()) {

				EmailContentBO template = templates.get(0);

				// Prepare subject
				// Format: [PREFIX] Company Name (Policy No.) - Loss Description
				StringBuffer subject = new StringBuffer();
				if (claim.getPolicy().getCompany() != null) {
					subject.append(claim.getPolicy().getCompany().getName());
				}
				subject.append(" (").append(claim.getPolicy().getPolicyNo()).append(")");
				if (StringUtils.isNotBlank(claim.getLossDescription())) {
					subject.append(" - ").append(claim.getLossDescription());
				}
				notificationEmail.setSubject(template.getSubject().replace("${subject}", subject.toString()));

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("prepareEmail : Email Subject : " + notificationEmail.getSubject());
				}

				// Prepare content
				// Claim No, Company Name, Policy No, Loss Date, Estimated Loss
				// Amount, Loss Description & Insured Contact No.
				String content = template.getExistingContent();
				content = content.replace("${claimNo}", claim.getClaimNo());
				if (claim.getPolicy().getCompany() != null) {
					content = content.replace("${companyName}", claim.getPolicy().getCompany().getName());
				} else {
					content = content.replace("${companyName}", "N/A");
				}

				content = content.replace("${policyNo}", claim.getPolicy().getPolicyNo());

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				content = content.replace("${lossDate}", sdf.format(claim.getLossDate()));

				if (claim.getEstLostAmount() != null) {
					DecimalFormat df = new DecimalFormat("###,###,##0.00");
					content = content.replace("${estimatedLossAmount}", "RM " + df.format(claim.getEstLostAmount()));
				} else {
					content = content.replace("${estimatedLossAmount}", "N/A");
				}

				if (StringUtils.isNotBlank(claim.getLossDescription())) {
					content = content.replace("${lossDescription}", claim.getLossDescription());
				} else {
					content = content.replace("${lossDescription}", "N/A");
				}

				if (claim.getInsuredContact() != null && StringUtils.isNotBlank(claim.getInsuredContact().getTelNo())) {
					content = content.replace("${insuredContactNo}", claim.getInsuredContact().getTelNo());
				} else {
					content = content.replace("${insuredContactNo}", "N/A");
				}

				if (claim.getRemarks() != null && !claim.getRemarks().isEmpty()) {
					// Get the latest remark, order by updated date
					List<ClaimRemark> ordered = Ordering.natural().nullsFirst()
							.onResultOf(new Function<ClaimRemark, Date>() {
								@Override
								public Date apply(ClaimRemark obj) {
									return obj.getUpdateDate();
								}
							}).reverse().immutableSortedCopy(claim.getRemarks());

					content = content.replace("${remark}", ordered.get(0).getRemark());
				} else {
					content = content.replace("${remark}", "N/A");
				}
				notificationEmail.setContent(content);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("prepareEmail : Email Content : " + notificationEmail.getContent());
				}
			}
		}
	}

	@Transactional(readOnly = true)
	public void prepareEmailMap(Claim claim, Map<String, Object> map) {
		if (claim != null) {
			claim = claimService.getClaim(claim.getId());

			// Prepare subject
			StringBuffer subject = new StringBuffer();
			if (claim.getPolicy().getCompany() != null) {
				subject.append(claim.getPolicy().getCompany().getName());
			}
			subject.append(" (").append(claim.getPolicy().getPolicyNo()).append(")");
			if (StringUtils.isNotBlank(claim.getLossDescription())) {
				subject.append(" - ").append(claim.getLossDescription());
			}
			map.put("subject", subject.toString());

			// Prepare content
			map.put("claimNo", claim.getClaimNo());
			if (claim.getPolicy().getCompany() != null) {
				map.put("companyName", claim.getPolicy().getCompany().getName());
			} else {
				map.put("companyName", "N/A");
			}
			map.put("policyNo", claim.getPolicy().getPolicyNo());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			map.put("lossDate", sdf.format(claim.getLossDate()));
			if (claim.getEstLostAmount() != null) {
				DecimalFormat df = new DecimalFormat("###,###,##0.00");
				map.put("estimatedLossAmount", "RM " + df.format(claim.getEstLostAmount()));
			} else {
				map.put("estimatedLossAmount", "N/A");
			}

			if (StringUtils.isNotBlank(claim.getLossDescription())) {
				map.put("lossDescription", claim.getLossDescription());
			} else {
				map.put("lossDescription", "N/A");
			}

			if (claim.getInsuredContact() != null && StringUtils.isNotBlank(claim.getInsuredContact().getTelNo())) {
				map.put("insuredContactNo", claim.getInsuredContact().getTelNo());
			} else {
				map.put("insuredContactNo", "N/A");
			}

			if (claim.getRemarks() != null && !claim.getRemarks().isEmpty()) {
				// Get the latest remark, order by updated date
				List<ClaimRemark> ordered = Ordering.natural().nullsFirst()
						.onResultOf(new Function<ClaimRemark, Date>() {
							@Override
							public Date apply(ClaimRemark obj) {
								return obj.getUpdateDate();
							}
						}).reverse().immutableSortedCopy(claim.getRemarks());

				map.put("remark", ordered.get(0).getRemark());
			} else {
				map.put("remark", "N/A");
			}

			if (LOGGER.isDebugEnabled()) {
				Joiner.MapJoiner mapJoiner = Joiner.on(", ").withKeyValueSeparator("=");
				LOGGER.debug("prepareEmailMap : Map [" + mapJoiner.join(map) + "]");
			}
		}
	}

	@Transactional
	public Long createClaimNotificationEmail(ClaimNotificationEmail email) {
		Session session = sessionFactory.getCurrentSession();
		return (Long) session.save(email);
	}
}
