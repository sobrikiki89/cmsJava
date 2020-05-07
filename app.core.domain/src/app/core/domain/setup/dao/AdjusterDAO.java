package app.core.domain.setup.dao;

import java.util.List;

import app.core.domain.setup.model.Adjuster;

public interface AdjusterDAO {

	public List<Adjuster> findAll();

	public Adjuster findOneById(Long id);

	public Adjuster create(Adjuster entity);

	public Adjuster update(Adjuster entity);

	public boolean deleteById(Long id);

	public String findAllByIds(Long[] selected);

	public boolean findOneByNameAndActiveFlag(String firmName, Boolean activeFlag);
	
}

//public interface AdjusterDAO extends CrudRepository<Adjuster, Long>{}