package module.setup.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.domain.setup.model.Contact;
import app.core.dto.Entity2DTOMapper;
import app.core.exception.BaseApplicationException;
import app.core.model.EntityBase;
import app.core.security.UserPrincipal;
import app.core.service.impl.AbstractServiceImpl;
import app.core.usermgmt.model.User;
import module.setup.dto.CompanyDTO;
import module.setup.dto.UserCompanyDTO;
import module.setup.dto.UserDTO;
import module.setup.model.Company;
import module.setup.model.CompanyDepartment;
import module.setup.model.UserCompany;
import module.setup.service.CompanyService;
import module.setup.service.SetupConstant;

@Service
@SuppressWarnings({"rawtypes","unchecked"})
public class CompanyServiceImpl extends AbstractServiceImpl implements CompanyService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional
	public Long createDefaultCompany(Company company) {
		try {
			Session session = sessionFactory.getCurrentSession();
			Query q = session.createQuery("SELECT o FROM " + Company.class.getName() + " o WHERE o.code = :code")
					.setParameter("code", company.getCode());
			Company comp = (Company) q.uniqueResult();
			if (comp != null) {
				LOGGER.info("skip to create, company found with code [" + comp.getCode() + "]");
				return comp.getId();
			} else {
				return (Long) session.save(company);
			}
		} catch (Exception e) {
			LOGGER.error("Error in createDefaultCompany", e);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public DataSet<CompanyDTO> getCompanies(DatatablesCriterias criteria) throws BaseApplicationException {
		try {
			Entity2DTOMapper mapper = new Entity2DTOMapper<Company, CompanyDTO>() {
				@Override
				public CompanyDTO map(Company entity) {
					CompanyDTO dto = new CompanyDTO();
					dto.setId(entity.getId());
					dto.setCompanyCode(entity.getCode());
					dto.setName(entity.getName());
					dto.setBizRegNo(entity.getBizRegNo());
					if (entity.getContact() != null) {
						Contact contact = entity.getContact();
						dto.setContactPerson(contact.getContactPerson());
						dto.setTelNo(contact.getTelNo());
						dto.setFaxNo(contact.getFaxNo());
						dto.setEmail(contact.getEmail());
					}
					dto.setActive(entity.getActive());
					return dto;
				};
			};
			
			StringBuilder queryBuilder = new StringBuilder("SELECT o FROM " + Company.class.getName() + " o");
	
			/**
			 * Step 1: filtering
			 */
			StringBuilder whereClause = new StringBuilder();
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				whereClause.append(" WHERE ");
				whereClause.append(" LOWER(o.code) LIKE :searchCrit");
				whereClause.append(" OR");
				whereClause.append(" LOWER(o.name) LIKE :searchCrit");
				whereClause.append(" OR");
				whereClause.append(" LOWER(o.bizRegNo) LIKE :searchCrit");
				whereClause.append(" OR");
				whereClause.append(" LOWER(o.contact.contactPerson) LIKE :searchCrit");
				whereClause.append(" OR");
				whereClause.append(" LOWER(o.contact.telNo) LIKE :searchCrit");
				whereClause.append(" OR");
				whereClause.append(" LOWER(o.contact.faxNo) LIKE :searchCrit");
				whereClause.append(" OR");
				whereClause.append(" LOWER(o.contact.email) LIKE :searchCrit");
			}
			queryBuilder.append(whereClause);
	
			/**
			 * Step 2: sorting
			 */
			StringBuilder sortClause = new StringBuilder();
			sortClause.append(" ORDER BY ");
			Iterator<ColumnDef> itr2 = criteria.getSortedColumnDefs().iterator();
			ColumnDef colDef;
			while (itr2.hasNext()) {
				colDef = itr2.next();
				if ("code".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.code " + colDef.getSortDirection());
				} else if ("name".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.name " + colDef.getSortDirection());
				} else if ("bizRegNo".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.bizRegNo " + colDef.getSortDirection());
				} else if ("contactPerson".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.contact.contactPerson " + colDef.getSortDirection());
				} else if ("telNo".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.contact.telNo " + colDef.getSortDirection());
				} else if ("faxNo".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.contact.faxNo " + colDef.getSortDirection());
				} else if ("email".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.contact.email " + colDef.getSortDirection());
				} 
				if (itr2.hasNext()) {
					sortClause.append(" , ");
				}
			}
			queryBuilder.append(sortClause);
	
			Session session = sessionFactory.getCurrentSession();
			session.setHibernateFlushMode(FlushMode.MANUAL);
			Query query = session.createQuery(queryBuilder.toString());
	
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				query.setParameter("searchCrit", "%"+criteria.getSearch().toLowerCase()+"%");
			}
			
			/**
			 * Step 3: paging
			 */
			query.setFirstResult(criteria.getStart());
			query.setMaxResults(criteria.getLength());
			List objects = query.list();
			List result = new ArrayList();
			for (Object object : objects) {
				result.add(mapper.map((EntityBase) object));
			}
	
			String countHQL = new String("SELECT COUNT(o) FROM " + Company.class.getName() + " o");
			query = session.createQuery(countHQL);
			Long count = (Long) query.uniqueResult();
			
			query = session.createQuery(queryBuilder.toString());
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				query.setParameter("searchCrit", "%"+criteria.getSearch().toLowerCase()+"%");
			}
			Long countFiltered = Long.parseLong(String.valueOf(query.list().size()));
	
			return new DataSet<CompanyDTO>(result, count, countFiltered);
		} catch (Exception e) {
			throw new BaseApplicationException("failed getDTOObjectList", e);
		}
	}

	@Transactional(readOnly = true)
	public Company getCompany(Long id) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT o FROM " + Company.class.getName() + " o WHERE o.id = :id")
				.setParameter("id", id);
		return (Company) q.uniqueResult();
	}

	@Transactional(readOnly = true)
	public List<Company> getCompanyByBizRegNo(String bizRegNo) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session
				.createQuery("SELECT o FROM " + Company.class.getName() + " o WHERE UPPER(o.bizRegNo) = :bizRegNo")
				.setParameter("bizRegNo", StringUtils.upperCase(bizRegNo));
		return (List<Company>) q.list();
	}

	@Transactional(readOnly = true)
	public List<Company> getCompanyByCode(String code) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT o FROM " + Company.class.getName() + " o WHERE UPPER(o.code) = :code")
				.setParameter("code", StringUtils.upperCase(code));
		return (List<Company>) q.list();
	}

	@Transactional(readOnly = true)
	public Company getCompByCode(String code) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT o FROM " + Company.class.getName() + " o WHERE o.code = :code")
				.setParameter("code", code);
		return (Company) q.uniqueResult();
	}

	@Transactional
	public Long createCompany(Company company, List<UserCompany> uc, List<CompanyDepartment> departments) {
		try {
			Session session = sessionFactory.getCurrentSession();
			Long compId = null;
			if (isContactFieldsBlank(company.getContact())) {
				company.setContact(null);
			} else {
				if (company.getContact().getState() != null
						&& StringUtils.isEmpty(company.getContact().getState().getCode())) {
					company.getContact().setState(null);
				}
				session.save(company.getContact());
			}
			String upperCaseCode = company.getCode().toUpperCase();
			company.setCode(upperCaseCode);
			compId = (Long) session.save(company);
			if (compId != null) {
				for (UserCompany userCompanyNew : uc) {
					session.persist(userCompanyNew);
				}

				if (departments != null) {
					for (CompanyDepartment department : departments) {
						department.setCompany(company);
						session.persist(department);
					}
				}

				return compId;
			} else {
				return null;
			}
		} catch (Exception e) {
			LOGGER.error("Error in create company: ", e);
			return null;
		}

	}

	@Transactional
	public Company updateCompany(Company company, List<UserCompany> uc, List<CompanyDepartment> departments) {
		Session session = sessionFactory.getCurrentSession();
		if (isContactFieldsBlank(company.getContact())) {
			if (company.getContact().getId() != null) {
				session.delete(company.getContact());
			}
			company.setContact(null);
		} else {
			if (company.getContact().getState() != null
					&& StringUtils.isEmpty(company.getContact().getState().getCode())) {
				company.getContact().setState(null);
			}
			session.saveOrUpdate(company.getContact());
		}

		Company entity = (Company) session.merge(company);

		List<UserCompany> oldUserCompanys = this.getUserCompany(company);
		for (UserCompany userCompanyNew : uc) {
			userCompanyNew.setCompanyId(entity);

			Boolean flagNewRecord = true;
			if (oldUserCompanys != null) {
				for (UserCompany oldObj : oldUserCompanys) {
					if (userCompanyNew.getUserId().getId() == oldObj.getUserId().getId()) {
						userCompanyNew.setId(oldObj.getId());
						session.merge(userCompanyNew);
						flagNewRecord = false;
						break;
					}
				}
			}
			if (flagNewRecord) {
				session.persist(userCompanyNew);
			}
		}
		if (oldUserCompanys != null) {
			for (UserCompany oldObj : oldUserCompanys) {
				Boolean flagDelRecord = true;
				for (UserCompany userCompanyNew : uc) {
					if (userCompanyNew.getUserId().getId() == oldObj.getUserId().getId()) {
						flagDelRecord = false;
						break;
					}
				}
				if (flagDelRecord) {
					session.delete(oldObj);
				}
			}
		}

		// Update current user company code, if it was changed
		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (uc != null && !uc.isEmpty()) {
			for (UserCompany ucObj : uc) {
				if (ucObj.getUserId().getId().equals(principal.getUserId())) {
					principal.setCompanyCode(ucObj.getCompanyId().getCode());
				}
			}
		}

		List<CompanyDepartment> oldDepartments = this.getCompanyDepartment(company);
		if (departments != null) {
			for (CompanyDepartment newDepartment : departments) {
				newDepartment.setCompany(entity);

				Boolean flagNewRecord = true;
				if (oldDepartments != null) {
					for (CompanyDepartment oldDepartment : oldDepartments) {
						if (newDepartment.getCode().equals(oldDepartment.getCode())) {
							session.merge(newDepartment);
							flagNewRecord = false;
							break;
						}
					}
				}
				if (flagNewRecord) {
					session.persist(newDepartment);
				}
			}
		}
		if (oldDepartments != null) {
			for (CompanyDepartment oldDepartment : oldDepartments) {
				Boolean flagDelRecord = true;
				for (CompanyDepartment newDepartment : departments) {
					if (newDepartment.getCode().equals(oldDepartment.getCode())) {
						flagDelRecord = false;
						break;
					}
				}
				if (flagDelRecord) {
					session.delete(oldDepartment);
				}
			}
		}

		return entity;
	}

	@Transactional(readOnly = true)
	public List<Company> getAllCompanies() {
		Session session = sessionFactory.getCurrentSession();
		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

		List<String> userCompanies = getUserCompaniesCode(principal);
		Query q;
		if (userCompanies.contains(SetupConstant.COMPANY_CODE_SIB)) {
			q = session.createQuery("SELECT o FROM " + Company.class.getName() + " o ORDER BY o.name ASC");
		} else {
			q = session
					.createQuery(
							"SELECT o.companyId FROM " + UserCompany.class.getName() + " o WHERE o.userId.id = :userId")
					.setParameter("userId", principal.getUserId());
		}
		List<Company> company = (List<Company>) q.getResultList();
		if (company != null) {
			return company;
		} else {
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public List<Company> getAllCompaniesWithoutBroker() {
		Session session = sessionFactory.getCurrentSession();
		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

		List<String> userCompanies = getUserCompaniesCode(principal);
		Query q;
		if (userCompanies.contains(SetupConstant.COMPANY_CODE_SIB)) {
			q = session.createQuery("SELECT o FROM " + Company.class.getName() + " o WHERE o.code != 'SIB' ORDER BY o.name ASC");
		} else {
			q = session
					.createQuery(
							"SELECT o.companyId FROM " + UserCompany.class.getName() + " o WHERE o.userId.id = :userId")
					.setParameter("userId", principal.getUserId());
		}
		List<Company> company = (List<Company>) q.getResultList();
		if (company != null) {
			return company;
		} else {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<Company> getAllCompaniesForSIB() {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT o FROM " + Company.class.getName() + " o WHERE o.code != 'SIB' ORDER BY o.name ASC");
		List<Company> company = (List<Company>) q.getResultList();
		if (company != null) {
			return company;
		} else {
			return null;
		}
	}

	protected boolean isContactFieldsBlank(Contact contact) {
		if (StringUtils.isEmpty(contact.getAddress1()) && StringUtils.isEmpty(contact.getAddress2())
				&& StringUtils.isEmpty(contact.getAddress3()) && StringUtils.isEmpty(contact.getCity())
				&& StringUtils.isEmpty(contact.getContactPerson()) && StringUtils.isEmpty(contact.getPostcode())
				&& StringUtils.isEmpty(contact.getEmail()) && StringUtils.isEmpty(contact.getFaxNo())
				&& StringUtils.isEmpty(contact.getMobileNo())
				&& (contact.getState() == null || StringUtils.isEmpty(contact.getState().getCode()))) {
			return true;
		}
		return false;
	}

	@Transactional
	public List<String> getCompanyNameList(Long companyId) {
		Session session = sessionFactory.getCurrentSession();
		List<String> companyName = new ArrayList<String>();
		Query q = session.createQuery("SELECT o.name FROM " + Company.class.getName() + " o "
				+ " WHERE o.id = :companyId ORDER BY o.name ASC").setParameter("companyId", companyId);
		companyName = q.list();
		if (companyName.size() > 0) {
			return companyName;
		} else {
			companyName.add("All Companies");
			return companyName;
		}
	}

	@Transactional
	public void deleteObject(Long id) {
		Session session = sessionFactory.getCurrentSession();
		// Remove all departments first before delete company
		Company company = (Company) session.get(Company.class, id);
		for (CompanyDepartment department : getCompanyDepartment(company)) {
			session.delete(department);
		}
		session.delete(company);
	}

	@Transactional
	public List<UserDTO> getUserList() {
		Session session = sessionFactory.getCurrentSession();
		List<UserDTO> userDTO = new ArrayList<UserDTO>();
		Query q = session.createQuery("SELECT DISTINCT u FROM " + UserCompany.class.getName() + " o "
				+ " RIGHT JOIN o.userId u ");
				//+ "WHERE u.activeFlag != false ");
		List<User> userList = (List<User>) q.list();
		if (userList.size() > 0) {
			for (User user : userList) {
				UserDTO dto = new UserDTO();
				dto.setId(user.getId());
				dto.setUserId(user.getId());
				dto.setUsername(user.getUsername());
				dto.setFirstName(user.getFirstName());
				dto.setLastName(user.getLastName());
				userDTO.add(dto);
			}
		}
		return userDTO;
	}

	@Transactional(readOnly = true)
	public List<UserDTO> getUserListEdit(Company company) {
		Session session = sessionFactory.getCurrentSession();
		List<UserDTO> userDTO = new ArrayList<UserDTO>();
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT DISTINCT u FROM ").append(UserCompany.class.getName())
				.append(" o RIGHT JOIN o.userId u ");
				//.append(" WHERE o.companyId = :company ");
				//.append(" OR ( u NOT IN (SELECT uc.userId FROM ").append(UserCompany.class.getName()).append(" uc ");
		/*
		if (!SetupConstant.COMPANY_CODE_SIB.equals(company.getCode())) {
			hql.append("WHERE uc.companyId.code = '").append(SetupConstant.COMPANY_CODE_SIB).append("'");
		}
		*/
		//hql.append(") )");

		Query q = session.createQuery(hql.toString()); //.setParameter("company", company);
		List<User> userList = (List<User>) q.list();
		if (userList.size() > 0) {
			for (User user : userList) {
				UserDTO dto = new UserDTO();
				dto.setId(user.getId());
				dto.setUserId(user.getId());
				dto.setUsername(user.getUsername());
				dto.setFirstName(user.getFirstName());
				dto.setLastName(user.getLastName());
				userDTO.add(dto);
			}
		}
		return userDTO;
	}

	@Transactional(readOnly = true)
	public List<Long> getUserListById(Long companyId) {
		Session session = sessionFactory.getCurrentSession();
		List<Long> userId = new ArrayList<Long>();
		Query q = session
				.createQuery("SELECT u FROM " + UserCompany.class.getName() + " o "
						+ " RIGHT JOIN o.userId u WHERE o.companyId.id = :companyId ")
				.setParameter("companyId", companyId);
		List<User> uComp = (List<User>) q.list();
		if (uComp.size() > 0) {
			for (User uc : uComp) {
				userId.add(uc.getId());
			}
			return userId;
		} else {
			return null;
		}
	}

	@Transactional
	public User getUserById(Long userId) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT o FROM " + User.class.getName() + " o " + " WHERE o.id = :userId ")
				.setParameter("userId", userId);
		List<User> uComp = (List<User>) q.list();
		if (uComp.size() > 0) {
			return uComp.get(0);
		} else {
			return null;
		}
	}

	@Transactional(readOnly = true)
	private List<UserCompany> getUserCompany(Company company) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session
				.createQuery("SELECT o FROM " + UserCompany.class.getName() + " o " + " WHERE o.companyId = :company ")
				.setParameter("company", company);
		List<UserCompany> uComp = (List<UserCompany>) q.list();
		if (uComp.size() > 0) {
			return uComp;
		} else {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<CompanyDepartment> getCompanyDepartment(Company company) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session
				.createQuery(
						"SELECT o FROM " + CompanyDepartment.class.getName() + " o " + " WHERE o.company = :company ")
				.setParameter("company", company);
		List<CompanyDepartment> departments = (List<CompanyDepartment>) q.list();
		if (!departments.isEmpty()) {
			return departments;
		} else {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public DataSet<UserDTO> getUserGridNew(DatatablesCriterias criterias) throws BaseApplicationException {
		try {
			Entity2DTOMapper mapper = new Entity2DTOMapper<User, UserDTO>() {
				@Override
				public UserDTO map(User entity) {
					UserDTO obj = new UserDTO();
					obj.setId(entity.getId());
					obj.setUserId(entity.getId());
					obj.setUsername(entity.getUsername());
					obj.setFirstName(entity.getFirstName());
					obj.setLastName(entity.getLastName());
					return obj;
				}
			};
			
			StringBuilder queryBuilder = new StringBuilder("SELECT o FROM " + User.class.getName() + " o ");
					//+ " WHERE o NOT IN (SELECT uc.userId FROM " + UserCompany.class.getName()
					//+ " uc WHERE uc.companyId.code = '" + SetupConstant.COMPANY_CODE_SIB + "') ");
			/**
			 * Step 2: sorting
			 */
			StringBuilder sortClause = new StringBuilder();
			sortClause.append(" ORDER BY ");
			Iterator<ColumnDef> itr2 = criterias.getSortedColumnDefs().iterator();
			ColumnDef colDef;
			while (itr2.hasNext()) {
				colDef = itr2.next();
				if ("firstName".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.firstName " + colDef.getSortDirection());
				} else if ("lastName".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.lastName " + colDef.getSortDirection());
				} else if ("username".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.username " + colDef.getSortDirection());
				}
				if (itr2.hasNext()) {
					sortClause.append(" , ");
				}
			}
			queryBuilder.append(sortClause);

			Session session = sessionFactory.getCurrentSession();
			session.setHibernateFlushMode(FlushMode.MANUAL);
			Query query = session.createQuery(queryBuilder.toString());

			if (StringUtils.isNotBlank(criterias.getSearch())) {
				query.setParameter("searchCrit", "%" + criterias.getSearch().toLowerCase() + "%");
			}

			/**
			 * Step 3: paging
			 */
			query.setFirstResult(criterias.getStart());
			query.setMaxResults(criterias.getLength());

			List objects = query.list();
			List result = new ArrayList();
			for (Object object : objects) {
				result.add(mapper.map((EntityBase) object));
			}

			String countHQL = new String("SELECT COUNT(o) FROM " + User.class.getName() + " o");
			query = session.createQuery(countHQL);
			Long count = (Long) query.uniqueResult();

			query = session.createQuery(queryBuilder.toString());
			if (StringUtils.isNotBlank(criterias.getSearch())) {
				query.setParameter("searchCrit", "%" + criterias.getSearch().toLowerCase() + "%");
			}

			Long countFiltered = Long.parseLong(String.valueOf(query.list().size()));

			return new DataSet(result, count, countFiltered);

		} catch (Exception e) {
			throw new BaseApplicationException("failed getDTOObjectList", e);
		}
	}

	@Transactional(readOnly = true)
	public List<String> getAllEmailUnderSameCompany(String companyCode) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session // Domain contains illegal character -
							// o.userId.firstName || '<' || o.userId.email ||
							// '>'
				.createQuery("SELECT concat(trim(o.userId.firstName), '<', o.userId.email, '>') FROM "
						+ UserCompany.class.getName()
						+ " o WHERE o.userId.email IS NOT NULL AND o.companyId.code LIKE :companyCode AND o.userId.activeFlag = true")
				.setParameter("companyCode", companyCode);
		return (List<String>) q.list();
	}

	@Transactional(readOnly = true)
	public List<UserCompanyDTO> getUserCompanyList(Long id) {
		Session session = sessionFactory.getCurrentSession();
		List<UserCompanyDTO> dtoList = new ArrayList<UserCompanyDTO>();
		try {
			Query query = session.createQuery("SELECT u FROM " + UserCompany.class.getName() + " o "
					+ " LEFT JOIN o.userId u WHERE o.companyId.id = :companyId AND o.userId.activeFlag = true").setParameter("companyId", id);
			List<User> obj = (List<User>) query.list();
			if (obj.size() > 0) {
				for (User user : obj) {
					UserCompanyDTO dto = new UserCompanyDTO();
					if (user.getLastName().isEmpty()) {
						dto.setFirstName(user.getFirstName());
					} else {
						dto.setFirstName(user.getFirstName() + " " + user.getLastName());
					}
					dto.setUserName(user.getUsername());
					dtoList.add(dto);
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("User size: " + dtoList.size());
				}
			} else {
				UserCompanyDTO dto = new UserCompanyDTO();
				dto.setFirstName(" ");
				dto.setUserName("No User found");
				dtoList.add(dto);
			}
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
		return dtoList;
	}

	@Transactional(readOnly = true)
	public List<UserCompany> getUserCompany(UserPrincipal principal) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT o FROM " + UserCompany.class.getName() + " o WHERE o.userId.id = :userId AND o.userId.activeFlag = true")
				.setParameter("userId", principal.getUserId());
		List<UserCompany> p = (List<UserCompany>) q.getResultList();
		if (p != null) {
			return p;
		} else {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<String> getUserCompaniesCode(UserPrincipal principal) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session
				.createQuery("SELECT c.code FROM " + UserCompany.class.getName() + " o "
						+ " JOIN o.companyId c WHERE o.userId.id = :userId AND o.userId.activeFlag = true")
				.setParameter("userId", principal.getUserId());
		List<String> p = (List<String>) q.getResultList();
		if (p != null) {
			return p;
		} else {
			return null;
		}
	}
}
