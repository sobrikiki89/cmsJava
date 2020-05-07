package module.setup.service;

import java.util.List;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.exception.BaseApplicationException;
import module.setup.dto.InsurerDTO;
import module.setup.model.Insurer;

public interface InsurerService {
	public DataSet<InsurerDTO> getInsurers(DatatablesCriterias criterias) throws BaseApplicationException;

	public Insurer getInsurer(String code);

	public String createInsurer(Insurer insurer);

	public Insurer updateInsurer(Insurer insurer);

	public List<Insurer> getInsurers();
	
	public List<Insurer> getAllInsurers();
}
