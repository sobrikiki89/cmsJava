package module.notification.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import app.core.model.EntityBase;

@Entity
@Table(name = "NOTIFICATION")
public class Notification extends EntityBase {
	private static final long serialVersionUID = 1L;

	private NotificationId pk;

	@EmbeddedId
	public NotificationId getPk() {
		return pk;
	}

	public void setPk(NotificationId pk) {
		this.pk = pk;
	}
}
