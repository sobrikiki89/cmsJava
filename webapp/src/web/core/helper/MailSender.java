package web.core.helper;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;

public class MailSender {
	private JavaMailSenderImpl mailSender;
	private String from;
	
	protected static Logger logger = LoggerFactory.getLogger("MailSender");
	
	public MailSender() {
	}
	
	public MailSender(JavaMailSenderImpl mailSender, String from) {
		this.mailSender = mailSender;
		this.from = from;
	}

	public MimeMessage createMimeMessage() {
		return mailSender.createMimeMessage();
	}
	
	@Async
	public void send(MimeMessage message) {
		try {
			mailSender.send(message);
		}
		
		catch (MailException e) {
			logger.error(e.getMessage());
		}
		
		logger.info("Asynchronous method call of send email - Complete");
	}

	public String getFrom() {
		return from;
	}
}
