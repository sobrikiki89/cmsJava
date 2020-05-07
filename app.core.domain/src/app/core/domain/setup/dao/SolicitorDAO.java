package app.core.domain.setup.dao;

import java.util.List;

import app.core.domain.setup.model.Solicitor;

public interface SolicitorDAO {

	public List<Solicitor> findAll();
	
	public String findUsedAssociationByIds(Long[] selected);

	public Solicitor findOneById(Long id);

	public Solicitor create(Solicitor entity);

	public Solicitor update(Solicitor entity);

	public boolean deleteById(Long id);

	public boolean findOneByNameAndActiveFlag(String firmName, boolean activeFlag);
	
}

//public interface SolicitorDAO extends CrudRepository<Solicitor, Long>{}