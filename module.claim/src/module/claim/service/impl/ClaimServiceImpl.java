package module.claim.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.core.security.UserPrincipal;
import app.core.service.SequenceService;
import app.core.service.impl.AbstractServiceImpl;
import module.claim.helper.ClaimConstant;
import module.claim.model.Claim;
import module.claim.model.ClaimFile;
import module.claim.model.ClaimRelatedPolicy;
import module.claim.model.ClaimRemark;
import module.claim.service.ClaimService;
import module.policy.dto.PolicyRelatedClaimDTO;
import module.policy.model.Policy;
import module.setup.model.CompanyDepartment;
import module.upload.model.UploadedFile;
import module.upload.service.UploadedFileService;

@Service
public class ClaimServiceImpl extends AbstractServiceImpl implements ClaimService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClaimServiceImpl.class);

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,##0.00");

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private SequenceService seqService;

	@Autowired
	private UploadedFileService uploadService;

	@Value("${max.total.attachment.bytesize}")
	private Long maxAttachmentsFilesize;

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public Claim getClaim(Long claimId) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT o FROM " + Claim.class.getName() + " o WHERE o.id = :claimId")
				.setParameter("claimId", claimId);
		Claim claim = (Claim) q.uniqueResult();
		sortOrder(claim);
		relatedPolicyToTheClaim(claim);
		return claim;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = true)
	public List<Claim> getClaimByClaimNo(String claimNo) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT o FROM " + Claim.class.getName() + " o WHERE UPPER(o.claimNo) = :claimNo")
				.setParameter("claimNo", StringUtils.upperCase(claimNo));
		List<Claim> claimList = (List<Claim>) q.list();
		for (Claim claim : claimList) {
			sortOrder(claim);
		}
		return claimList;
	}

	@Transactional
	public Long createClaim(Claim claim) {
		Session session = sessionFactory.getCurrentSession();
		claim.setDeleted(false);
		if (claim.getAdjusterContact() != null && StringUtils.isEmpty(claim.getAdjusterContact().getContactPerson())
				&& StringUtils.isEmpty(claim.getAdjusterContact().getTelNo())) {
			claim.setAdjusterContact(null);
		} else {
			claim.getAdjusterContact().setState(null);
			session.saveOrUpdate(claim.getAdjusterContact());
		}

		if (claim.getSolicitorContact() != null && StringUtils.isEmpty(claim.getSolicitorContact().getContactPerson())
				&& StringUtils.isEmpty(claim.getSolicitorContact().getTelNo())) {
			claim.setSolicitorContact(null);
		} else {
			claim.getSolicitorContact().setState(null);
			session.saveOrUpdate(claim.getSolicitorContact());
		}

		if (claim.getInsuredContact() != null && StringUtils.isEmpty(claim.getInsuredContact().getContactPerson())
				&& StringUtils.isEmpty(claim.getInsuredContact().getTelNo())) {
			claim.setInsuredContact(null);
		} else {
			claim.getInsuredContact().setState(null);
			session.saveOrUpdate(claim.getInsuredContact());
		}

		if (claim.getRelatedPolicy() == null) {
			claim.setRelatedPolicy(new ArrayList<ClaimRelatedPolicy>());
		} else {
			List<ClaimRelatedPolicy> claimRelatedPolicies = new ArrayList<ClaimRelatedPolicy>();
			for (ClaimRelatedPolicy relatedPolicy : claim.getRelatedPolicy()) {
				if (StringUtils.isBlank(relatedPolicy.getPolicyNo())) {
					claimRelatedPolicies.add(relatedPolicy);
				}
				if (StringUtils.isNotBlank(relatedPolicy.getPolicyNo())) {
					relatedPolicy.setClaim(claim);
				}
			}
			claim.getRelatedPolicy().removeAll(claimRelatedPolicies);
		}

		if (claim.getRemarks() == null) {
			claim.setRemarks(new ArrayList<ClaimRemark>());
		} else {
			for (ClaimRemark remark : claim.getRemarks()) {
				remark.setClaim(claim);
			}
		}

		if (claim.getDepartment() != null && claim.getDepartment().getId() != null) {
			claim.setDepartment(
					(CompanyDepartment) session.get(CompanyDepartment.class, claim.getDepartment().getId()));
		} else {
			claim.setDepartment(null);
		}

		String generatedClaimNo = new String();
		if (StringUtils.isNotBlank(claim.getPolicy().getCompany().getCode())) {
			generatedClaimNo = this.getNextClaimNo(claim.getPolicy().getCompany().getCode(), claim.getLossDate(),
					claim.getPolicy().getInsuranceClass().getCode());
			claim.setCompanyCode(claim.getPolicy().getCompany().getCode());
		} else {
			return null;
		}
		claim.setClaimNo(generatedClaimNo);
		return (Long) session.save(claim);
	}

	@Transactional
	public Claim updateClaim(Claim claim) {
		Session session = sessionFactory.getCurrentSession();

		if (claim.getAdjusterContact() != null && StringUtils.isEmpty(claim.getAdjusterContact().getContactPerson())
				&& StringUtils.isEmpty(claim.getAdjusterContact().getTelNo())) {
			claim.setAdjusterContact(null);
		} else {
			claim.getAdjusterContact().setState(null);
			session.saveOrUpdate(claim.getAdjusterContact());
		}

		if (claim.getSolicitorContact() != null && StringUtils.isEmpty(claim.getSolicitorContact().getContactPerson())
				&& StringUtils.isEmpty(claim.getSolicitorContact().getTelNo())) {
			claim.setSolicitorContact(null);
		} else {
			claim.getSolicitorContact().setState(null);
			session.saveOrUpdate(claim.getSolicitorContact());
		}

		if (claim.getInsuredContact() != null && StringUtils.isEmpty(claim.getInsuredContact().getContactPerson())
				&& StringUtils.isEmpty(claim.getInsuredContact().getTelNo())) {
			claim.setInsuredContact(null);
		} else {
			claim.getInsuredContact().setState(null);
			session.saveOrUpdate(claim.getInsuredContact());
		}

		if (claim.getRelatedPolicy() == null) {
			claim.setRelatedPolicy(new ArrayList<ClaimRelatedPolicy>());
		} else {
			List<ClaimRelatedPolicy> claimRelatedPolicies = new ArrayList<ClaimRelatedPolicy>();
			for (ClaimRelatedPolicy relatedPolicy : claim.getRelatedPolicy()) {
				if (StringUtils.isBlank(relatedPolicy.getPolicyNo())) {
					claimRelatedPolicies.add(relatedPolicy);
				}
				if (StringUtils.isNotBlank(relatedPolicy.getPolicyNo())) {
					relatedPolicy.setClaim(claim);
				}
			}
			claim.getRelatedPolicy().removeAll(claimRelatedPolicies);
		}

		if (claim.getRemarks() == null) {
			claim.setRemarks(new ArrayList<ClaimRemark>());
		} else {
			for (ClaimRemark remark : claim.getRemarks()) {
				remark.setClaim(claim);
			}
		}

		if (claim.getDepartment() != null && claim.getDepartment().getId() != null) {
			claim.setDepartment(
					(CompanyDepartment) session.get(CompanyDepartment.class, claim.getDepartment().getId()));
		} else {
			claim.setDepartment(null);
		}

		return (Claim) session.merge(claim);
	}

	protected void sortOrder(Claim claim) {
		if (claim != null) {
			if (claim.getRemarks() != null) {
				Collections.sort(claim.getRemarks(), new Comparator<ClaimRemark>() {
					@Override
					public int compare(ClaimRemark o1, ClaimRemark o2) {
						return o1.getOrder() > o2.getOrder() ? 1 : o1.getOrder() < o2.getOrder() ? -1 : 0;
					}
				});
			}
		}
	}

	protected void relatedPolicyToTheClaim(Claim claim) {
		if (claim != null) {
			if (claim.getRelatedPolicy() != null) {
				Collections.sort(claim.getRelatedPolicy(), new Comparator<ClaimRelatedPolicy>() {
					@Override
					public int compare(ClaimRelatedPolicy o1, ClaimRelatedPolicy o2) {
						return o1.getId() > o2.getId() ? 1 : o1.getId() < o2.getId() ? -1 : 0;
					}
				});
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional
	public List<ClaimFile> getClaimFile(Claim claim) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session
				.createQuery("SELECT o FROM " + ClaimFile.class.getName() + " o " + " WHERE o.claim.id = :claimId ")
				.setParameter("claimId", claim.getId());
		List<ClaimFile> claimFile = (List<ClaimFile>) q.list();
		if (claimFile.size() > 0) {
			return claimFile;
		} else {
			return null;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional
	public List<UploadedFile> getUploadedFile(Claim claim) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery(
				"SELECT o.uploadedFile FROM " + ClaimFile.class.getName() + " o " + " WHERE o.claim.id = :claimId")
				.setParameter("claimId", claim.getId());
		List<UploadedFile> fileList = (List<UploadedFile>) q.list();
		if (fileList.size() > 0) {
			return fileList;
		} else {
			return null;
		}
	}

	@Transactional
	public List<ClaimFile> attachFile(Claim claim, List<UploadedFile> files) {
		Session session = sessionFactory.getCurrentSession();
		List<ClaimFile> claimFile = new ArrayList<ClaimFile>();
		List<UploadedFile> attach = new ArrayList<UploadedFile>();
		for (UploadedFile delFile : files) {
			if (delFile.getId() != null) {
				deleteClaimFilebyFileId(delFile.getId());
				deleteUploadedFileById(delFile.getId());
			}
			attach.add(delFile);
		}

		List<UploadedFile> attachment = uploadService.saveAttachment(attach);
		if (attachment != null) {
			for (UploadedFile item : attachment) {
				ClaimFile file = new ClaimFile();
				file.setClaim(claim);
				file.setUploadedFile(item);
				session.persist(file);
				claimFile.add(file);
			}
		}

		return claimFile;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional
	public List<PolicyRelatedClaimDTO> getOtherPolicy(Policy policy, boolean checkUserCompany) {
		Session session = sessionFactory.getCurrentSession();
		Map<String, Object> param = new LinkedHashMap<String, Object>();

		StringBuilder sql = new StringBuilder(
				"SELECT DISTINCT o FROM " + Policy.class.getName() + " o " + " WHERE o.id != :policyId ");
		param.put("policyId", policy.getId());

		if (checkUserCompany) {
			UserPrincipal principal = UserPrincipal.class
					.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
			if (principal.getCompanyCode() != null) {
				sql.append(" AND o.company.code = :companyCode ");
				param.put("companyCode", principal.getCompanyCode());
			}
		}

		sql.append(" ORDER BY o.policyNo DESC");
		Query q = session.createQuery(sql.toString());
		for (Map.Entry<String, Object> entry : param.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List<Policy> otherPolicy = (List<Policy>) q.list();
		if (otherPolicy.size() > 0) {
			List<PolicyRelatedClaimDTO> otherPolicyList = new ArrayList<PolicyRelatedClaimDTO>();
			for (Policy item : otherPolicy) {
				PolicyRelatedClaimDTO pDto = new PolicyRelatedClaimDTO();
				pDto.setId(item.getId());
				pDto.setPolicyNo(item.getPolicyNo());
				pDto.setInsuranceClass(item.getInsuranceClass().getCode());
				pDto.setStartDate(item.getStartDate());
				pDto.setSumInsured(item.getSumInsured());
				pDto.setDropDownLabel(item.getPolicyNo() + " (" + item.getInsuranceClass().getCode() + ") - " + " RM "
						+ DECIMAL_FORMAT.format(item.getSumInsured() != null ? item.getSumInsured() : 0));
				otherPolicyList.add(pDto);
			}
			return otherPolicyList;
		} else {
			return null;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional
	public List<ClaimRemark> getClaimRemarks(Claim claim) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session
				.createQuery("SELECT o FROM " + ClaimRemark.class.getName() + " o " + " WHERE o.claim.id = :claimId ")
				.setParameter("claimId", claim.getId());
		List<ClaimRemark> remarks = (List<ClaimRemark>) q.list();
		if (remarks.size() > 0) {
			return remarks;
		} else {
			return null;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional
	public List<ClaimRelatedPolicy> getRelatedPolicy(Claim claim) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session
				.createQuery(
						"SELECT o FROM " + ClaimRelatedPolicy.class.getName() + " o " + " WHERE o.claim.id = :claimId ")
				.setParameter("claimId", claim.getId());
		List<ClaimRelatedPolicy> relatedPolicy = (List<ClaimRelatedPolicy>) q.list();
		if (relatedPolicy.size() > 0) {
			return relatedPolicy;
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	@Transactional
	public Claim getClaimbyClaimFile(Long objId) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT c FROM " + Claim.class.getName() + " c, " + ClaimFile.class.getName()
				+ " o WHERE o.uploadedFile.id = :fileId and o.claim.id = c.id").setParameter("fileId", objId);
		Claim claim = (Claim) q.uniqueResult();
		if (claim != null) {
			return claim;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Transactional
	public void deleteClaimFilebyFileId(Long objId) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session
				.createQuery("SELECT o FROM " + ClaimFile.class.getName() + " o WHERE o.uploadedFile.id = :fileId")
				.setParameter("fileId", objId);
		ClaimFile claimFile = (ClaimFile) q.uniqueResult();
		if (claimFile != null) {
			session.delete(claimFile);
		}
	}

	@Transactional
	public void deleteUploadedFileById(Long objId) {
		Session session = sessionFactory.getCurrentSession();
		UploadedFile myObject = (UploadedFile) session.load(UploadedFile.class, objId);
		session.delete(myObject);
	}

	// Delete service
	@Transactional
	public Boolean updateDeleteFlag(Long claimId, boolean deleted) {
		Session session = sessionFactory.getCurrentSession();
		Claim claim = (Claim) session.load(Claim.class, claimId);
		claim.setDeleted(deleted ? deleted : null);
		try {
			session.merge(claim);
			return true;
		} catch (Exception e) {
			LOGGER.error("Error in update deleted flag", e);
			return false;
		}
	}

	@Transactional
	public Boolean hideClaimById(Long claimId) {

		try {
			Session session = sessionFactory.getCurrentSession();
			Claim claim = (Claim) session.load(Claim.class, claimId);
			claim.setDeleteApproval(false);
		} catch (Exception e) {
			LOGGER.error("Error on delete claim ", e);
			return false;
		}

		return true;
	}

	@Transactional
	protected String getNextClaimNo(String companyCode, Date lossDate, String insClass) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(lossDate);
		int year = cal.get(Calendar.YEAR);

		String seq = seqService.getNextSequence(companyCode, ClaimConstant.SEQ_FORMAT_CLAIM_NO);
		String claimNo = companyCode + "/" + year + "/" + insClass + "/" + seq;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Generated Claim No [" + companyCode + "/" + year + "/" + insClass + "/" + seq + "]");
		}

		return claimNo.replaceAll("\\s", "");
	}

	public Long getMaxAttachmentsFilesize() {
		return maxAttachmentsFilesize;
	}
}
