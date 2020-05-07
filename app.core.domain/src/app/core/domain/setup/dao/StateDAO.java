package app.core.domain.setup.dao;

import app.core.domain.setup.model.State;

public interface StateDAO {

	public State findOneByCode(String stateCode);

}