package module.notification.service;

import javax.servlet.http.HttpServletRequest;

import module.notification.object.JMSMessage;
import module.notification.object.JMSMessageContent;

public interface JMSMessageService {

	public JMSMessage createMessage(HttpServletRequest request, JMSMessageContent jmsMessageContent);

	public void send(String uri, final JMSMessage message);
}