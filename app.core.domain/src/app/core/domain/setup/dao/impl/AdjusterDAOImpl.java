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

import app.core.domain.setup.dao.AdjusterDAO;
import app.core.domain.setup.model.Adjuster;

@Repository
public class AdjusterDAOImpl implements AdjusterDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdjusterDAOImpl.class);
	
	@Autowired
    private SessionFactory sessionFactory;

	@Override
	public List<Adjuster> findAll() {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Adjuster> query = builder.createQuery(Adjuster.class);
		query.from(Adjuster.class);
        List<Adjuster> result = (List<Adjuster>) session.createQuery(query).getResultList();
        Optional.ofNullable(result).ifPresent(name -> LOGGER.info("" + result.size())); // TODO READ
    	return result;
	}

	@Override
	public Adjuster findOneById(Long id) {
		Session session = sessionFactory.getCurrentSession();
		Adjuster obj = session.get(Adjuster.class, id);
    	return obj;
	}

	@Override
	public Adjuster create(Adjuster entity) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(entity);
		return entity;
	}

	@Override
	public Adjuster update(Adjuster entity) {
		Session session = sessionFactory.getCurrentSession();
		return (Adjuster) session.merge(entity);
	}

	@Override
	public boolean deleteById(Long id) {
		Session session = sessionFactory.getCurrentSession();
		Adjuster adjuster = findOneById(id);
		if (adjuster != null) {
			session.delete(adjuster);
			return true;
		}
		return false;
	}

	@Override
	public String findAllByIds(Long[] selected) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<String> query = builder.createQuery(String.class);
		Root<Adjuster> root = query.from(Adjuster.class);

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
	public boolean findOneByNameAndActiveFlag(String firmName, Boolean activeFlag) {
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Adjuster> query = builder.createQuery(Adjuster.class);
		Root<Adjuster> root = query.from(Adjuster.class);
		Predicate name = builder.equal(root.get("firmName"), firmName);
		Predicate active = builder.equal(root.get("activeFlag"), activeFlag);
		query.where(name, active);
        List<Adjuster> result = (List<Adjuster>) session.createQuery(query).getResultList();
        return CollectionUtils.isEmpty(result);
	}

}
