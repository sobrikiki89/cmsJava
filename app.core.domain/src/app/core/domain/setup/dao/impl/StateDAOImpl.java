package app.core.domain.setup.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import app.core.domain.setup.dao.StateDAO;
import app.core.domain.setup.model.State;

@Repository
public class StateDAOImpl implements StateDAO {

	@Autowired
    private SessionFactory sessionFactory;

	@Override
	public State findOneByCode(String code) {
		Session session = sessionFactory.getCurrentSession();
		State obj = session.get(State.class, code);
    	return obj;
	}
}
