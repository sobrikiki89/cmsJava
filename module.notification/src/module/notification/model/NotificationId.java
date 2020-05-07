package module.notification.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class NotificationId implements Serializable {

	private static final long serialVersionUID = 1L;

	private String trxCode;

	private String trxType;

	private NotificationType notificationType;

	@Column(name = "TRX_CODE", nullable = false, length = 10)
	public String getTrxCode() {
		return trxCode;
	}

	public void setTrxCode(String trxCode) {
		this.trxCode = trxCode;
	}

	@Column(name = "TRX_TYPE", nullable = false, length = 100)
	public String getTrxType() {
		return trxType;
	}

	public void setTrxType(String trxType) {
		this.trxType = trxType;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "NOTIFICATION_TYPE", nullable = false, length = 10)
	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}
}
