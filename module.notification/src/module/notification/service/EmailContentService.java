package module.notification.service;

import java.util.Date;
import java.util.List;

import module.notification.model.EmailType;
import module.notification.model.NotificationType;
import module.notification.object.EmailContentBO;

public interface EmailContentService {
	public List<EmailContentBO> getActiveContent(String trxCode, String trxType, Date effectiveDate,
			EmailType emailType);

	public List<EmailContentBO> getActiveContent(String trxCode, String trxType, EmailType emailType);

	public void createNotificationIfNotExists(String trxCode, String trxType, NotificationType notificationType);

	public void createEmailTemplate(EmailContentBO bo);

	public EmailContentBO getEmailTemplate(String trxCode, String trxType, NotificationType notificationType,
			EmailType emailType);

	public void initializeEmailContent();
}
