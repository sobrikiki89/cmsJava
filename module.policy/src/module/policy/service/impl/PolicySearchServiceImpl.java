package module.policy.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.core.security.UserPrincipal;
import app.core.service.impl.AbstractServiceImpl;
import app.core.usermgmt.model.User;
import app.core.utils.AppConstant;
import module.policy.dto.PolicySearchCriteria;
import module.policy.dto.PolicySearchDTO;
import module.policy.model.Policy;
import module.policy.service.PolicySearchService;
import module.setup.model.UserCompany;
import module.setup.service.SetupConstant;

@Service
public class PolicySearchServiceImpl extends AbstractServiceImpl implements PolicySearchService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PolicySearchServiceImpl.class);	
	private static final SimpleDateFormat SDF = new SimpleDateFormat(AppConstant.DATE_FORMAT);
	
	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = true)
	public List<PolicySearchDTO> searchPolicy(PolicySearchCriteria criteria) {

		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

		List<String> companyCodes = getUserCompanies(principal);

		boolean isSIB = false;
		for (String companyCode : companyCodes) {
			if (SetupConstant.COMPANY_CODE_SIB.equals(companyCode)) {
				isSIB = true;
				break;
			}
		}

		Map<String, Object> param = new LinkedHashMap<String, Object>();
		StringBuilder whereClause = new StringBuilder();
		whereClause.append(" 1 = 1 ");
		if (criteria != null) {
			if (criteria.getCompanyId() != null) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" o.company.id = :companyId");
				param.put("companyId", criteria.getCompanyId());
			}

			if (StringUtils.isNotEmpty(criteria.getInsurerCode())) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" LOWER(o.insurer.code) = :insurerCode");
				param.put("insurerCode", criteria.getInsurerCode().toLowerCase());
			}

			if (StringUtils.isNotEmpty(criteria.getInsuranceClassCode())) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" LOWER(o.insuranceClass.code) = :insuranceClass");
				param.put("insuranceClass", criteria.getInsuranceClassCode().toLowerCase());
			}

			if (StringUtils.isNotEmpty(criteria.getPolicyNo())) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" UPPER(o.policyNo) LIKE (:policyNo)");
				param.put("policyNo", "%"+criteria.getPolicyNo().toUpperCase()+"%");
			}

			if (!isSIB) {
				if (companyCodes != null && !companyCodes.isEmpty()) {
					if (whereClause.length() > 0) {
						whereClause.append(" AND ");
					}
					whereClause.append(" o.company.code IN :companyCode ");
					param.put("companyCode", companyCodes);
				}
				else {
					if (whereClause.length() > 0) {
						whereClause.append(" AND ");
					}
					whereClause.append(" o.company.code = '_NULL' ");
				}
			}

			if (criteria.getEffectiveStartDate() != null && criteria.getEffectiveEndDate() != null) {
				if (whereClause.length() > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(" o.startDate BETWEEN :startDate AND :endDate");
				param.put("startDate", criteria.getEffectiveStartDate());
				param.put("endDate", criteria.getEffectiveEndDate());
			}
		} else {
			if (!isSIB) {
				if (companyCodes != null && !companyCodes.isEmpty()) {
					if (whereClause.length() > 0) {
						whereClause.append(" AND ");
					}
					whereClause.append(" o.company IS NULL OR o.company.code IN :companyCode");
					param.put("companyCode", companyCodes);
				}
				else {
					if (whereClause.length() > 0) {
						whereClause.append(" AND ");
					}
					whereClause.append(" o.company IS NULL OR o.company.code = '_NULL'");
				}
			}
		}

		StringBuilder sql = new StringBuilder("SELECT o FROM " + Policy.class.getName() + " o");
		sql.append(" WHERE ").append(whereClause);

		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery(sql.toString());
		for (Map.Entry<String, Object> entry : param.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}

		List<Policy> policyList = q.getResultList();
		LOGGER.info("Total policy found : " + policyList.size());

		List<PolicySearchDTO> result = new ArrayList<PolicySearchDTO>();
		for (Policy p : policyList) {
			PolicySearchDTO dto = new PolicySearchDTO();
			if (p.getCompany() != null) {
				dto.setCompanyName(p.getCompany().getName());
			}
			dto.setPolicyId(p.getId());
			dto.setPolicyNo(p.getPolicyNo());
			dto.setInsurerCode(p.getInsurer().getCode());
			dto.setInsurerName(p.getInsurer().getName());
			if(p.getInsuranceClass() != null)
				dto.setInsuranceClassCode(p.getInsuranceClass().getCode());
			dto.setStartDate(p.getStartDate());
			dto.setEndDate(p.getEndDate());
			dto.setPremiumGross(p.getPremiumGross());
			dto.setSumInsured(p.getSumInsured());
			String createBy = this.getUsernameByUserId(p.getCreateUserId());
			dto.setCreateBy(createBy);
			dto.setCreateDate(p.getCreateDate());
			dto.setTitle("Created By : " + createBy + "&#13;&#10;" + "Created Date : " + SDF.format(p.getCreateDate()) );
			result.add(dto);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private String getUsernameByUserId(Long createUserId) {
		Session session = sessionFactory.getCurrentSession();
		Query<String> q = session
				.createQuery("SELECT o.username FROM " + User.class.getName() + " o "
						+ " WHERE o.createUserId = :createUserId")
				.setParameter("createUserId", createUserId);
		List<String> p = (List<String>) q.getResultList();
		if (p.size() > 0) {
			return p.get(0);
		} else {
			return "User deleted";
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<String> getUserCompanies(UserPrincipal principal) {
		Session session = sessionFactory.getCurrentSession();
		Query<String> q = session
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
