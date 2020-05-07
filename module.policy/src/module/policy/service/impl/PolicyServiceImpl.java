package module.policy.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.dto.Entity2DTOMapper;
import app.core.exception.BaseApplicationException;
import app.core.service.impl.AbstractServiceImpl;
import module.policy.dto.PolicySetupDTO;
import module.policy.model.Policy;
import module.policy.model.PolicyEndorsement;
import module.policy.model.PolicyExcessDeductible;
import module.policy.model.PolicyFile;
import module.policy.model.PolicyInterestInsured;
import module.policy.service.PolicyService;
import module.upload.model.UploadedFile;
import module.upload.service.UploadedFileService;

@Service
@SuppressWarnings("unchecked")
public class PolicyServiceImpl extends AbstractServiceImpl implements PolicyService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PolicyServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private UploadedFileService uploadService;

	@Transactional(readOnly = true)
	public DataSet<PolicySetupDTO> getPoliciesForSetup(DatatablesCriterias criterias) throws BaseApplicationException {
		return getListByCriteria(criterias, Policy.class, new Entity2DTOMapper<Policy, PolicySetupDTO>() {
			@Override
			public PolicySetupDTO map(Policy entity) {
				PolicySetupDTO dto = new PolicySetupDTO();
				dto.setId(entity.getId());
				dto.setPolicyNo(entity.getPolicyNo());
				dto.setPremiumGross(entity.getPremiumGross());
				dto.setSumInsured(entity.getSumInsured());
				dto.setStartDate(entity.getStartDate());
				dto.setEndDate(entity.getEndDate());
				if (entity.getCompany() != null) {
					dto.setCompanyName(entity.getCompany().getName());
				}
				if (entity.getInsurer() != null) {
					dto.setInsurerName(entity.getInsurer().getName());
				}
				if (entity.getInsuranceClass() != null) {
					dto.setInsuranceClassCode(entity.getInsuranceClass().getCode());
				}
				return dto;
			}
		});
	}

	@Transactional(readOnly = true)
	public List<Policy> getPolicyByPolicyNo(String policyNo) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session
				.createQuery("SELECT o FROM " + Policy.class.getName() + " o WHERE UPPER(o.policyNo) = :policyNo")
				.setParameter("policyNo", StringUtils.upperCase(policyNo));
		List<Policy> policyList = (List<Policy>) q.list();
		for (Policy p : policyList) {
			sortOrder(p);
		}
		return policyList;
	}

	@Transactional(readOnly = true)
	public List<Policy> getAllPolicy() {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT o FROM " + Policy.class.getName() + " o ");
		List<Policy> policyList = (List<Policy>) q.list();
		for (Policy p : policyList) {
			sortOrder(p);
		}
		return policyList;
	}

	@Transactional(readOnly = true)
	public Policy getPolicy(Long policyId) {
		Session session = sessionFactory.getCurrentSession();
		Query<Policy> q = session.createQuery("SELECT o FROM " + Policy.class.getName() + " o WHERE o.id = :policyId")
				.setParameter("policyId", policyId);
		Policy p = (Policy) q.uniqueResult();
		if (p != null) {
			Hibernate.initialize(p.getInterestInsuredList());
			Hibernate.initialize(p.getExcessDeductibleList());
			Hibernate.initialize(p.getEndorsementList());
			sortOrder(p);
		}
		return p;
	}

	protected void sortOrder(Policy policy) {
		if (policy != null) {
			if (policy.getInterestInsuredList() != null) {
				Collections.sort(policy.getInterestInsuredList(), new Comparator<PolicyInterestInsured>() {
					@Override
					public int compare(PolicyInterestInsured o1, PolicyInterestInsured o2) {
						return o1.getOrder() > o2.getOrder() ? 1 : o1.getOrder() < o2.getOrder() ? -1 : 0;
					}
				});
			}

			if (policy.getExcessDeductibleList() != null) {
				Collections.sort(policy.getExcessDeductibleList(), new Comparator<PolicyExcessDeductible>() {
					@Override
					public int compare(PolicyExcessDeductible o1, PolicyExcessDeductible o2) {
						return o1.getOrder() > o2.getOrder() ? 1 : o1.getOrder() < o2.getOrder() ? -1 : 0;
					}
				});
			}
			
			if (policy.getEndorsementList() != null) {
				Collections.sort(policy.getEndorsementList(), new Comparator<PolicyEndorsement>() {
					@Override
					public int compare(PolicyEndorsement o1, PolicyEndorsement o2) {
						return o1.getOrder() > o2.getOrder() ? 1 : o1.getOrder() < o2.getOrder() ? -1 : 0;
					}
				});
			}
		}
	}

	@Transactional
	public Long createPolicy(Policy policy) {
		Session session = sessionFactory.getCurrentSession();
		BigDecimal sumInsured = BigDecimal.ZERO;
		if (policy.getInterestInsuredList() == null) {
			policy.setInterestInsuredList(new ArrayList<PolicyInterestInsured>());
		} else {
			for (PolicyInterestInsured item : policy.getInterestInsuredList()) {
				if (item.getSumCovered() != null) {
					sumInsured = sumInsured.add(item.getSumCovered());
				}
				item.setPolicy(policy);
			}
		}
		if (BigDecimal.ZERO.compareTo(sumInsured) == 0) {
			policy.setSumInsured(null);
		} else {
			policy.setSumInsured(sumInsured);
		}

		if (policy.getExcessDeductibleList() == null) {
			policy.setExcessDeductibleList(new ArrayList<PolicyExcessDeductible>());
		} else {
			for (PolicyExcessDeductible item : policy.getExcessDeductibleList()) {
				item.setPolicy(policy);
			}
		}
		if (policy.getCompany().getId() == null) {
			policy.setCompany(null);
		}
		return (Long) session.save(policy);
	}

	@Transactional
	public Policy updatePolicy(Policy policy) {
		Session session = sessionFactory.getCurrentSession();
		if (policy.getCompany().getId() == null) {
			policy.setCompany(null);
		}
		
		return (Policy) session.merge(policy);
	}

	// Attachment Service Start
	@Transactional
	public List<PolicyFile> attachFile(Policy policy, List<UploadedFile> files) {
		Session session = sessionFactory.getCurrentSession();
		List<PolicyFile> policyFile = new ArrayList<PolicyFile>();
		List<UploadedFile> attach = new ArrayList<UploadedFile>();
		for (UploadedFile delFile : files) {
			if (delFile.getId() != null) {
				deletePolicyFilebyFileId(delFile.getId());
				deleteUploadedFileById(delFile.getId());
			}
			attach.add(delFile);
		}

		List<UploadedFile> attachment = uploadService.saveAttachment(attach);
		if (attachment != null) {
			for (UploadedFile item : attachment) {
				PolicyFile file = new PolicyFile();
				file.setPolicy(policy);
				file.setFile(item);
				session.persist(file);
				policyFile.add(file);
			}
		}

		return policyFile;
	}

	@Transactional
	public void deleteUploadedFileById(Long objId) {
		LOGGER.debug("deleteUploadedFileById : ", +objId);
		Session session = sessionFactory.getCurrentSession();
		UploadedFile myObject = (UploadedFile) session.load(UploadedFile.class, objId);
		session.delete(myObject);
	}

	@Transactional
	public void deletePolicyFilebyFileId(Long objId) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT o FROM " + PolicyFile.class.getName() + " o WHERE o.file.id = :fileId")
				.setParameter("fileId", objId);
		PolicyFile policyFile = (PolicyFile) q.uniqueResult();
		if (policyFile != null) {
			session.delete(policyFile);
		}
	}

	@Transactional
	public List<PolicyFile> getPolicyFileByPolicyId(Policy policy) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session
				.createQuery("SELECT o FROM " + PolicyFile.class.getName() + " o WHERE o.policy.id = :policyId")
				.setParameter("policyId", policy.getId());
		List<PolicyFile> policyFiles = q.list();
		return policyFiles;
	}

	@Transactional
	public List<UploadedFile> getUploadedFile(Policy policy) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session
				.createQuery(
						"SELECT o.file FROM " + PolicyFile.class.getName() + " o " + " WHERE o.policy.id = :policyId")
				.setParameter("policyId", policy.getId());
		List<UploadedFile> fileList = (List<UploadedFile>) q.list();
		if (fileList.size() > 0) {
			return fileList;
		} else {
			return null;
		}
	}

	@Transactional
	public Policy getPolicyByFileId(Long objId) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT c FROM " + Policy.class.getName() + " c, " + PolicyFile.class.getName()
				+ " o WHERE o.file.id = :fileId and o.policy.id = c.id").setParameter("fileId", objId);
		Policy policy = (Policy) q.uniqueResult();
		if (policy != null) {
			return policy;
		}
		return null;
	}

	@Transactional
	public List<PolicyInterestInsured> getInterestInsuredByPolicy(Policy policy) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery(
				"SELECT c FROM " + PolicyInterestInsured.class.getName() + " c " + " WHERE c.policy.id = :policyId ")
				.setParameter("policyId", policy.getId());
		List<PolicyInterestInsured> insureds = (List<PolicyInterestInsured>) q.list();
		if (insureds != null) {
			return insureds;
		}
		return null;
	}

	@Transactional
	public List<PolicyExcessDeductible> getExcessDeductible(Policy policy) {
		Session session = sessionFactory.getCurrentSession();
		Query<PolicyExcessDeductible> q = session.createQuery(
				"SELECT c FROM " + PolicyExcessDeductible.class.getName() + " c " + " WHERE c.policy.id = :policyId ")
				.setParameter("policyId", policy.getId());
		List<PolicyExcessDeductible> insureds = (List<PolicyExcessDeductible>) q.list();
		if (insureds != null) {
			return insureds;
		}
		return null;
	}

	@Transactional
	public List<PolicyEndorsement> getAllPolicyEndorsements(Policy policy) {
		Session session = sessionFactory.getCurrentSession();
		Query<PolicyEndorsement> q = session.createQuery(
				"SELECT o FROM " + PolicyEndorsement.class.getName() + " o " + " WHERE o.policy.id = :policyId ")
				.setParameter("policyId", policy.getId());
		List<PolicyEndorsement> endorsments = (List<PolicyEndorsement>) q.list();
		if (endorsments != null) {
			return endorsments;
		}
		return null;
	}

	
}
