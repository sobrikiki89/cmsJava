package module.notification.listener.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import module.notification.listener.service.EmailSenderService;
import module.notification.object.Email;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailSenderServiceImpl.class);

	@Autowired
	@Qualifier("jmsMailSender")
	private JavaMailSender mailSender;

	@Override
	public void sendHtmlMail(Email email) {
		String[] toEmailList = emailListSanityCheck(email.getTo());
		Map<String, DataHandler> attachmentMap = attachmentSanityCheck(email.getAttachments());
		if (toEmailList != null && toEmailList.length > 0) {
			try {
				MimeMessage mime = mailSender.createMimeMessage();
				
				MimeMessageHelper mail = new MimeMessageHelper(mime, true, "UTF-8");
				mail.setFrom(email.getFrom());
				mail.setTo(toEmailList);
				mail.setSubject(email.getSubject());				
				mail.setText(email.getMessage(), true);
				for (Map.Entry<String, DataHandler> attachment : attachmentMap.entrySet()) {
					mail.addAttachment(attachment.getKey(), attachment.getValue().getDataSource());
				}
				mailSender.send(mime);
			} catch (MessagingException | MailException e) {
				LOGGER.error("Unable to create / send html email.", e);
			}
		} else {
			LOGGER.info("email will not send due to toEmailList is empty");
		}
	}

	@Override
	public void sendPlainMail(Email email) {
		String[] toEmailList = emailListSanityCheck(email.getTo());
		Map<String, DataHandler> attachmentMap = attachmentSanityCheck(email.getAttachments());
		if (toEmailList != null && toEmailList.length > 0) {
			try {
				SimpleMailMessage mail = new SimpleMailMessage();
				mail.setFrom(email.getFrom());
				mail.setTo(toEmailList);
				mail.setSubject(email.getSubject());
				mail.setText(email.getMessage());
				if (!attachmentMap.isEmpty()) {
					LOGGER.info("Skip adding attachment due to plain text email");
				}
				mailSender.send(mail);
			} catch (MailException e) {
				LOGGER.error("Unable to create / send plain email.", e);
			}
		} else {
			LOGGER.info("email will not send due to toEmailList is empty");
		}
	}

	private static String[] emailListSanityCheck(String[] incomingEmail) {
		List<String> toEmailList = new ArrayList<>(0);
		if (incomingEmail != null) {
			for (String toEmail : incomingEmail) {
				if (StringUtils.isBlank(toEmail)) {
					LOGGER.warn("[emailListSanityCheck] found empty/null string email-> {}.", toEmail);
				} else {
					LOGGER.info("[emailListSanityCheck] email -> {}", toEmail);
					toEmailList.add(toEmail);
				}
			}
		}
		return toEmailList.toArray(new String[0]);
	}

	private static Map<String, DataHandler> attachmentSanityCheck(Map<String, DataHandler> incomingAttachments) {
		Map<String, DataHandler> attachments = new LinkedHashMap<>();
		if (incomingAttachments != null) {
			for (Map.Entry<String, DataHandler> entry : incomingAttachments.entrySet()) {
				if (entry.getValue() != null) {
					try {
						// Ensure the input stream is able to be opened
						entry.getValue().getDataSource().getInputStream();
						attachments.put(entry.getKey(), entry.getValue());
						LOGGER.info("[attachmentSanityCheck] filename -> {}", entry.getKey());
					} catch (IOException e) {
						LOGGER.warn("[attachmentSanityCheck] attachment content cannot be read for filename-> {}.",
								entry.getKey(), e);
					}
				} else {
					LOGGER.warn("[attachmentSanityCheck] found empty attachment content for filename-> {}.",
							entry.getKey());
				}
			}
		}
		return attachments;
	}
}