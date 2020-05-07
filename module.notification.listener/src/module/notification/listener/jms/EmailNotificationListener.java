package module.notification.listener.jms;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import org.apache.camel.Exchange;
import org.apache.camel.component.jms.JmsMessage;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import app.core.service.XMLDocumentService;
import module.notification.constant.JMSMessageConstant;
import module.notification.constant.NotificationConstant;
import module.notification.listener.converter.NotificationConverter;
import module.notification.listener.service.EmailSenderService;
import module.notification.model.EmailType;
import module.notification.model.NotificationType;
import module.notification.object.Email;
import module.notification.object.EmailContentBO;
import module.notification.object.NotificationTransaction;
import module.notification.service.EmailContentService;

@Component
@Qualifier("emailNotificationListener")
public class EmailNotificationListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotificationListener.class);

	@Autowired
	private ApplicationContext context;

	@Autowired
	private EmailContentService emailContentService;

	@Autowired
	private EmailSenderService senderService;

	@Autowired
	private XMLDocumentService xmlDocumentService;

	private String senderEmail;

	@Autowired
	public EmailNotificationListener(@Value("${email.sender.email}") String senderEmail) {
		super();
		this.senderEmail = checkNotNull(senderEmail, "senderEmail is required.");
		LOGGER.info("###################### Sender Email [" + senderEmail + "]");
	}

	@SuppressWarnings("unchecked")
	public void listen(Exchange exchange) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Message receive start");
		}

		Map<String, Object> header = exchange.getIn().getHeaders();

		// we only interest on notification event
		if (JMSMessageConstant.EVENT_TYPE_NOTIFICATION.equals(header.get(JMSMessageConstant.HEADER_EVENT_TYPE))) {
			String contentType = (String) header.get(JMSMessageConstant.HEADER_CONTENT_TYPE);

			if (validContentType(contentType) && validContent(exchange.getIn())) {
				// get message body and send email
				Map<String, Object> bodyMap = (Map<String, Object>) exchange.getIn().getBody();

				String msg = (String) bodyMap.remove(JMSMessageConstant.INTERNAL_XML);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Message content: {}", msg);
				}
				Document xml = xmlDocumentService.toXML(msg);

				String notificationType = xmlDocumentService.getField(xml,
						JMSMessageConstant.MESSAGE_HEADER_NOTIFICATION_TYPE);
				if (!NotificationType.EMAIL.toString().equals(notificationType)) {
					LOGGER.warn("Skip to process message due to it is not EMAIL notification, content: {}", msg);
					return;
				}

				String transactionCode = String.valueOf(header.get(JMSMessageConstant.HEADER_TRASACTION_CODE));
				String transactionType = String.valueOf(header.get(JMSMessageConstant.HEADER_TRASACTION_TYPE));
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Transaction Code: {}", transactionCode);
					LOGGER.debug("Transaction Type: {}", transactionType);
				}
				NotificationTransaction nTransaction = NotificationTransaction.get(transactionCode, transactionType);

				String eventDateTime = String.valueOf(header.get(JMSMessageConstant.HEADER_EVENT_DATETME));
				LOGGER.info("Resolving message for Notification Transaction [" + nTransaction + "], Event Date Time ["
						+ eventDateTime + "]");

				Date effectiveDate = null;
				try {
					effectiveDate = LocalDateTime
							.parse(eventDateTime, DateTimeFormat.forPattern(JMSMessageConstant.DATE_TIME_FORMAT))
							.toDate();
				} catch (Exception e) {
					LOGGER.warn("Error in parsing event date time", e);
				}

				List<Email> emails = resolveMessage(nTransaction, xml, effectiveDate);

				try {
					if (bodyMap.size() > 0) {
						Map<String, DataHandler> attachmentDataHanlder = new HashMap<>();
						for (Map.Entry<String, Object> entry : bodyMap.entrySet()) {
							if (entry.getValue() instanceof byte[]) {
								byte[] attachment = (byte[]) entry.getValue();
								attachmentDataHanlder.put(entry.getKey(), new DataHandler(
										new ByteArrayDataSource(attachment, "application/octet-stream")));

								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug("Attachment found filename : {}", entry.getKey());
								}
							}
						}

						for (Email email : emails) {
							email.setAttachments(attachmentDataHanlder);
						}
					}
				} catch (Exception e) {
					LOGGER.error("Error in getting attachment from JMS", e);
				}
				sendMail(emails, resolveSender(xml));
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Message receive end");
		}
	}

	protected void sendMail(List<Email> emails, NotificationConstant.Mailer sender) {
		for (Email mail : emails) {
			if (mail.getTo() != null && StringUtils.isNotBlank(mail.getMessage())) {
				LOGGER.info("Sending email to {}", Arrays.asList(mail.getTo()));
				LOGGER.info("subject {}", mail.getSubject());
				LOGGER.info("message {}", mail.getMessage());
				LOGGER.info("with attachment {}", (mail.getAttachments() != null && !mail.getAttachments().isEmpty()));
				switch (sender) {
				case PLAIN_TEXT:
					senderService.sendPlainMail(mail);
					break;
				case HTML:
					senderService.sendHtmlMail(mail);
					break;
				}
			}
		}
	}

	private static boolean validContentType(String contypeType) {
		return JMSMessageConstant.CONTENT_TYPE_XML.equals(contypeType);
	}

	@SuppressWarnings("unchecked")
	private static boolean validContent(Object body) {
		if (body == null || !(body instanceof JmsMessage) || !(((JmsMessage) body).getBody() instanceof Map)) {
			return false;
		}

		Map<String, Object> bodyMap = (Map<String, Object>) ((JmsMessage) body).getBody();
		if (StringUtils.isBlank((String) bodyMap.get(JMSMessageConstant.INTERNAL_XML))) {
			LOGGER.error("No any xml content found");
			return false;
		}

		return true;
	}

	protected String[] resolveAlertAddress(Document xml) {
		String recepient = xmlDocumentService.getField(xml, NotificationConstant.EMAIL_ALERT_RECEPIENT);
		if (!StringUtils.isBlank(recepient)) {
			return recepient.split(NotificationConstant.DELIMITER);
		}
		return new String[] {};
	}

	protected String[] resolveNotificationAddress(Document xml) {
		String recepient = xmlDocumentService.getField(xml, NotificationConstant.EMAIL_RECEPIENT);
		if (!StringUtils.isBlank(recepient)) {
			return recepient.split(NotificationConstant.DELIMITER);
		}
		return new String[] {};
	}

	protected NotificationConstant.Mailer resolveSender(Document xml) {
		String sender = xmlDocumentService.getField(xml, NotificationConstant.EMAIL_MAILER);
		if (!StringUtils.isBlank(sender)) {
			try {
				return NotificationConstant.Mailer.valueOf(NotificationConstant.Mailer.class, sender);
			} catch (IllegalArgumentException e) {
				LOGGER.warn("Unable to resolve email sender, {}", sender, e);
			}
		}
		LOGGER.warn("Invalid email sender default HTML");
		return NotificationConstant.Mailer.HTML;
	}

	protected List<Email> resolveMessage(NotificationTransaction nTransaction, Document xml, Date effectiveDate) {
		if (nTransaction == null) {
			return new ArrayList<>();
		}

		String alertRecepient = xmlDocumentService.getField(xml, NotificationConstant.EMAIL_ALERT_RECEPIENT);
		if (StringUtils.isNotBlank(alertRecepient)) {
			LOGGER.info("Alert recepient found [" + alertRecepient + "]");
		}

		List<EmailContentBO> result = null;
		if (effectiveDate != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Getting active content with effective date [" + effectiveDate + "]");
			}
			result = emailContentService.getActiveContent(nTransaction.getCode(), nTransaction.getType(), effectiveDate,
					StringUtils.isNotBlank(alertRecepient) ? null : EmailType.NOTIFICATION);
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Getting active content with effective date = today date");
			}
			result = emailContentService.getActiveContent(nTransaction.getCode(), nTransaction.getType(),
					StringUtils.isNotBlank(alertRecepient) ? null : EmailType.NOTIFICATION);
		}

		List<Email> mails = new ArrayList<>();
		if (result != null && !result.isEmpty()) {
			NotificationConverter converter = context.getBean(nTransaction.name() + "Converter",
					NotificationConverter.class);

			Email email = new Email();
			for (EmailContentBO emailBO : result) {
				email.setFrom(senderEmail);
				email.setSubject(emailBO.getSubject());
				email.setMessage(emailBO.getExistingContent());

				if (EmailType.ALERT.getCode().equals(emailBO.getEmailType())) {
					email.setTo(resolveAlertAddress(xml));
					email = converter.convertAlertEmail(email, xml);
				} else if (EmailType.NOTIFICATION.getCode().equals(emailBO.getEmailType())) {
					email.setTo(resolveNotificationAddress(xml));
					email = converter.convertNotificationEmail(email, xml);
				}

				if (email != null && email.getTo() != null) {
					mails.add(email);
				}
			}

			return mails;
		} else {
			return new ArrayList<>();
		}
	}
}
