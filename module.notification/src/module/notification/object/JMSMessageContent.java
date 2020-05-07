package module.notification.object;

import java.sql.Timestamp;
import java.util.Map;

import module.notification.model.NotificationType;

public class JMSMessageContent {

	private String userId;

	private Map<String, Object> messageBody;

	private Timestamp eventTime;

	private String srcId;

	private String transactionStatus;

	private NotificationType notificationType;

	private NotificationTransaction transaction;

	public JMSMessageContent(String userId) {
		this.userId = userId;
		this.eventTime = new Timestamp(System.currentTimeMillis());
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Map<String, Object> getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(Map<String, Object> messageBody) {
		this.messageBody = messageBody;
	}

	public Timestamp getEventTime() {
		return eventTime;
	}

	public void setEventTime(Timestamp eventTime) {
		this.eventTime = eventTime;
	}

	public String getSrcId() {
		return srcId;
	}

	public void setSrcId(String srcId) {
		this.srcId = srcId;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public NotificationTransaction getTransaction() {
		return transaction;
	}

	public void setTransaction(NotificationTransaction transaction) {
		this.transaction = transaction;
	}

	@Override
	public String toString() {
		return "JMSMessageContent{" + ", userId=" + userId + ", messageBody=" + messageBody + ", eventTime=" + eventTime
				+ ", srcId=" + srcId + ", transaction=" + transaction + ", transactionStatus=" + transactionStatus
				+ ", notification type=" + notificationType + "}";
	}
}