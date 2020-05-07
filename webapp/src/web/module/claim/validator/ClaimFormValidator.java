package web.module.claim.validator;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import module.claim.model.Claim;
import module.claim.model.ClaimStatusEnum;
import module.claim.service.ClaimService;
import web.module.claim.model.ClaimSetupForm;

public class ClaimFormValidator implements Validator {

	private ClaimService claimService;

	public ClaimFormValidator(ClaimService claimService) {
		this.claimService = claimService;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return ClaimSetupForm.class.equals(arg0);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		// ValidationUtils.rejectIfEmptyOrWhitespace(errors, "claim.claimNo",
		// "claim.required.claimNo");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "claim.status", "claim.required.status");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "claim.notifyDate", "claim.required.notificationDate");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "claim.policy.id", "claim.required.policyNo");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "claim.lossDate", "claim.required.lossDate");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "claim.lossType.code", "claim.required.lossType");

		ClaimSetupForm claimSetupForm = ClaimSetupForm.class.cast(obj);
		Claim claim = claimSetupForm.getClaim();
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendDayOfMonth(2).appendLiteral("-")
				.appendMonthOfYearShortText().appendLiteral("-").appendYear(4, 4).toFormatter();
		DateTimeFormatter formatterWithTime = new DateTimeFormatterBuilder().appendDayOfMonth(2).appendLiteral("-")
				.appendMonthOfYearShortText().appendLiteral("-").appendYear(4, 4).appendLiteral(" ").appendHourOfDay(2)
				.appendLiteral(":").appendMinuteOfHour(2).toFormatter();

		LocalDate notifyDate = new LocalDate(claim.getNotifyDate());
		LocalDateTime lossDateTime = new LocalDateTime(claim.getLossDate());
		LocalDate lossDate = new LocalDate(claim.getLossDate());
		if (notifyDate.isBefore(lossDate)) {
			errors.reject("claim.notifyDateBeforeLossDate",
					new Object[] { notifyDate.toString(formatter), lossDateTime.toString(formatterWithTime) }, "error");
		}

		if (claim.getDocCompletionDate() != null) {
			LocalDate docCompletionDate = new LocalDate(claim.getDocCompletionDate());
			if (docCompletionDate.isBefore(notifyDate)) {
				errors.reject("claim.docCompletionDateBeforeNotifyDate",
						new Object[] { docCompletionDate.toString(formatter), notifyDate.toString(formatter) },
						"error");
			}
		}

		if (claim.getDocCompletionDate() != null && claim.getApprovalDate() != null) {
			LocalDate docCompletionDate = new LocalDate(claim.getDocCompletionDate());
			LocalDate offerDate = new LocalDate(claim.getApprovalDate());
			if (offerDate.isBefore(docCompletionDate)) {
				errors.reject("claim.offerDateBeforeDocCompletionDate",
						new Object[] { offerDate.toString(formatter), docCompletionDate.toString(formatter) }, "error");
			}
		}

		if (claim.getApprovalDate() != null && claim.getPaidDate() != null) {
			LocalDate approvalDate = new LocalDate(claim.getApprovalDate());
			LocalDate paidDate = new LocalDate(claim.getPaidDate());
			if (paidDate.isBefore(approvalDate)) {
				errors.reject("claim.paidDateBeforeApprovalDate",
						new Object[] { paidDate.toString(formatter), approvalDate.toString(formatter) }, "error");
			}
		}

		if (StringUtils.isNotEmpty(claim.getClaimNo())) {
			List<Claim> claimList = claimService.getClaimByClaimNo(claim.getClaimNo());
			if (claimList != null && !claimList.isEmpty()) {
				errors.reject("claim.claimNo_exists", new Object[] { claim.getClaimNo() }, "error");
			}
		}

		if (ClaimStatusEnum.CPAID.equals(claim.getStatus()) || ClaimStatusEnum.CDCL.equals(claim.getStatus())
				|| ClaimStatusEnum.CUEX.equals(claim.getStatus()) || ClaimStatusEnum.CWDTH.equals(claim.getStatus())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "claim.paidDate", "claim.required.closedDate");
		}
		
