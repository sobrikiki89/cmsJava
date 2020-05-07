package module.notification.object;

import java.io.Serializable;
import java.util.Map;

import javax.activation.DataHandler;

public class Email implements Serializable {
	private static final long serialVersionUID = 1L;

	private String from;

	private String[] to;

	private String subject;

	private String message;

	private Map<String, DataHandler> attachments;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String[] getTo() {
		return to;
	}

	public void setTo(String[] to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, DataHandler> getAttachments() {
		return attachments;
	}

	public void setAttachments(Map<String, DataHandler> attachments) {
		this.attachments = attachments;
	}
}
