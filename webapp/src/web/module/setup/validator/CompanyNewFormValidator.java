package web.module.setup.validator;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import module.setup.model.Company;
import module.setup.service.CompanyService;
import web.module.setup.model.CompanyForm;

public class CompanyNewFormValidator implements Validator {

	private CompanyService companyService;

	public CompanyNewFormValidator(CompanyService companyService) {
		this.companyService = companyService;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return CompanyForm.class.equals(arg0);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "company.name", "setup.company.required.name");
		ValidationUtils.rejectIfEmpty(errors, "company.code", "setup.company.required.code");
		
		CompanyForm companyForm = CompanyForm.class.cast(obj);
		String bizRegNo = companyForm.getCompany().getBizRegNo();
		if (StringUtils.isNotEmpty(bizRegNo)) {
			List<Company> companyList = companyService.getCompanyByBizRegNo(bizRegNo);
			if (companyList != null && !companyList.isEmpty()) {
				errors.reject("setup.company.bizRegNo_exists", new Object[] { bizRegNo }, "error");
			}
		}
		
		if (!StringUtils.isNotEmpty(companyForm.getCompany().getCode())) {
			List<Company> companyList = companyService.getCompanyByCode(companyForm.getCompany().getCode());
			if (companyList != null && !companyList.isEmpty()) {
				errors.reject("setup.company.code_exists", new Object[] { companyForm.getCompany().getCode() }, "error");
			}
		}
	}
}
