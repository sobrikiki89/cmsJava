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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.domain.setup.model.Contact;
import app.core.dto.Entity2DTOMapper;
import app.core.exception.BaseApplicationException;
import app.core.model.EntityBase;
import app.core.service.impl.AbstractServiceImpl;
import module.setup.dto.InsurerDTO;
import module.setup.model.Insurer;
import module.setup.service.InsurerService;

@Service
@SuppressWarnings("unchecked")
public class InsurerServiceImpl extends AbstractServiceImpl implements InsurerService {
	private static final Logger LOGGER = LoggerFactory.getLogger(InsurerServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional
	@SuppressWarnings("rawtypes")
	public DataSet<InsurerDTO> getInsurers(DatatablesCriterias criteria) throws BaseApplicationException {
		try {
			Entity2DTOMapper mapper = new Entity2DTOMapper<Insurer, InsurerDTO>() {
				@Override
				public InsurerDTO map(Insurer entity) {
					InsurerDTO dto = new InsurerDTO();
					dto.setCode(entity.getCode());
					dto.setName(entity.getName());
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

			StringBuilder queryBuilder = new StringBuilder("SELECT o FROM " + Insurer.class.getName() + " o");

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

			String countHQL = new String("SELECT COUNT(o) FROM " + Insurer.class.getName() + " o");
			query = session.createQuery(countHQL);
			Long count = (Long) query.uniqueResult();
			
			query = session.createQuery(queryBuilder.toString());
			if (StringUtils.isNotBlank(criteria.getSearch())) {
				query.setParameter("searchCrit", "%"+criteria.getSearch().toLowerCase()+"%");
			}
			Long countFiltered = Long.parseLong(String.valueOf(query.list().size()));

			return new DataSet<InsurerDTO>(result, count, countFiltered);
		} catch (Exception e) {
			throw new BaseApplicationException("failed getDTOObjectList", e);
		}
	}
	
	@Transactional(readOnly = true)
	public Insurer getInsurer(String code) {
		Session session = sessionFactory.getCurrentSession();
		Query<Insurer> q = session.createQuery("SELECT o FROM " + Insurer.class.getName() 
				+ " o WHERE UPPER(o.code) = :code").setParameter("code", code.toUpperCase());
		return (Insurer) q.uniqueResult();
	}

	@Transactional
	public String createInsurer(Insurer insurer) {
		Session session = sessionFactory.getCurrentSession();
		String upperCaseCode = insurer.getCode().toUpperCase();
		insurer.setCode(upperCaseCode);
		if (isContactFieldsBlank(insurer.getContact())) {
			insurer.setContact(null);
		} else {
			if (insurer.getContact().getState() != null
					&& StringUtils.isEmpty(insurer.getContact().getState().getCode())) {
				insurer.getContact().setState(null);
			}
			session.save(insurer.getContact());
		}
		return (String) session.save(insurer);
	}

	@Transactional
	public Insurer updateInsurer(Insurer insurer) {
		Session session = sessionFactory.getCurrentSession();
		if (isContactFieldsBlank(insurer.getContact())) {
			if (insurer.getContact().getId() != null) {
				session.delete(insurer.getContact());
			}
			insurer.setContact(null);
		} else {
			if (insurer.getContact().getState() != null
					&& StringUtils.isEmpty(insurer.getContact().getState().getCode())) {
				insurer.getContact().setState(null);
			}
			session.saveOrUpdate(insurer.getContact());
		}
		return (Insurer) session.merge(insurer);
	}

	@Transactional(readOnly = true)
	public List<Insurer> getInsurers() {
		Session session = sessionFactory.getCurrentSession();
		Query<Insurer> q = session.createQuery(
				"SELECT o FROM " + Insurer.class.getName() + " o WHERE o.active = true ORDER BY o.name ASC");
		return q.list();
	}

	@Transactional(readOnly = true)
	public List<Insurer> getAllInsurers() {
		Session session = sessionFactory.getCurrentSession();
		Query<Insurer> q = session.createQuery(
				"SELECT o FROM " + Insurer.class.getName() + " o ORDER BY o.name ASC");
		return q.list();
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
}
