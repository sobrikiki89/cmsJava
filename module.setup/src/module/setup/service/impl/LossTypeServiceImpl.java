package module.setup.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.FlushMode;
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
import module.setup.dto.LossTypeDTO;
import module.setup.model.LossType;
import module.setup.service.LossTypeService;

@Service
@SuppressWarnings("unchecked")
public class LossTypeServiceImpl extends AbstractServiceImpl implements LossTypeService {
	private static final Logger LOGGER = LoggerFactory.getLogger(LossTypeServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings({ "rawtypes" })
	@Transactional(readOnly = true)
	public DataSet<LossTypeDTO> getLossTypes(DatatablesCriterias criteria) throws BaseApplicationException {
		try {
			Entity2DTOMapper mapper = new Entity2DTOMapper<LossType, LossTypeDTO>(){
				@Override
				public LossTypeDTO map(LossType entity) {
					LossTypeDTO dto = new LossTypeDTO();
					dto.setCode(entity.getCode());
					dto.setName(entity.getName());
					dto.setSortOrder(entity.getSortOrder());
					dto.setActiveFlag(entity.getActiveFlag());
					return dto;
				}
			};
					
			StringBuilder queryBuilder = new StringBuilder("SELECT o FROM " + LossType.class.getName() + " o");
			
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

			String countHQL = "SELECT COUNT(o) FROM " + LossType.class.getName() + " o";
			query = session.createQuery(countHQL);
			Long count = (Long) query.uniqueResult();

			query = session.createQuery(queryBuilder.toString());
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				query.setParameter("searchCrit", "%"+criteria.getSearch().toLowerCase()+"%");
			}
			
			Long countFiltered = Long.parseLong(String.valueOf(query.list().size()));

			return new DataSet(result, count, countFiltered);
			
		}  catch (Exception e) {
			throw new BaseApplicationException("failed getLossTypes", e);
		}
	}
	
	@Transactional(readOnly = true)
	public LossType getLossType(String code) {
		Session session = sessionFactory.getCurrentSession();
		Query<LossType> q = session.createQuery("SELECT o FROM " + LossType.class.getName() + " o WHERE o.code = :code")
				.setParameter("code", code);
		return (LossType) q.uniqueResult();
	}

	@Transactional(readOnly = true)
	public List<LossType> getLossTypes() {
		Session session = sessionFactory.getCurrentSession();
		Query<LossType> q = session.createQuery("SELECT o FROM " + LossType.class.getName() + " o");
		return (List<LossType>) q.list();
	}

	@Transactional(readOnly = true)
	public List<LossType> getActiveLossTypes() {		
		Session session = sessionFactory.getCurrentSession(); 
        CriteriaBuilder criterBuilder = session.getCriteriaBuilder();
        CriteriaQuery<LossType> criteriaQuery = criterBuilder.createQuery(LossType.class);
        Root<LossType> root = criteriaQuery.from(LossType.class);
        criteriaQuery.where(criterBuilder.equal(root.get("activeFlag"), true));
        List<LossType> lossTypes = (List<LossType>) session.createQuery(criteriaQuery).getResultList();
        Collections.sort(lossTypes, Comparator.comparing(LossType::getSortOrder)); 
        return lossTypes;
	}

	@Transactional
	public String createLossType(LossType obj) {
		Session session = sessionFactory.getCurrentSession();
		String objCode = null;
		try{
			String upperCaseCode = obj.getCode().toUpperCase();
			obj.setCode(upperCaseCode);
			objCode = (String)session.save(obj);
		}catch(Exception ex){
			LOGGER.error(ex.toString());
		}
		return objCode;
	}

	@Transactional
	public LossType updateLossType(LossType insuranceClass) {
		Session session = sessionFactory.getCurrentSession();
		return (LossType) session.merge(insuranceClass);
	}

	@Transactional
	public LossType getUniqueSortOrder(Long sortOrder) {
		Session session = sessionFactory.getCurrentSession();
		List<LossType> lossType = new ArrayList<LossType>();
		Query<LossType> q = session.createQuery("SELECT o FROM " + LossType.class.getName() + " o WHERE o.sortOrder = :sortOrder")
				.setParameter("sortOrder", sortOrder);
		lossType = q.list();
		if (lossType.size() > 0) {
			return lossType.get(0);
		} else {
			return null;
		}
	}
	
	@Transactional
	public void deleteObject(String code) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(session.get(LossType.class, code));
	}

}
