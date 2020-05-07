package module.setup.service;

import java.util.List;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;

import app.core.exception.BaseApplicationException;
import app.core.security.UserPrincipal;
import app.core.usermgmt.model.User;
import module.setup.dto.CompanyDTO;
import module.setup.dto.UserCompanyDTO;
import module.setup.dto.UserDTO;
import module.setup.model.Company;
import module.setup.model.CompanyDepartment;
import module.setup.model.UserCompany;

public interface CompanyService {
	public Long createDefaultCompany(Company company);
	
	public DataSet<CompanyDTO> getCompanies(DatatablesCriterias criterias) throws BaseApplicationException;

	public Company getCompany(Long id);

	public List<Company> getCompanyByBizRegNo(String bizRegNo);

	public Long createCompany(Company company, List<UserCompany> uc, List<CompanyDepartment> departments);

	public Company updateCompany(Company company, List<UserCompany> uc, List<CompanyDepartment> departments);

	public List<Company> getAllCompanies();

	public List<Company> getAllCompaniesWithoutBroker();
	
	public List<Company> getAllCompaniesForSIB();
	
	public List<String> getCompanyNameList(Long companyId);

	public List<Company> getCompanyByCode(String code);

	public void deleteObject(Long id);

	public Company getCompByCode(String company);

	public List<UserDTO> getUserList();

	public List<Long> getUserListById(Long companyId);
	
	public User getUserById(Long userId);

	public DataSet<UserDTO> getUserGridNew(DatatablesCriterias criterias) throws BaseApplicationException;

	public List<UserDTO> getUserListEdit(Company company);
	
	public List<String> getAllEmailUnderSameCompany(String companyCode);

	public List<UserCompanyDTO> getUserCompanyList(Long id);

	public List<UserCompany> getUserCompany(UserPrincipal principal);
	
	public List<CompanyDepartment> getCompanyDepartment(Company company);

	public List<String> getUserCompaniesCode(UserPrincipal principal);
}
