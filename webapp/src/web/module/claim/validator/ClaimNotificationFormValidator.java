package web.module.claim.validator;

import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import module.claim.service.ClaimService;
import module.upload.model.UploadedFile;
import web.module.claim.model.ClaimNotificationForm;

public class ClaimNotificationFormValidator implements Validator {

	private ClaimService claimService;

	public ClaimNotificationFormValidator(ClaimService claimService) {
		this.claimService = claimService;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return ClaimNotificationForm.class.equals(arg0);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ClaimNotificationForm claimNotificationForm = ClaimNotificationForm.class.cast(obj);
		if (!claimNotificationForm.isInsurerRecipientSelected() && !claimNotificationForm.isSibRecipientSelected()
				&& !claimNotificationForm.isUemRecipientSelected()) {
			errors.reject("claim.noRecipientTypeSelected");
		}

		if (claimNotificationForm.isSibRecipientSelected()
				&& (claimNotificationForm.getSibEmails() == null || claimNotificationForm.getSibEmails().length == 0)) {
			errors.reject("claim.noEmail.sibSelected");
		}

		if (claimNotificationForm.isUemRecipientSelected()
				&& (claimNotificationForm.getUemEmails() == null || claimNotificationForm.getUemEmails().length == 0)) {
			errors.reject("claim.noEmail.uemSelected");
		}

		// Validate the attachment is not exceeding the max total file size
		Long[] attachmentIds = claimNotificationForm.getSelectedAttachments();
		if (attachmentIds != null) {
			List<UploadedFile> files = claimService
					.getUploadedFile(claimNotificationForm.getNotificationEmail().getClaim());
			Long selectedFilesize = 0L;
			for (UploadedFile f : files) {
				for (Long attachmentId : attachmentIds) {
					if (attachmentId.equals(f.getId())) {
						selectedFilesize += f.getFileSize();
					}
				}
			}

			Long maxSize = claimService.getMaxAttachmentsFilesize();
			if (selectedFilesize > maxSize) {
				errors.reject("claim.exceed.totalFilesize", new Object[] { FileUtils.byteCountToDisplaySize(maxSize) },
						"error");
			}
		}
	}
}
