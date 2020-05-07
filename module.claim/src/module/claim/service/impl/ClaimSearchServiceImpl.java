package module.claim.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.core.security.UserPrincipal;
import app.core.service.impl.AbstractServiceImpl;
import app.core.usermgmt.model.User;
import app.core.utils.AppConstant;
import module.claim.dto.ClaimSearchCriteria;
import module.claim.dto.ClaimSearchDTO;
import module.claim.model.Claim;
import module.claim.service.ClaimSearchService;
import module.setup.model.UserCompany;
import module.setup.service.SetupConstant;

@Service
public class ClaimSearchServiceImpl extends AbstractServiceImpl implements ClaimSearchService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClaimSearchServiceImpl.class);
	
	private static final SimpleDateFormat SDF = new SimpleDateFormat(AppConstant.DATE_FORMAT);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = true)
	public List<ClaimSearchDTO> searchClaim(ClaimSearchCriteria criteria) {

		Map<String, Object> param = new LinkedHashMap<String, Object>();
		StringBuilder whereClause = new StringBuilder();
		whereClause.append("1 = 1");
		if (criteria != null) {

			if (criteria.getCompanyId() != null) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" o.policy.company.id = :companyId");
				param.put("companyId", criteria.getCompanyId());
			}

			if (StringUtils.isNotEmpty(criteria.getContractor())) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" UPPER(o.contractor) LIKE :contractor");
				param.put("contractor", criteria.getContractor().toUpperCase());
			}

			if (StringUtils.isNotEmpty(criteria.getInsurerCode())) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" o.policy.insurer.code = :insurerCode");
				param.put("insurerCode", criteria.getInsurerCode());
			}

			if (StringUtils.isNotEmpty(criteria.getInsuranceClassCode())) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" o.policy.insuranceClass.code = :insuranceClass");
				param.put("insuranceClass", criteria.getInsuranceClassCode());
			}

			if (StringUtils.isNotEmpty(criteria.getPolicyNo())) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" UPPER(o.policy.policyNo) LIKE :policyNo");
				param.put("policyNo", criteria.getPolicyNo().toUpperCase());
			}

			if (StringUtils.isNotEmpty(criteria.getClaimNo())) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" UPPER(o.claimNo) LIKE :claimNo");
				param.put("claimNo", criteria.getClaimNo().toUpperCase());
			}

			if (StringUtils.isNotEmpty(criteria.getCmsRefNo())) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" UPPER(o.cmsRefNo) LIKE :cmsRefNo");
				param.put("cmsRefNo", criteria.getCmsRefNo().toUpperCase());
			}

			if (criteria.getClaimStatus() != null) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" o.status = :status");
				param.put("status", criteria.getClaimStatus());
			}

			if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" o.notifyDate BETWEEN :startDate AND :endDate");
				Calendar cStart = Calendar.getInstance();
				cStart.setTime(criteria.getStartDate());
				cStart.set(Calendar.HOUR_OF_DAY, 0);
				cStart.set(Calendar.MINUTE, 0);
				cStart.set(Calendar.SECOND, 0);
				cStart.set(Calendar.MILLISECOND, 0);
				Calendar cEnd = Calendar.getInstance();
				cEnd.setTime(criteria.getEndDate());
				cEnd.set(Calendar.HOUR_OF_DAY, 23);
				cEnd.set(Calendar.MINUTE, 59);
				cEnd.set(Calendar.SECOND, 59);
				cEnd.set(Calendar.MILLISECOND, 999);
				param.put("startDate", cStart.getTime());
				param.put("endDate", cEnd.getTime());
			}

			if (criteria.getStartDate() != null && criteria.getEndDate() == null) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" o.notifyDate >= :startDate");
				Calendar cStart = Calendar.getInstance();
				cStart.setTime(criteria.getStartDate());
				cStart.set(Calendar.HOUR_OF_DAY, 0);
				cStart.set(Calendar.MINUTE, 0);
				cStart.set(Calendar.SECOND, 0);
				cStart.set(Calendar.MILLISECOND, 0);
				param.put("startDate", cStart.getTime());
			}

			if (criteria.getStartDate() == null && criteria.getEndDate() != null) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" o.notifyDate <= :endDate");
				Calendar cEnd = Calendar.getInstance();
				cEnd.setTime(criteria.getEndDate());
				cEnd.set(Calendar.HOUR_OF_DAY, 23);
				cEnd.set(Calendar.MINUTE, 59);
				cEnd.set(Calendar.SECOND, 59);
				cEnd.set(Calendar.MILLISECOND, 999);
				param.put("endDate", cEnd.getTime());
			}

			if (criteria.getFromLossDate() != null && criteria.getToLossDate() != null) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" o.lossDate BETWEEN :fromLossDate AND :toLossDate");
				Calendar cStart = Calendar.getInstance();
				cStart.setTime(criteria.getFromLossDate());
				cStart.set(Calendar.HOUR_OF_DAY, 0);
				cStart.set(Calendar.MINUTE, 0);
				cStart.set(Calendar.SECOND, 0);
				cStart.set(Calendar.MILLISECOND, 0);
				Calendar cEnd = Calendar.getInstance();
				cEnd.setTime(criteria.getToLossDate());
				cEnd.set(Calendar.HOUR_OF_DAY, 23);
				cEnd.set(Calendar.MINUTE, 59);
				cEnd.set(Calendar.SECOND, 59);
				cEnd.set(Calendar.MILLISECOND, 999);
				param.put("fromLossDate", cStart.getTime());
				param.put("toLossDate", cEnd.getTime());
			}

			if (criteria.getFromLossDate() != null && criteria.getToLossDate() == null) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" o.lossDate >= :fromLossDate");
				Calendar cStart = Calendar.getInstance();
				cStart.setTime(criteria.getFromLossDate());
				cStart.set(Calendar.HOUR_OF_DAY, 0);
				cStart.set(Calendar.MINUTE, 0);
				cStart.set(Calendar.SECOND, 0);
				cStart.set(Calendar.MILLISECOND, 0);
				param.put("fromLossDate", cStart.getTime());
			}

			if (criteria.getFromLossDate() == null && criteria.getToLossDate() != null) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" o.lossDate <= :toLossDate");
				Calendar cEnd = Calendar.getInstance();
				cEnd.setTime(criteria.getToLossDate());
				cEnd.set(Calendar.HOUR_OF_DAY, 23);
				cEnd.set(Calendar.MINUTE, 59);
				cEnd.set(Calendar.SECOND, 59);
				cEnd.set(Calendar.MILLISECOND, 999);
				param.put("toLossDate", cEnd.getTime());
			}

			if (StringUtils.isNotEmpty(criteria.getInsurerRef())) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}

				whereClause.append(" UPPER(o.insurerRef) LIKE :insurerRef");
				param.put("insurerRef", criteria.getInsurerRef().toUpperCase());
			}

			if (StringUtils.isNotEmpty(criteria.getVehicleRegNo())) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" UPPER(o.vehicleRegNo) LIKE :vehicleRegNo");
				param.put("vehicleRegNo", criteria.getVehicleRegNo().toUpperCase());
			}
			
			if (criteria.getSolicitorId() != 0) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" o.solicitor.id = :solicitor");
				param.put("solicitor", criteria.getSolicitorId());
			}
			
			if (criteria.getAdjusterId() != 0) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" o.adjuster.id = :adjuster");
				param.put("adjuster", criteria.getAdjusterId());
			}
		}

		if (criteria != null && Boolean.TRUE.equals(criteria.getDeletedOnly())) {
			if (whereClause.length() > 0) {
				whereClause.append(" AND ");
			}
			whereClause.append(" o.deleted = :deleted");
			param.put("deleted", Boolean.TRUE);
		} else {
			if (whereClause.length() > 0) {
				whereClause.append(" AND ");
			}
			whereClause.append(" (o.deleted IS NULL OR o.deleted != :deleted)");
			param.put("deleted", Boolean.TRUE);
		}

		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

		if (principal != null) {
			List<String> companyCodes = getUserCompanies(principal);
			boolean isSIB = false;
			for (String companyCode : companyCodes) {
				if (SetupConstant.COMPANY_CODE_SIB.equals(companyCode)) {
					isSIB = true;
					break;
				}
			}
			if (!isSIB) {
				if (companyCodes != null && !companyCodes.isEmpty()) {
					if (whereClause.length() > 0) {
						whereClause.append(" AND ");
					}
					whereClause.append(" o.policy.company.code IN :companyCode");
					param.put("companyCode", companyCodes);
				}
				else {
					if (whereClause.length() > 0) {
						whereClause.append(" AND ");
					}
					whereClause.append(" o.policy.company.code = '_NULL'");					
				}
			}
		}

		StringBuilder sql = new StringBuilder("SELECT o FROM " + Claim.class.getName() + " o ");
		sql.append(" WHERE ").append(whereClause);

		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery(sql.toString());
		for (Map.Entry<String, Object> entry : param.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List<Claim> claimList = q.list();
		LOGGER.info("Total claim found : " + claimList.size());

		if (!claimList.isEmpty()) {
			return mapResultToDTO(claimList);
		}
		return null;
	}

	private List<ClaimSearchDTO> mapResultToDTO(List<Claim> claimList) {
		List<ClaimSearchDTO> result = new ArrayList<ClaimSearchDTO>();
			for (Claim c : claimList) {
				ClaimSearchDTO dto = new ClaimSearchDTO();
				dto.setContractor(c.getContractor());
				dto.setClaimId(c.getId());
				dto.setClaimNo(c.getClaimNo());
				if (c.getPolicy() != null) {
					dto.setInsuranceClassCode(c.getPolicy().getInsuranceClass().getCode());
					dto.setPolicyId(c.getPolicy().getId());
					dto.setPolicyNo(c.getPolicy().getPolicyNo());
				}
				dto.setStatus(c.getStatus().getLabel());
				dto.setStatusCode(c.getStatus().name());
				dto.setNotificationDate(c.getNotifyDate());
				dto.setLossDate(c.getLossDate());
				dto.setLossType(c.getLossType().getName());
				dto.setLossTypeCode(c.getLossType().getCode());
				dto.setCmsRefNo(c.getCmsRefNo());
				dto.setDeleteApproval(c.isDeleteApproval());
				dto.setSolicitorFirmName(c.getSolicitor() != null ? c.getSolicitor().getFirmName() : "");
				dto.setAdjusterFirmName(c.getAdjuster() != null ? c.getAdjuster().getFirmName() : "");
				String createBy = this.getUsernameByUserId(c.getCreateUserId());
				dto.setCreateBy(createBy);
				dto.setCreateDate(c.getCreateDate());
				dto.setTitle("Created By : " + createBy + "&#13;&#10;" + "Created Date : " + SDF.format(c.getCreateDate()) );
				result.add(dto);
			}
		return result;
	}

	@Transactional
	private String getUsernameByUserId(Long createUserId) {
		Session session = sessionFactory.getCurrentSession();
		User obj = session.get(User.class, createUserId); // User user = commonDAO.findUserById(createUserId);
		return Optional.ofNullable(obj).map(o -> o.getUsername()).orElse("User deleted");
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(readOnly = true)
	public List<UserCompany> getUserCompany(UserPrincipal principal) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT o FROM " + UserCompany.class.getName() + " o WHERE o.userId.id = :userId")
				.setParameter("userId", principal.getUserId());
		List<UserCompany> p = (List<UserCompany>) q.getResultList();
		if (p != null) {
			return p;
		} else {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(readOnly = true)
	public List<String> getUserCompanies(UserPrincipal principal) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session
				.createQuery("SELECT c.code FROM " + UserCompany.class.getName() + " o "
						+ " JOIN o.companyId c WHERE o.userId.id = :userId")
				.setParameter("userId", principal.getUserId());
		List<String> p = (List<String>) q.getResultList();
		if (p != null) {
			return p;
		} else {
			return null;
		}
	}
}
