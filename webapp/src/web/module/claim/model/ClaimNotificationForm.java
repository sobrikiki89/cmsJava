package web.module.claim.model;

import java.util.List;

import app.core.usermgmt.model.User;
import module.claim.model.ClaimNotificationEmail;
import module.upload.model.UploadedFile;
import web.core.model.AbstractForm;

public class ClaimNotificationForm extends AbstractForm {

	private static final long serialVersionUID = 1L;

	private ClaimNotificationEmail notificationEmail;
	private List<String> emailRecipientSib;
	private List<String> emailRecipientUem;
	private List<User> userSIB;
	private List<UploadedFile> attachments;

	private String insurerEmail;
	private String[] sibEmails;
	private String[] uemEmails;
	private Long[] selectedAttachments;

	private boolean insurerRecipientSelected;
	private boolean sibRecipientSelected;
	private boolean uemRecipientSelected;

	public ClaimNotificationEmail getNotificationEmail() {
		return notificationEmail;
	}

	public void setNotificationEmail(ClaimNotificationEmail notificationEmail) {
		this.notificationEmail = notificationEmail;
	}

	public List<String> getEmailRecipientSib() {
		return emailRecipientSib;
	}

	public void setEmailRecipientSib(List<String> emailRecipientSib) {
		this.emailRecipientSib = emailRecipientSib;
	}

	public List<String> getEmailRecipientUem() {
		return emailRecipientUem;
	}

	public void setEmailRecipientUem(List<String> emailRecipientUem) {
		this.emailRecipientUem = emailRecipientUem;
	}

	public List<UploadedFile> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<UploadedFile> attachments) {
		this.attachments = attachments;
	}

	public String getInsurerEmail() {
		return insurerEmail;
	}

	public void setInsurerEmail(String insurerEmail) {
		this.insurerEmail = insurerEmail;
	}

	public String[] getSibEmails() {
		return sibEmails;
	}

	public void setSibEmails(String[] sibEmails) {
		this.sibEmails = sibEmails;
	}

	public String[] getUemEmails() {
		return uemEmails;
	}

	public void setUemEmails(String[] uemEmails) {
		this.uemEmails = uemEmails;
	}

	public Long[] getSelectedAttachments() {
		return selectedAttachments;
	}

	public void setSelectedAttachments(Long[] selectedAttachments) {
		this.selectedAttachments = selectedAttachments;
	}

	public boolean isInsurerRecipientSelected() {
		return insurerRecipientSelected;
	}

	public void setInsurerRecipientSelected(boolean insurerRecipientSelected) {
		this.insurerRecipientSelected = insurerRecipientSelected;
	}

	public boolean isSibRecipientSelected() {
		return sibRecipientSelected;
	}

	public void setSibRecipientSelected(boolean sibRecipientSelected) {
		this.sibRecipientSelected = sibRecipientSelected;
	}

	public boolean isUemRecipientSelected() {
		return uemRecipientSelected;
	}

	public void setUemRecipientSelected(boolean uemRecipientSelected) {
		this.uemRecipientSelected = uemRecipientSelected;
	}

	public List<User> getUserSIB() {
		return userSIB;
	}

	public void setUserSIB(List<User> userSIB) {
		this.userSIB = userSIB;
	}

}
