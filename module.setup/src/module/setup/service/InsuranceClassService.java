package module.setup.service;

import java.util.List;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.exception.BaseApplicationException;
import module.setup.dto.InsuranceClassDTO;
import module.setup.model.InsuranceClass;
import module.setup.model.InsuranceClassCategory;

public interface InsuranceClassService {
	public DataSet<InsuranceClassDTO> getInsuranceClasses(DatatablesCriterias criterias)
			throws BaseApplicationException;

	public InsuranceClass getInsuranceClass(String code);

	public String createInsuranceClass(InsuranceClass insurer);

	public InsuranceClass updateInsuranceClass(InsuranceClass insurer);

	public List<InsuranceClass> getInsuranceClasses();

	public void deleteObject(String code);

	public InsuranceClass getUniqueSortOrder(Long sortOrder);

	public List<InsuranceClassCategory> getCategories();

	public InsuranceClassCategory getClassCategoryByCode(String code);

	public List<InsuranceClass> getInsuranceClassesByGroup(String code);
}
