package module.report.handler;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import module.setup.model.UserCompany;
import module.setup.service.CompanyService;
import module.setup.service.InsuranceClassService;
import module.setup.service.SetupConstant;

public class PremiumVsClaimParamHandler extends ReportParamHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PremiumVsClaimParamHandler.class);
	
	SimpleDateFormat sdfD = new SimpleDateFormat("dd-MM-yyyy");

	public PremiumVsClaimParamHandler() {
		super();
	}
	
	@Override
	public void setupLookup() {
		CompanyService companyService = context.getBean(CompanyService.class);
		lookupMap.put("companies", companyService.getAllCompaniesWithoutBroker());
		
		InsuranceClassService insuranceClassService = context.getBean(InsuranceClassService.class);
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

		if (paramMap.get("companies") != null) {
			StringBuilder dispCompanies = new StringBuilder();
			List<Long> companyIdList = new ArrayList<Long>();
			if (paramMap.get("companies").getClass().isArray()) {
				LOGGER.info("Multiple company selected");
				String[] lstCompanies = (String[]) paramMap.get("companies");
				for (String companyId : lstCompanies) {
					try {
						if (!companyIdList.isEmpty()) {
							paramStr.append(",");
							dispCompanies.append(", ");
						}

						companyIdList.add(Long.parseLong(companyId));
						Company comp = companyService.getCompany(Long.parseLong(companyId));
						if (comp != null) {
							dispCompanies.append(comp.getName());
						}
						paramStr.append(companyId);
						LOGGER.info("company Id=" + companyId);
					} catch (NumberFormatException ignore) {
						LOGGER.warn("Error in formating company id [" + companyId + "]");
					}
				}
			} else if (paramMap.get("companies") instanceof String) {
				LOGGER.info("Single company selected");
				String strCompanyId = (String) paramMap.get("companies");
				try {
					companyIdList.add(Long.parseLong(strCompanyId));
					Company comp = companyService.getCompany(Long.parseLong(strCompanyId));
					if (comp != null) {
						dispCompanies.append(comp.getName());
					}
					paramStr.append(strCompanyId);
					LOGGER.info("company Id=" + strCompanyId);
				} catch (NumberFormatException ignore) {
					LOGGER.warn("Error in formating company id [" + strCompanyId + "]");
				}
			}

			if (!companyIdList.isEmpty()) {
				paramMap.put("companies", companyIdList.toArray());
			} else {
				paramMap.put("companies", null);
			}

			paramMap.put("dispCompanies", dispCompanies.toString());
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
		if (paramMap.get("fromDate") != null && paramMap.get("fromDate") instanceof String) {
			Timestamp fromDate = null;
			String strfromDate = (String) paramMap.get("fromDate");
			try {
				DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MMM-yyyy");
				DateTime dt = formatter.parseDateTime(strfromDate);
				dt = dt.withTimeAtStartOfDay();
				fromDate = new Timestamp(dt.getMillis());
				paramStr.append(fromDate);
				LOGGER.info("from Loss Date=" + fromDate);
			} catch (Exception ignore) {
				LOGGER.warn("Error in formating fromDate [" + strfromDate + "]");
			}

			paramMap.put("fromDate", fromDate);
			paramMap.put("dispFromDate", paramMap.get("fromDate"));
		}

		paramStr.append(", ");
		paramStr.append("toDate=");
		if (paramMap.get("toDate") != null && paramMap.get("toDate") instanceof String) {
			Timestamp toDate = null;
			String strtoDate = (String) paramMap.get("toDate");
			// 01-Jan-2016
			try {
				DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MMM-yyyy");
				DateTime dt = formatter.parseDateTime(strtoDate);
				dt = dt.withTimeAtStartOfDay();
				toDate = new Timestamp(dt.getMillis());
				paramStr.append(toDate);
				LOGGER.info("to Loss Date=" + toDate);
			} catch (Exception ignore) {
				LOGGER.warn("Error in formating toDate [" + strtoDate + "]");
			}

			paramMap.put("toDate", toDate);
			paramMap.put("dispToDate", paramMap.get("toDate"));
		}

		paramStr.append(", ");
		paramStr.append("insuranceClass=");
		List<String> insuranceClassCodeList = new ArrayList<>();
		StringBuilder dispInsuranceClass = new StringBuilder();
		if (paramMap.get("insuranceClass") != null) {
			InsuranceClassService insuranceClassService = context.getBean(InsuranceClassService.class);

			if (paramMap.get("insuranceClass").getClass().isArray()) {
				LOGGER.info("Multiple insurance class selected");
				String[] lstInsuranceClass = (String[]) paramMap.get("insuranceClass");
				for (String insuranceClassCode : lstInsuranceClass) {
					if (!StringUtils.isBlank(insuranceClassCode)) {
						if (insuranceClassCodeList.size() > 0) {
							paramStr.append(",");
							dispInsuranceClass.append(", ");
						}

						InsuranceClass insuranceClass = insuranceClassService.getInsuranceClass(insuranceClassCode);
						if (insuranceClass != null) {
							insuranceClassCodeList.add(insuranceClassCode);
							paramStr.append(insuranceClassCode);
							dispInsuranceClass.append(insuranceClass.getDropdownLabel());
						}

						LOGGER.info("Insurance Class=" + insuranceClassCode);
					}
				}
			} else if (paramMap.get("insuranceClass") instanceof String) {
				LOGGER.info("Single insurance class selected");
				String insuranceClassCode = (String) paramMap.get("insuranceClass");
				if (!StringUtils.isBlank(insuranceClassCode)) {
					InsuranceClass insuranceClass = insuranceClassService.getInsuranceClass(insuranceClassCode);
					if (insuranceClass != null) {
						insuranceClassCodeList.add(insuranceClassCode);
						paramStr.append(insuranceClassCode);
						dispInsuranceClass.append(insuranceClass.getDropdownLabel());
					}
					LOGGER.info("Insurance Class=" + insuranceClassCode);
				}
			}

			if (!insuranceClassCodeList.isEmpty()) {
				paramMap.put("insuranceClass", insuranceClassCodeList.toArray());
				paramMap.put("dispInsuranceClass", dispInsuranceClass.toString());
			} else {
				paramMap.put("insuranceClass", null);
				paramMap.put("dispInsuranceClass", null);
			}
		}

		return paramStr.toString();
	}

	@Override
	public void validateParams(BindingResult result) {
		// TODO Auto-generated method stub
	}

}
