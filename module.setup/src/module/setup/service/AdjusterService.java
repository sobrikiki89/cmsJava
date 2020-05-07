package module.setup.service;

import java.util.List;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.exception.BaseApplicationException;
import module.setup.dto.AdjusterDTO;
import module.setup.dto.ContactDTO;

public interface AdjusterService {

	public List<AdjusterDTO> getAllAdjusters();

	public DataSet<AdjusterDTO> findAdjusterBy(DatatablesCriterias criterias) throws BaseApplicationException;

	public AdjusterDTO findAdjusterById(Long id);

	public Long postAdjuster(AdjusterDTO adjusterDTO, ContactDTO contactDTO);

	public Long postEditAdjuster(AdjusterDTO adjusterDTO, ContactDTO contactDTO);

	public void postDeleteAdjuster(Long[] selected);

	public String checkAssociationById(Long[] selected);

	public boolean findAdjusterByNameAndActiveFlag(String firmName, Boolean activeFlag);

}
