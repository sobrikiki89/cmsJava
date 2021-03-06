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

public class ClaimByClassOfInsuranceParamHandler extends ReportParamHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClaimByLossTypeParamHandler.class);
	
	SimpleDateFormat sdfD = new SimpleDateFormat("dd-MM-yyyy");

	public ClaimByClassOfInsuranceParamHandler() {
		super();
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
		}paramStr.append(", ");
		paramStr.append("GroupOfInsuranceCode=");
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

		paramStr.append(", ");
		paramStr.append("insuranceClassCode=");
		if (paramMap.get("insuranceClassCode") != null) {
			InsuranceClassService insurerService = context.getBean(InsuranceClassService.class);

			StringBuilder dispInsuranceClasss = new StringBuilder();
			List<String> insuranceClassCodeList = new ArrayList<String>();
			if (paramMap.get("insuranceClassCode").getClass().isArray()) {
				LOGGER.info("Multiple Insurance Class selected");
				String[] lstInsuranceClasss = (String[]) paramMap.get("insuranceClassCode");
				for (String insuranceClassCode : lstInsuranceClasss) {
					if (insuranceClassCodeList.size() > 0) {
						paramStr.append(",");
						dispInsuranceClasss.append(", ");
					}

					InsuranceClass insuranceClass = insurerService.getInsuranceClass(insuranceClassCode);
					if (insuranceClass != null) {
						dispInsuranceClasss.append(insuranceClass.getName());
					}

					insuranceClassCodeList.add(insuranceClassCode);
					paramStr.append(insuranceClassCode);
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
				paramStr.append(insuranceClassCode);
				LOGGER.info("Insurance Class Code=" + insuranceClassCode);
			}

			if (insuranceClassCodeList.size() > 0) {
				paramMap.put("insuranceClassCode", insuranceClassCodeList.toArray());
			} else {
				paramMap.put("insuranceClassCode", null);
			}
			paramMap.put("dispInsuranceClass", dispInsuranceClasss.toString());
		}

		paramStr.append("fromLossDate=");
		paramMap.put("dispFromLossDate", null);
		if (paramMap.get("fromLossDate") != null && paramMap.get("fromLossDate") instanceof String) {
			Timestamp fromLossDate = null;
			String strFromLossDate = (String) paramMap.get("fromLossDate");
			try {
				DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MMM-yyyy");
				DateTime dt = formatter.parseDateTime(strFromLossDate);
				dt = dt.withTimeAtStartOfDay();
				fromLossDate = new Timestamp(dt.getMillis());
				paramStr.append(fromLossDate);
				LOGGER.info("from Loss Date=" + fromLossDate);
			} catch (Exception ignore) {
				LOGGER.warn("Error in formating fromLossDate [" + strFromLossDate + "]");
			}

			paramMap.put("fromLossDate", fromLossDate);
		}
		paramMap.put("dispFromLossDate", paramMap.get("fromLossDate"));

		paramStr.append(", ");
		paramStr.append("toLossDate=");
		paramMap.put("dispToLossDate", null);
		if (paramMap.get("toLossDate") != null && paramMap.get("toLossDate") instanceof String) {
			Timestamp toLossDate = null;
			String strToLossDate = (String) paramMap.get("toLossDate");
			// 01-Jan-2016
			try {
				DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MMM-yyyy");
				DateTime dt = formatter.parseDateTime(strToLossDate);
				dt = dt.plusDays(1).withTimeAtStartOfDay().minusMillis(1);
				toLossDate = new Timestamp(dt.getMillis());
				paramStr.append(toLossDate);
				LOGGER.info("to Loss Date=" + toLossDate);
			} catch (Exception ignore) {
				LOGGER.warn("Error in formating toLossDate [" + strToLossDate + "]");
			}

			paramMap.put("toLossDate", toLossDate);
			paramMap.put("dispToLossDate", paramMap.get("toLossDate"));
		}

		return paramStr.toString();
	}
	
	@Override
	public void validateParams(BindingResult result) {
		// TODO Auto-generated method stub
	}

}
