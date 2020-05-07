package module.notification.listener.service;

import module.notification.object.Email;

public interface EmailSenderService {
	public void sendHtmlMail(Email email);

	public void sendPlainMail(Email email);
}
