package app.core.domain.setup.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import app.core.domain.setup.dao.ContactDAO;
import app.core.domain.setup.model.Contact;

@Repository
public class ContactDAOImpl implements ContactDAO {
	
	@Autowired
    private SessionFactory sessionFactory;

	@Override
	public Contact findOneById(Long id) {
		Session session = sessionFactory.getCurrentSession();
		Contact obj = session.get(Contact.class, id);
    	return obj;
	}

	@Override
	public Contact saveOrUpdate(Contact entity) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(entity);
		return entity;
	}

	@Override
	public Contact update(Contact solicitor) {
		Session session = sessionFactory.getCurrentSession();
		return (Contact) session.merge(solicitor);
	}

	@Override
	public boolean deleteById(Long id) {
		Session session = sessionFactory.getCurrentSession();
		Contact solicitor = findOneById(id);
		if (solicitor != null) {
			session.delete(solicitor);
			return true;
		}
		return false;
	}
	
}
