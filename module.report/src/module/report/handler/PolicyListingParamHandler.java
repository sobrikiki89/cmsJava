package module.report.handler;

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

import com.google.common.collect.Lists;

import app.core.security.UserPrincipal;
import module.setup.model.Company;
import module.setup.model.InsuranceClass;
import module.setup.model.InsuranceClassCategory;
import module.setup.model.Insurer;
import module.setup.service.CompanyService;
import module.setup.service.InsuranceClassService;
import module.setup.service.InsurerService;

public class PolicyListingParamHandler extends ReportParamHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PolicyListingParamHandler.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdfD = new SimpleDateFormat("dd-MM-yyyy");
	
	public PolicyListingParamHandler() {
		super();
		// Initialization
	}

	@Override
	public void setupLookup() {
		CompanyService companyService = context.getBean(CompanyService.class);
		lookupMap.put("companies", companyService.getAllCompanies());

		InsurerService insurerService = context.getBean(InsurerService.class);
		lookupMap.put("insurers", insurerService.getInsurers());

		InsuranceClassService insuranceClassService = context.getBean(InsuranceClassService.class);
		lookupMap.put("groupOfInsurance", insuranceClassService.getCategories());
		lookupMap.put("insuranceClasses", insuranceClassService.getInsuranceClasses());
	}

	@SuppressWarnings("unchecked")
	@Override
	public String formatParams() {
		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		CompanyService companyService = context.getBean(CompanyService.class);

		List<String> userCompanies = companyService.getUserCompaniesCode(principal);
		if (userCompanies != null && !userCompanies.isEmpty()) {
			paramMap.put("userCompany", userCompanies);
		} else {
			paramMap.put("userCompany", Lists.newArrayList("_NULL"));
		}

		if (paramMap.get("companyId") != null && !"".equals(paramMap.get("companyId"))) {
			Long companyId = Long.parseLong((String) paramMap.get("companyId"));
			paramMap.put("companyId", companyId);
			for (Company company : (List<Company>) lookupMap.get("companies")) {
				if (company.getId() == companyId) {
					paramMap.put("dispCompanyId", company.getName());
					break;
				}
			}
		} else {
			paramMap.put("companyId", null);
			paramMap.put("dispCompanyId", null);
		}

		paramMap.put("dispInsurerCode", null);
		if ("".equals(paramMap.get("insurerCode"))) {
			paramMap.put("insurerCode", null);
		} else {
			for (Insurer insurer : (List<Insurer>) lookupMap.get("insurers")) {
				if (insurer.getCode().equals(paramMap.get("insurerCode"))) {
					paramMap.put("dispInsurerCode", insurer.getName());
					break;
				}
			}
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
		
		/** Ida : if user want to be like the policy listing just uncomment this part
		 * add this to jasper query : and ($P{effectiveYear} is null or date_trunc('year', p.start_date)=to_date($P{effectiveYear}, 'yyyy')) 
		 * 				Parameter : effectiveYear String
		if (paramMap.get("effectiveYear") != null && !"".equals(paramMap.get("effectiveYear")) && paramMap.get("effectiveYear") instanceof String) {
			String effectiveYear = (String) paramMap.get("effectiveYear");
			paramMap.put("effectiveYear", effectiveYear);
		} else {
			paramMap.put("effectiveYear", null);
		}
		**/
		
		paramMap.put("dispFromDate", null);
		if (paramMap.get("fromDate") != null && !"".equals(paramMap.get("fromDate")) && paramMap.get("fromDate") instanceof String) {
			String strfromDate = (String) paramMap.get("fromDate");
			try {
				DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MMM-yyyy");
				DateTime dt = formatter.parseDateTime(strfromDate);
				String fromDate = sdf.format(dt.getMillis());
				String dispDate = sdfD.format(dt.getMillis());
				paramMap.put("fromDate", fromDate);
				paramMap.put("dispFromDate", dispDate);
			} catch (Exception ignore) {
				LOGGER.warn("Error in formating fromDate [" + strfromDate + "]");
			}
		} else {
			String fromDate = "2010-01-01";
			paramMap.put("fromDate", fromDate);
		}

		paramMap.put("dispToDate", null);
		if (paramMap.get("toDate") != null && !"".equals(paramMap.get("toDate")) && paramMap.get("toDate") instanceof String) {
			String strtoDate = (String) paramMap.get("toDate");
			try {
				DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MMM-yyyy");
				DateTime dt = formatter.parseDateTime(strtoDate);
				dt = dt.plusDays(1).withTimeAtStartOfDay().minusMillis(1);
				String toDate =  sdf.format(dt.getMillis());
				String dispDate = sdfD.format(dt.getMillis());
				paramMap.put("toDate", toDate);
				paramMap.put("dispToDate", dispDate);
			} catch (Exception ignore) {
			}
		} else {
			String toDate = "2030-12-31";
			paramMap.put("toDate", toDate);
		}

		StringBuilder param = new StringBuilder();
		param.append("companyId=").append(paramMap.get("companyId") == null ? "" : paramMap.get("companyId"));
		param.append(", ");
		param.append("insurerCode=").append(paramMap.get("insurerCode") == null ? "" : paramMap.get("insurerCode"));
		param.append(", ");
		param.append("insuranceClassCode=")
				.append(paramMap.get("insuranceClassCode") == null ? "" : paramMap.get("insuranceClassCode"));
		param.append(", ");
		param.append("effectiveYear=").append(paramMap.get("effectiveYear") == null ? "" : paramMap.get("effectiveYear"));
		return param.toString();
	}

	public void validateParams(BindingResult result) {

	}
}
