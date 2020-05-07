package module.notification.listener.converter;

import org.w3c.dom.Document;

import module.notification.object.Email;

public interface NotificationConverter {
	public Email convertAlertEmail(Email mail, Document xml);

	public Email convertNotificationEmail(Email mail, Document xml);
}