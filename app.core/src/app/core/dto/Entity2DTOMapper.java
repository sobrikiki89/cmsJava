package app.core.dto;

import app.core.model.EntityBase;

public interface Entity2DTOMapper<E extends EntityBase, T extends DTOBase> {
	public T map(E entity);
}
