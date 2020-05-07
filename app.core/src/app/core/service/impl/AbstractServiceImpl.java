package app.core.service.impl;

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
import org.springframework.transaction.annotation.Transactional;

import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.dto.Entity2DTOMapper;
import app.core.exception.BaseApplicationException;
import app.core.model.EntityBase;

public abstract class AbstractServiceImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	public StringBuilder getFilterQuery(DatatablesCriterias criterias) {
		StringBuilder queryBuilder = new StringBuilder();
		List<String> paramList = new ArrayList<String>();

		/**
		 * Step 1.1: global filtering
		 */
		if (StringUtils.isNotBlank(criterias.getSearch()) && criterias.hasOneSearchableColumn()) {
			queryBuilder.append(" WHERE ");

			for (ColumnDef columnDef : criterias.getColumnDefs()) {
				if (columnDef.isSearchable() && StringUtils.isBlank(columnDef.getSearch())) {
					paramList.add(" LOWER(o." + columnDef.getName()
							+ ") LIKE '%?%'".replace("?", criterias.getSearch().toLowerCase()));
				}
			}

			Iterator<String> itr = paramList.iterator();
			while (itr.hasNext()) {
				queryBuilder.append(itr.next());
				if (itr.hasNext()) {
					queryBuilder.append(" OR ");
				}
			}
		}

		/**
		 * Step 1.2: individual column filtering
		 */
		if (criterias.hasOneSearchableColumn() && criterias.hasOneFilteredColumn()) {
			paramList = new ArrayList<String>();

			if (!queryBuilder.toString().contains("WHERE")) {
				queryBuilder.append(" WHERE ");
			} else {
				queryBuilder.append(" AND ");
			}

			for (ColumnDef columnDef : criterias.getColumnDefs()) {
				if (columnDef.isSearchable()) {
					if (StringUtils.isNotBlank(columnDef.getSearchFrom())) {
						if (columnDef.getName().equalsIgnoreCase("birthDate")) {
							paramList.add("o." + columnDef.getName() + " >= '" + columnDef.getSearchFrom() + "'");
						} else {
							paramList.add("o." + columnDef.getName() + " >= " + columnDef.getSearchFrom());
						}
					}

					if (StringUtils.isNotBlank(columnDef.getSearchTo())) {
						if (columnDef.getName().equalsIgnoreCase("birthDate")) {
							paramList.add("o." + columnDef.getName() + " < '" + columnDef.getSearchTo() + "'");
						} else {
							paramList.add("o." + columnDef.getName() + " < " + columnDef.getSearchTo());
						}
					}

					if (StringUtils.isNotBlank(columnDef.getSearch())) {
						paramList.add(" LOWER(o." + columnDef.getName()
								+ ") LIKE '%?%'".replace("?", columnDef.getSearch().toLowerCase()));
					}
				}
			}

			Iterator<String> itr = paramList.iterator();
			while (itr.hasNext()) {
				queryBuilder.append(itr.next());
				if (itr.hasNext()) {
					queryBuilder.append(" AND ");
				}
			}
		}

		return queryBuilder;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DataSet getListByCriteria(DatatablesCriterias criterias, Class<? extends EntityBase> entity,
			Entity2DTOMapper mapper) throws BaseApplicationException {
		try {
			StringBuilder queryBuilder = new StringBuilder("SELECT o FROM " + entity.getName() + " o");

			/**
			 * Step 1: global and individual column filtering
			 */
			queryBuilder.append(getFilterQuery(criterias));

			/**
			 * Step 2: sorting
			 */
			if (criterias.hasOneSortedColumn()) {

				List<String> orderParams = new ArrayList<String>();
				queryBuilder.append(" ORDER BY ");
				for (ColumnDef columnDef : criterias.getSortedColumnDefs()) {
					orderParams.add("o." + columnDef.getName() + " " + columnDef.getSortDirection());
				}

				Iterator<String> itr2 = orderParams.iterator();
				while (itr2.hasNext()) {
					queryBuilder.append(itr2.next());
					if (itr2.hasNext()) {
						queryBuilder.append(" , ");
					}
				}
			}

			Session session = sessionFactory.getCurrentSession();
			session.setHibernateFlushMode(FlushMode.MANUAL);
			Query query = session.createQuery(queryBuilder.toString());

			/**
			 * Step 3: paging
			 */
			query.setFirstResult(criterias.getStart());
			query.setMaxResults(criterias.getLength());

			List objects = query.list();
			List result = new ArrayList();			
			for(Object object : objects) {
				result.add(mapper.map((EntityBase)object));				
			}
			
			String countHQL = "SELECT COUNT(o) FROM " + entity.getName() + " o";
			query = session.createQuery(countHQL);
			Long count = (Long) query.uniqueResult();

			queryBuilder.append(getFilterQuery(criterias));
			query = session.createQuery(queryBuilder.toString());
			Long countFiltered = Long.parseLong(String.valueOf(query.list().size()));

			return new DataSet(result, count, countFiltered);

		} catch (Exception e) {
			throw new BaseApplicationException("failed getListByCriteria", e);
		}
	}
}