		if (ClaimStatusEnum.CPAID.equals(claim.getStatus())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "claim.approvalAmount", "error.required", new Object[] { "Paid Amount" });
		}

		// Enhancement Batch 3 : either claim reserve amount or paid amount is mandatory
		if (claim.getClaimReservedAmount() == null && claim.getApprovalAmount() == null) {
			errors.reject("error.required", new Object[] { "Paid Amount OR Claim Reserve Amount" }, "error");
		}
		
		if (claim.getSolicitorContact() != null && claim.getAdjusterContact() != null) {
			validateSolicitorAndAdjuster(claim, errors);
		}
	}

	public void validateEdit(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "claim.claimNo", "claim.required.claimNo");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "claim.status", "claim.required.status");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "claim.notifyDate", "claim.required.notificationDate");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "claim.policy.id", "claim.required.policyNo");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "claim.lossDate", "claim.required.lossDate");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "claim.lossType.code", "claim.required.lossType");

		ClaimSetupForm claimSetupForm = ClaimSetupForm.class.cast(obj);
		Claim claim = claimSetupForm.getClaim();
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendDayOfMonth(2).appendLiteral("-")
				.appendMonthOfYearShortText().appendLiteral("-").appendYear(4, 4).toFormatter();
		DateTimeFormatter formatterWithTime = new DateTimeFormatterBuilder().appendDayOfMonth(2).appendLiteral("-")
				.appendMonthOfYearShortText().appendLiteral("-").appendYear(4, 4).appendLiteral(" ").appendHourOfDay(2)
				.appendLiteral(":").appendMinuteOfHour(2).toFormatter();

		LocalDate notifyDate = new LocalDate(claim.getNotifyDate());
		LocalDateTime lossDateTime = new LocalDateTime(claim.getLossDate());
		LocalDate lossDate = new LocalDate(claim.getLossDate());
		if (notifyDate.isBefore(lossDate)) {
			errors.reject("claim.notifyDateBeforeLossDate",
					new Object[] { notifyDate.toString(formatter), lossDateTime.toString(formatterWithTime) }, "error");
		}

		if (claim.getDocCompletionDate() != null) {
			LocalDate docCompletionDate = new LocalDate(claim.getDocCompletionDate());
			if (docCompletionDate.isBefore(notifyDate)) {
				errors.reject("claim.docCompletionDateBeforeNotifyDate",
						new Object[] { docCompletionDate.toString(formatter), notifyDate.toString(formatter) },
						"error");
			}
		}

		if (claim.getDocCompletionDate() != null && claim.getApprovalDate() != null) {
			LocalDate docCompletionDate = new LocalDate(claim.getDocCompletionDate());
			LocalDate offerDate = new LocalDate(claim.getApprovalDate());
			if (offerDate.isBefore(docCompletionDate)) {
				errors.reject("claim.offerDateBeforeDocCompletionDate",
						new Object[] { offerDate.toString(formatter), docCompletionDate.toString(formatter) }, "error");
			}
		}

		if (claim.getApprovalDate() != null && claim.getPaidDate() != null) {
			LocalDate approvalDate = new LocalDate(claim.getApprovalDate());
			LocalDate paidDate = new LocalDate(claim.getPaidDate());
			if (paidDate.isBefore(approvalDate)) {
				errors.reject("claim.paidDateBeforeApprovalDate",
						new Object[] { paidDate.toString(formatter), approvalDate.toString(formatter) }, "error");
			}
		}

		if (StringUtils.isNotEmpty(claim.getClaimNo())) {
			List<Claim> claimList = claimService.getClaimByClaimNo(claim.getClaimNo());
			if (claimList != null && !claimList.isEmpty()) {
				for (Claim dbClaim : claimList) {
					if (dbClaim.getId().longValue() != claim.getId().longValue()) {
						errors.reject("claim.claimNo_exists", new Object[] { claim.getClaimNo() }, "error");
					}
				}
			}
		}

		if (ClaimStatusEnum.CPAID.equals(claim.getStatus()) || ClaimStatusEnum.CDCL.equals(claim.getStatus())
				|| ClaimStatusEnum.CUEX.equals(claim.getStatus()) || ClaimStatusEnum.CWDTH.equals(claim.getStatus())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "claim.paidDate", "claim.required.closedDate");
		}
		
		if (ClaimStatusEnum.CPAID.equals(claim.getStatus())) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "claim.approvalAmount", "error.required", new Object[] { "Paid Amount" });
		}

		// Enhancement Batch 3 : either claim reserve amount or paid amount is mandatory
		if (claim.getClaimReservedAmount() == null && claim.getApprovalAmount() == null) {
			errors.reject("error.required", new Object[] { "Paid Amount OR Claim Reserve Amount" }, "error");
		}
		
		if (claim.getSolicitorContact() != null || claim.getAdjusterContact() != null) {
			validateSolicitorAndAdjuster(claim, errors);
		}
	}
	
	private void validateSolicitorAndAdjuster(Claim claim, Errors errors) {
		if (!StringUtils.isEmpty(claim.getSolicitorContact().getContactPerson()) && !StringUtils.isEmpty(claim.getAdjusterContact().getContactPerson())) {
			errors.reject("error.required", new Object[] { "Paid Amount OR Claim Reserve Amount" }, "error");
		}
	}
}
