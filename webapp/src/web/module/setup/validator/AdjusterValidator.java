package web.module.setup.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import app.core.domain.setup.model.Adjuster;
import module.setup.service.AdjusterService;
import web.module.setup.model.AdjusterForm;


public class AdjusterValidator implements Validator {

	private AdjusterService adjusterService;

	public AdjusterValidator(AdjusterService adjusterService) {
		this.adjusterService = adjusterService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Adjuster.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
			AdjusterForm adjusterForm = AdjusterForm.class.cast(obj);

			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adjusterDTO.firmName", "error.required", new Object[]{"Firm Name"} ,"Field is required.");
			if (adjusterForm.getAdjusterDTO().getId() == null) {
				boolean duplicate = adjusterService.findAdjusterByNameAndActiveFlag(adjusterForm.getAdjusterDTO().getFirmName(), adjusterForm.getAdjusterDTO().getActiveFlag());
				if (!duplicate) {
					errors.reject("error.duplicate", new Object[]{"Active Adjuster", adjusterForm.getAdjusterDTO().getFirmName()}, null);
				}
			}
			
			if (adjusterForm.getContactDTO() != null) {
				if (!StringUtils.isEmpty(adjusterForm.getContactDTO().getEmail())
						&& !isEmailValid(adjusterForm.getContactDTO().getEmail())) {
					errors.reject("setup.contact.error.email.format");
				}
				if (!StringUtils.isEmpty(adjusterForm.getContactDTO().getTelNo())
						&& !isContactNoValid(adjusterForm.getContactDTO().getTelNo())) {
					errors.reject("setup.contact.error.telNo.format");
				}
				if (!StringUtils.isEmpty(adjusterForm.getContactDTO().getFaxNo())
						&& !isContactNoValid(adjusterForm.getContactDTO().getFaxNo())) {
					errors.reject("setup.contact.error.faxNo.format");
				}
			}
	}

	public void validateDelete(AdjusterForm adjusterForm, BindingResult errors) {
		String usedAssociation = adjusterService.checkAssociationById(adjusterForm.getSelected());
		if (StringUtils.isNotBlank(usedAssociation)) {
			errors.reject("error.inuse.delete", new Object[] { "Adjuster", usedAssociation }, "error");
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
