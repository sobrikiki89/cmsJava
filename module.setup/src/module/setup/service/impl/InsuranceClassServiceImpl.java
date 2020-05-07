package module.setup.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.dto.Entity2DTOMapper;
import app.core.exception.BaseApplicationException;
import app.core.model.EntityBase;
import app.core.service.impl.AbstractServiceImpl;
import module.setup.dto.InsuranceClassDTO;
import module.setup.model.InsuranceClass;
import module.setup.model.InsuranceClassCategory;
import module.setup.service.InsuranceClassService;

@Service
@SuppressWarnings("unchecked")
public class InsuranceClassServiceImpl extends AbstractServiceImpl implements InsuranceClassService {
	private static final Logger LOGGER = LoggerFactory.getLogger(InsuranceClassServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional
	public DataSet<InsuranceClassDTO> getInsuranceClasses(DatatablesCriterias criteria) throws BaseApplicationException {
		try {
			Entity2DTOMapper mapper = new Entity2DTOMapper<InsuranceClass, InsuranceClassDTO>(){
				@Override
				public InsuranceClassDTO map(InsuranceClass entity) {
					InsuranceClassDTO dto = new InsuranceClassDTO();
					dto.setCode(entity.getCode());
					dto.setName(entity.getName());
					if (entity.getCategory()!=null) {
						dto.setCategory(entity.getCategory().getName());
					} else {
						dto.setCategory("");
					}
					dto.setSortOrder(entity.getSortOrder());
					dto.setActiveFlag(entity.getActiveFlag());
					return dto;
				}
			};
					
			StringBuilder queryBuilder = new StringBuilder("SELECT o FROM " + InsuranceClass.class.getName() + " o");
			
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
				whereClause.append(" LOWER(o.category.name) LIKE :searchCrit");
				whereClause.append(" OR");
				whereClause.append(" CAST(o.sortOrder AS text) LIKE :searchCrit");
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
				}  else if ("category".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.category " + colDef.getSortDirection());
				} else if ("sortOrder".equalsIgnoreCase(colDef.getName())) {
					sortClause.append("o.sortOrder " + colDef.getSortDirection());
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

			String countHQL = "SELECT COUNT(o) FROM " + InsuranceClass.class.getName() + " o";
			query = session.createQuery(countHQL);
			Long count = (Long) query.uniqueResult();

			query = session.createQuery(queryBuilder.toString());
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				query.setParameter("searchCrit", "%"+criteria.getSearch().toLowerCase()+"%");
			}
			
			Long countFiltered = Long.parseLong(String.valueOf(query.list().size()));

			return new DataSet(result, count, countFiltered);
			
		}  catch (Exception e) {
			throw new BaseApplicationException("failed getInsuranceClasses", e);
		}
	
	}

	@Transactional(readOnly = true)
	public InsuranceClass getInsuranceClass(String code) {
		Session session = sessionFactory.getCurrentSession();
		Query<InsuranceClass> q = session.createQuery("SELECT o FROM " + InsuranceClass.class.getName() + " o WHERE o.code = :code")
				.setParameter("code", code);
		InsuranceClass insuranceClass = q.uniqueResult();
		if (insuranceClass!=null) {
			Hibernate.initialize(insuranceClass.getCategory());
		}
		return insuranceClass;
	}

	@Transactional
	public String createInsuranceClass(InsuranceClass insuranceClass) {
		Session session = sessionFactory.getCurrentSession();
		String objCode = null;
		try{
			String upperCaseCode = insuranceClass.getCode().toUpperCase();
			insuranceClass.setCode(upperCaseCode);
			InsuranceClassCategory cat = this.getInsuranceClassCategory(insuranceClass.getCategory().getName());
			insuranceClass.setCategory(cat);
			objCode = (String)session.save(insuranceClass);
		}catch(Exception ex){
			LOGGER.error(ex.toString());
		}
		return objCode;
	}

	private InsuranceClassCategory getInsuranceClassCategory(String code) {
		Session session = sessionFactory.getCurrentSession();
		List<InsuranceClassCategory> categories = new ArrayList<InsuranceClassCategory>();
		Query<InsuranceClassCategory> q = session.createQuery("SELECT o FROM " + InsuranceClassCategory.class.getName() + " o WHERE o.code = :code")
				.setParameter("code", code);
		categories = q.list();
		if (categories.size() > 0) {
			return categories.get(0);
		} else {
			return null;
		}
	}

	@Transactional
	public InsuranceClass getUniqueSortOrder(Long sortOrder) {
		Session session = sessionFactory.getCurrentSession();
		List<InsuranceClass> insClass = new ArrayList<InsuranceClass>();
		Query<InsuranceClass> q = session.createQuery("SELECT o FROM " + InsuranceClass.class.getName() + " o WHERE o.sortOrder = :sortOrder")
				.setParameter("sortOrder", sortOrder);
		insClass = q.list();
		if (insClass.size() > 0) {
			return insClass.get(0);
		} else {
			return null;
		}
	}
	
	@Transactional
	public InsuranceClass updateInsuranceClass(InsuranceClass insuranceClass) {
		Session session = sessionFactory.getCurrentSession();
		return (InsuranceClass) session.merge(insuranceClass);
	}

	@Transactional(readOnly = true)
	public List<InsuranceClass> getInsuranceClasses() {
		Session session = sessionFactory.getCurrentSession();
		Query<InsuranceClass> q = session.createQuery(
				"SELECT o FROM " + InsuranceClass.class.getName() + " o WHERE o.activeFlag = true ORDER BY o.name ASC");
		return q.list();
	}

	@Transactional
	public void deleteObject(String code) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(session.get(InsuranceClass.class, code));
	}

	@Transactional
	public List<InsuranceClassCategory> getCategories() {
		Session session = sessionFactory.getCurrentSession();
		Query<InsuranceClassCategory> q = session.createQuery(
				"SELECT o FROM " + InsuranceClassCategory.class.getName() + " o WHERE o.activeFlag = true ORDER BY o.sortOrder ASC");
		return q.list();
	}

	@Transactional
	public InsuranceClassCategory getClassCategoryByCode(String code) {
		Session session = sessionFactory.getCurrentSession();
		Query<InsuranceClassCategory> q = session.createQuery(
				"SELECT o FROM " + InsuranceClassCategory.class.getName() + " o "
						+ "WHERE o.activeFlag = true and o.code = :code ORDER BY o.sortOrder ASC")
				.setParameter("code", code);
		return q.uniqueResult();
	}

	@Transactional
	public List<InsuranceClass> getInsuranceClassesByGroup(String code) {
		Session session = sessionFactory.getCurrentSession();
		List<InsuranceClass> list = new ArrayList<InsuranceClass>();
		Query<InsuranceClass> q = session.createQuery(
				"SELECT o FROM " + InsuranceClass.class.getName() + " o WHERE o.category.code = :code AND o.activeFlag = true ORDER BY o.name ASC")
				.setParameter("code", code);
		list = q.getResultList();
		return list;
	}

}
