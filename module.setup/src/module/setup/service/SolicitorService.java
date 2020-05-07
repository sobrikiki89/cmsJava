package module.setup.service;

import java.util.List;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.exception.BaseApplicationException;
import module.setup.dto.ContactDTO;
import module.setup.dto.SolicitorDTO;

public interface SolicitorService {

	public List<SolicitorDTO> getAllSolicitors();

	public DataSet<SolicitorDTO> findSolicitorBy(DatatablesCriterias criterias) throws BaseApplicationException;

	public SolicitorDTO findSolicitorById(Long id);

	public Long postSolicitor(SolicitorDTO solicitorDTO, ContactDTO contactDTO);

	public Long postEditSolicitor(SolicitorDTO solicitorDTO, ContactDTO contactDTO);

	public String checkAssociationById(Long[] selected);

	public void postDeleteSolicitor(Long[] selected);

	public boolean findSolicitorByNameAndActiveFlag(String firmName, boolean activeFlag);

}
