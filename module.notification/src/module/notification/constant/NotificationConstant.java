package module.notification.constant;

public class NotificationConstant {

	public static final String ENDPOINT_EMAIL = "direct:sendEmail";
			
	public static final String PROP_NOTIFICATION_EMAIL_SUBJECT = "subject";

	public static final String PROP_NOTIFICATION_EMAIL_CONTENT = "content";

	public static final String PROP_NOTIFICATION_EMAIL_EFFECTIVE_DATE = "effectiveDate";

	public static final String PROP_NOTIFICATION_EMAIL_EFFECTIVE_DATE_FORMAT = "dd/MM/yyyy";

	public static final String DELIMITER = ",|;";

	public static final String EMAIL_RECEPIENT = "emailRecepient";	

	public static final String EMAIL_ALERT_RECEPIENT = "emailAlertRecepient";

	public static final String EMAIL_SUBJECT = "emailSubject";

	public static final String EMAIL_BODY = "emailBody";

	public static final String EMAIL_TYPE = "emailType";

	public static final String EMAIL_MAILER = "emailMailer";

	public enum Mailer {
		HTML, PLAIN_TEXT
	}

	private NotificationConstant() {
	}
}
