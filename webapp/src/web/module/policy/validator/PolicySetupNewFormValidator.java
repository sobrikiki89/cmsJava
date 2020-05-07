package web.module.policy.validator;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import module.policy.model.Policy;
import module.policy.model.PolicyEndorsement;
import module.policy.service.PolicyService;
import web.module.policy.model.PolicySetupForm;

public class PolicySetupNewFormValidator implements Validator {

	private PolicyService policyService;

	public PolicySetupNewFormValidator(PolicyService policyService) {
		this.policyService = policyService;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return PolicySetupForm.class.equals(arg0);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "policy.policyNo", "policymgmt.setup.required.policyNo");
		ValidationUtils.rejectIfEmpty(errors, "policy.insurer.code", "policymgmt.setup.required.insurerName");
		ValidationUtils.rejectIfEmpty(errors, "policy.insuranceClass.code",
				"policymgmt.setup.required.insuranceClassCode");
		ValidationUtils.rejectIfEmpty(errors, "policy.insuredContent", "policymgmt.setup.required.insuredContent");
		ValidationUtils.rejectIfEmpty(errors, "policy.startDate", "policymgmt.setup.required.startDate");
		ValidationUtils.rejectIfEmpty(errors, "policy.company.id", "error.required", new Object[] { "Company"});
	

		PolicySetupForm policySetupForm = PolicySetupForm.class.cast(obj);
		if (policySetupForm.getPolicy().getEndDate() != null) {
			LocalDate startDate = new LocalDate(policySetupForm.getPolicy().getStartDate());
			LocalDate endDate = new LocalDate(policySetupForm.getPolicy().getEndDate());
			if (endDate.isBefore(startDate)) {
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendDayOfMonth(2).appendLiteral("-")
						.appendMonthOfYearShortText().appendLiteral("-").appendYear(4, 4).toFormatter();
				errors.reject("policymgmt.setup.endDateBeforeStartDate",
						new Object[] { endDate.toString(formatter), startDate.toString(formatter) }, "error");
			}
		}

		String policyNo = policySetupForm.getPolicy().getPolicyNo();
		if (StringUtils.isNotEmpty(policyNo)) {
			List<Policy> policyList = policyService.getPolicyByPolicyNo(policyNo);
			if (policyList != null && !policyList.isEmpty()) {
				errors.reject("policymgmt.setup.policyNo_exists", new Object[] { policyNo }, "error");
			}
		}
		
		if (policySetupForm.getPolicy().getEndorsementList() != null) {
			for (PolicyEndorsement item : policySetupForm.getPolicy().getEndorsementList()) {
				if (StringUtils.isBlank(item.getEndorsmentNo())) {
					errors.reject("error.required.for", new Object[] { "Endorsement No" , ("No. " + item.getOrder()) }, "error");
				}
			}
		}
	}
	
	public void validateEdit(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "policy.policyNo", "policymgmt.setup.required.policyNo");
		ValidationUtils.rejectIfEmpty(errors, "policy.insurer.code", "policymgmt.setup.required.insurerName");
		ValidationUtils.rejectIfEmpty(errors, "policy.insuranceClass.code",
				"policymgmt.setup.required.insuranceClassCode");
		ValidationUtils.rejectIfEmpty(errors, "policy.insuredContent", "policymgmt.setup.required.insuredContent");
		ValidationUtils.rejectIfEmpty(errors, "policy.startDate", "policymgmt.setup.required.startDate");

		PolicySetupForm policySetupForm = PolicySetupForm.class.cast(obj);
		if (policySetupForm.getPolicy().getEndDate() != null) {
			LocalDate startDate = new LocalDate(policySetupForm.getPolicy().getStartDate());
			LocalDate endDate = new LocalDate(policySetupForm.getPolicy().getEndDate());
			if (endDate.isBefore(startDate)) {
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendDayOfMonth(2).appendLiteral("-")
						.appendMonthOfYearShortText().appendLiteral("-").appendYear(4, 4).toFormatter();
				errors.reject("policymgmt.setup.endDateBeforeStartDate",
						new Object[] { endDate.toString(formatter), startDate.toString(formatter) }, "error");
			}
		}

		if (policySetupForm.getPolicy().getCompany().getId() == null) {
			errors.reject("error.required", new Object[] { "Company" }, "error");
		}

		String policyNo = policySetupForm.getPolicy().getPolicyNo();
		if (StringUtils.isNotEmpty(policyNo)) {
			List<Policy> policyList = policyService.getPolicyByPolicyNo(policyNo);
			if (policyList != null && !policyList.isEmpty()) {
				for (Policy policy : policyList) {
					if (!policy.getId().equals(policySetupForm.getPolicy().getId())) {
						errors.reject("policymgmt.setup.policyNo_exists", new Object[] { policyNo }, "error");
					}
				}
			}
		}
		
		if (policySetupForm.getPolicy().getEndorsementList() != null) {
			for (PolicyEndorsement item : policySetupForm.getPolicy().getEndorsementList()) {
				if (StringUtils.isBlank(item.getEndorsmentNo())) {
					errors.reject("error.required.for", new Object[] { "Endorsement No" , ("No. " + item.getOrder()) }, "error");
				}
			}
		}	
	}
}
