package web.module.setup.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import module.setup.service.SolicitorService;
import web.module.setup.model.SolicitorForm;


public class SolicitorValidator implements Validator {

	private SolicitorService solicitorService;

	public SolicitorValidator(SolicitorService solicitorService) {
		this.solicitorService = solicitorService;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return SolicitorForm.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		SolicitorForm solicitorForm = SolicitorForm.class.cast(obj);
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "solicitorDTO.firmName", "error.required", new Object[]{"Firm Name"} ,"Field is required.");
		if (solicitorForm.getSolicitorDTO().getId() == null) {
			boolean duplicate = solicitorService.findSolicitorByNameAndActiveFlag(solicitorForm.getSolicitorDTO().getFirmName(), solicitorForm.getSolicitorDTO().getActiveFlag());
			if (!duplicate) {
				errors.reject("error.duplicate", new Object[]{"Active Solicitor", solicitorForm.getSolicitorDTO().getFirmName()}, null);
			}
		}
		
		if (solicitorForm.getContactDTO() != null) {
			if (!StringUtils.isEmpty(solicitorForm.getContactDTO().getEmail())
					&& !isEmailValid(solicitorForm.getContactDTO().getEmail())) {
				errors.reject("setup.contact.error.email.format");
			}
			if (!StringUtils.isEmpty(solicitorForm.getContactDTO().getTelNo())
					&& !isContactNoValid(solicitorForm.getContactDTO().getTelNo())) {
				errors.reject("setup.contact.error.telNo.format");
			}
			if (!StringUtils.isEmpty(solicitorForm.getContactDTO().getFaxNo())
					&& !isContactNoValid(solicitorForm.getContactDTO().getFaxNo())) {
				errors.reject("setup.contact.error.faxNo.format");
			}
		}
	}

	public void validateDelete(SolicitorForm solicitorForm, BindingResult errors) {
		String usedAssociation = solicitorService.checkAssociationById(solicitorForm.getSelected());
		if (StringUtils.isNotBlank(usedAssociation)) {
			errors.reject("error.inuse.delete", new Object[] { "Solicitor", usedAssociation }, "error");
		}
	}
	
	private static boolean isEmailValid(String email) {
      String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w-_]+\\.)+[\\w]+[\\w]$";
      return email.matches(regex);
    }

	private boolean isContactNoValid(String contactNo) {
	      String regex = "^[0-9\\s-]+$";
	      return contactNo.matches(regex);
	}

}
