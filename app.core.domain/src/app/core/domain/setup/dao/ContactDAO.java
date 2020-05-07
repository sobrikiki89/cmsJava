package app.core.domain.setup.dao;

import app.core.domain.setup.model.Contact;

public interface ContactDAO {
	
	public Contact findOneById(Long id);

	public Contact saveOrUpdate(Contact entity);

	public Contact update(Contact entity);

	public boolean deleteById(Long id);
	
}
