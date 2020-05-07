package module.setup.service;

import java.util.List;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.exception.BaseApplicationException;
import module.setup.dto.LossTypeDTO;
import module.setup.model.LossType;

public interface LossTypeService {
	public DataSet<LossTypeDTO> getLossTypes(DatatablesCriterias criterias) throws BaseApplicationException;

	public LossType getLossType(String code);

	// Get all loss type
	public List<LossType> getLossTypes();

	public List<LossType> getActiveLossTypes();

	public String createLossType(LossType insurer);

	public LossType updateLossType(LossType insurer);

	public LossType getUniqueSortOrder(Long sortOrder);

	public void deleteObject(String code);
}
