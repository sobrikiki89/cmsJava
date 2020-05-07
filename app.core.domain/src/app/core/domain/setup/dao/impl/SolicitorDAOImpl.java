package app.core.domain.setup.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import app.core.domain.setup.dao.SolicitorDAO;
import app.core.domain.setup.model.Solicitor;

@Repository
public class SolicitorDAOImpl implements SolicitorDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(SolicitorDAOImpl.class);
	
	@Autowired
    private SessionFactory sessionFactory;

	@Override
	public List<Solicitor> findAll() {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Solicitor> query = builder.createQuery(Solicitor.class);
		query.from(Solicitor.class);
        List<Solicitor> result = (List<Solicitor>) session.createQuery(query).getResultList();
        Optional.ofNullable(result).ifPresent(name -> LOGGER.info("" + result.size())); // TODO READ
    	return result;
	}
	
	@Override
	public Solicitor findOneById(Long id) {
		Session session = sessionFactory.getCurrentSession();
		Solicitor obj = session.get(Solicitor.class, id);
    	return obj;
	}

	@Override
	public Solicitor create(Solicitor entity) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(entity);
		return entity;
	}

	@Override
	public Solicitor update(Solicitor solicitor) {
		Session session = sessionFactory.getCurrentSession();
		return (Solicitor) session.merge(solicitor);
	}

	@Override
	public boolean deleteById(Long id) {
		Session session = sessionFactory.getCurrentSession();
		Solicitor solicitor = findOneById(id);
		if (solicitor != null) {
			session.delete(solicitor);
			return true;
		}
		return false;
	}

	@Override
	public String findUsedAssociationByIds(Long[] selected) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<String> query = builder.createQuery(String.class);
		Root<Solicitor> root = query.from(Solicitor.class);

		In<Long> inClause = builder.in(root.get("id"));
		for (Long id : selected) {
			inClause.value(id);
		}
		query.select(root.get("firmName")).where(inClause);

		List<String> resultList = session.createQuery(query).getResultList();
        if (resultList.size() > 0 || resultList.isEmpty()) {
        	String result = String.join(",", resultList);
        	return result;
        }
        
        return null;
	}

	@Override
	public boolean findOneByNameAndActiveFlag(String firmName, boolean activeFlag) {
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Solicitor> query = builder.createQuery(Solicitor.class);
		Root<Solicitor> root = query.from(Solicitor.class);
		Predicate name = builder.equal(root.get("firmName"), firmName);
		Predicate active = builder.equal(root.get("activeFlag"), activeFlag);
		query.where(name, active);
        List<Solicitor> result = (List<Solicitor>) session.createQuery(query).getResultList();
        return CollectionUtils.isEmpty(result);
	}

	// CriteriaDelete<Solicitor> criteriaQuery = criterBuilder.createCriteriaDelete(Solicitor.class);
}
