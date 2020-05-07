package module.report.handler;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;

import app.core.security.UserPrincipal;
import module.setup.model.Company;
import module.setup.model.InsuranceClass;
import module.setup.model.InsuranceClassCategory;
import module.setup.model.UserCompany;
import module.setup.service.CompanyService;
import module.setup.service.InsuranceClassService;
import module.setup.service.SetupConstant;

public class PremiumTabulationDetailsParamHandler extends ReportParamHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PremiumTabulationDetailsParamHandler.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdfD = new SimpleDateFormat("dd-MM-yyyy");
	
	public PremiumTabulationDetailsParamHandler() {
		super();
		// Initialization
	}

	@Override
	public void setupLookup() {
		CompanyService companyService = context.getBean(CompanyService.class);
		lookupMap.put("companies", companyService.getAllCompaniesWithoutBroker());

		InsuranceClassService insuranceClassService = context.getBean(InsuranceClassService.class);
		lookupMap.put("groupOfInsurance", insuranceClassService.getCategories());
		lookupMap.put("insuranceClasses", insuranceClassService.getInsuranceClasses());
	}

	@Override
	public String formatParams() {
		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

		StringBuilder paramStr = new StringBuilder();
		LOGGER.info("#### [Parameters] #### ");
		CompanyService companyService = context.getBean(CompanyService.class);
		List<UserCompany> userCompanyList = (List<UserCompany>) companyService.getUserCompany(principal);
		List<String> userCompanyCode = new ArrayList<String>();

		boolean isSIB = false;
		if (userCompanyList != null) {
			for (UserCompany userCompany : userCompanyList) {
				if (SetupConstant.COMPANY_CODE_SIB.equals(userCompany.getCompanyId().getCode())) {
					isSIB = true;
				} else {
					Long companyId = userCompany.getCompanyId().getId();
					LOGGER.info("company Id=" + companyId);

					userCompanyCode.add(userCompany.getCompanyId().getCode());
				}
			}
		}

		if (isSIB) {
			// We do not need to filter user company if it is SIB, as we are
			// going to search for all company
			paramMap.put("userCompany", null);
		} else {
			if (!userCompanyCode.isEmpty()) {
				paramMap.put("userCompany", userCompanyCode.toArray());
			} else {
				// The purpose of _NULL here is to block user from selecting the
				// company if he/she has no company assigned
				paramMap.put("userCompany", new Object[] { "_NULL" });
			}
		}

		if (paramMap.containsKey("companies")) {
			paramStr.append("companyId=");
		}

		if (paramMap.get("companies") != null && !"".equals(paramMap.get("companies"))) {
			Long companyId = Long.parseLong((String) paramMap.get("companies"));
			paramMap.put("companies", companyId);
			for (Company company : (List<Company>) lookupMap.get("companies")) {
				if (company.getId() == companyId) {
					paramMap.put("dispCompanyId", company.getName());
					break;
				}
			}
		} else {
			paramMap.put("companies", null);
			paramMap.put("dispCompanyId", null);
		}

		Object[] userCompanies = (Object[]) paramMap.get("userCompany");
		if (userCompanies != null) {
			if (userCompanies.length > 1) {
				paramMap.put("showCompany", Boolean.TRUE);
			} else {
				paramMap.put("showCompany", Boolean.FALSE);
			}
		} else {
			// SIB, show company
			paramMap.put("showCompany", Boolean.TRUE);
		}

		if (paramMap.containsKey("companies")) {
			paramStr.append(", ");
		}

		paramStr.append("fromDate=");
		paramMap.put("dispFromDate", null);
		if (paramMap.get("fromDate") != null && !"".equals(paramMap.get("fromDate")) && paramMap.get("fromDate") instanceof String) {
			Timestamp fromDate = null;
			String strFromDate = (String) paramMap.get("fromDate");
			try {
				DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MMM-yyyy");
				DateTime dt = formatter.parseDateTime(strFromDate);
				dt = dt.withTimeAtStartOfDay();
				fromDate = new Timestamp(dt.getMillis());
				paramStr.append(fromDate);
				LOGGER.info("from Date=" + fromDate);
			} catch (Exception ignore) {
				LOGGER.warn("Error in formating fromDate [" + strFromDate + "]");
			}

			paramMap.put("fromDate", fromDate);
		} else {
			paramMap.put("fromDate", null);
		}
		paramMap.put("dispFromDate", paramMap.get("fromDate"));

		paramStr.append(", toDate=");
		paramMap.put("dispToDate", null);
		if (paramMap.get("toDate") != null  && !"".equals(paramMap.get("toDate")) &&  paramMap.get("toDate") instanceof String) {
			Timestamp toDate = null;
			String strToDate = (String) paramMap.get("toDate");
			// 01-Jan-2016
			try {
				DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MMM-yyyy");
				DateTime dt = formatter.parseDateTime(strToDate);
				dt = dt.withTimeAtStartOfDay();
				toDate = new Timestamp(dt.getMillis());
				paramStr.append(toDate);
				LOGGER.info("to Date=" + toDate);
			} catch (Exception ignore) {
				LOGGER.warn("Error in formating toDate [" + strToDate + "]");
			}

			paramMap.put("toDate", toDate);
			paramMap.put("dispToDate", toDate);
		} else {
			paramMap.put("toDate", null);
		}

		paramMap.put("dispGroupOfInsuranceCode", null);
		if("".equals(paramMap.get("groupOfInsuranceCode"))) {
			paramMap.put("groupInsuranceCode", null);
		} else {
			for (InsuranceClassCategory groupInsurance : (List<InsuranceClassCategory>) lookupMap.get("groupOfInsurance")) {
				if (groupInsurance.getCode().equals(paramMap.get("groupOfInsuranceCode"))) {
					paramMap.put("dispGroupOfInsuranceCode", groupInsurance.getName());
					break;
				}
			}
		}

		paramMap.put("dispInsuranceClassCode", null);
		if (paramMap.get("insuranceClassCode") != null) {
			InsuranceClassService insurerService = context.getBean(InsuranceClassService.class);

			StringBuilder dispInsuranceClasss = new StringBuilder();
			List<String> insuranceClassCodeList = new ArrayList<String>();
			if (paramMap.get("insuranceClassCode").getClass().isArray()) {
				LOGGER.info("Multiple Insurance Class selected");
				String[] lstInsuranceClasss = (String[]) paramMap.get("insuranceClassCode");
				for (String insuranceClassCode : lstInsuranceClasss) {
					if (insuranceClassCodeList.size() > 0) {
						dispInsuranceClasss.append(", ");
					}

					InsuranceClass insuranceClass = insurerService.getInsuranceClass(insuranceClassCode);
					if (insuranceClass != null) {
						dispInsuranceClasss.append(insuranceClass.getName());
					}

					insuranceClassCodeList.add(insuranceClassCode);
					LOGGER.info("Insurance Class Code=" + insuranceClassCode);
				}
			} else if (paramMap.get("insuranceClassCode") instanceof String) {
				LOGGER.info("Single InsuranceClass selected");
				String insuranceClassCode = (String) paramMap.get("insuranceClassCode");
				InsuranceClass insurer = insurerService.getInsuranceClass(insuranceClassCode);
				if (insurer != null) {
					dispInsuranceClasss.append(insurer.getName());
				}
				insuranceClassCodeList.add(insuranceClassCode);
				LOGGER.info("Insurance Class Code=" + insuranceClassCode);
			}

			if (insuranceClassCodeList.size() > 0) {
				paramMap.put("insuranceClassCode", insuranceClassCodeList.toArray());
			} else {
				paramMap.put("insuranceClassCode", null);
			}
			paramMap.put("dispInsuranceClassCode", dispInsuranceClasss.toString());
		}
		
		return paramStr.toString();
	}

	public void validateParams(BindingResult result) {

	}
}
