package module.report.handler;

import java.sql.Timestamp;
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
import module.setup.model.InsuranceClass;
import module.setup.model.UserCompany;
import module.setup.service.CompanyService;
import module.setup.service.InsuranceClassService;
import module.setup.service.SetupConstant;

public class OverallClaimStatBySystemParamHandler extends ReportParamHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(OverallClaimStatParamHandler.class);

	@Override
	public void setupLookup() {
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

		paramStr.append("fromLossDate=");
		if (paramMap.get("fromLossDate") != null && paramMap.get("fromLossDate") instanceof String) {
			Timestamp fromLossDate = null;
			String strfromLossDate = (String) paramMap.get("fromLossDate");
			try {
				DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MMM-yyyy");
				DateTime dt = formatter.parseDateTime(strfromLossDate);
				dt = dt.withTimeAtStartOfDay();
				fromLossDate = new Timestamp(dt.getMillis());
				paramStr.append(fromLossDate);
				LOGGER.info("from Loss Date=" + fromLossDate);
			} catch (Exception ignore) {
				LOGGER.warn("Error in formating fromLossDate [" + strfromLossDate + "]");
			}

			paramMap.put("fromLossDate", fromLossDate);
			paramMap.put("dispFromLossDate", paramMap.get("fromLossDate"));
		}

		paramStr.append(", ");
		paramStr.append("toLossDate=");
		if (paramMap.get("toLossDate") != null && paramMap.get("toLossDate") instanceof String) {
			Timestamp toLossDate = null;
			String strtoLossDate = (String) paramMap.get("toLossDate");
			// 01-Jan-2016
			try {
				DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MMM-yyyy");
				DateTime dt = formatter.parseDateTime(strtoLossDate);
				dt = dt.plusDays(1).withTimeAtStartOfDay().minusMillis(1);
				toLossDate = new Timestamp(dt.getMillis());
				paramStr.append(toLossDate);
				LOGGER.info("to Loss Date=" + toLossDate);
			} catch (Exception ignore) {
				LOGGER.warn("Error in formating toLossDate [" + strtoLossDate + "]");
			}

			paramMap.put("toLossDate", toLossDate);
			paramMap.put("dispToLossDate", paramMap.get("toLossDate"));
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
			} else {
				paramMap.put("insuranceClass", null);
			}

			paramMap.put("dispInsuranceClass", dispInsuranceClass.toString());
		}

		return paramStr.toString();
	}

	@Override
	public void validateParams(BindingResult result) {
		// TODO Auto-generated method stub
	}
}
