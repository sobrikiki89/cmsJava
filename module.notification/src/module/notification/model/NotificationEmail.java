package module.notification.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import app.core.model.EntityHistory;

@Entity
@Table(name = "NOTIFICATION_EMAIL")
@SequenceGenerator(name = "SEQ_NOTIFICATION_EMAIL", sequenceName = "SEQ_NOTIFICATION_EMAIL", initialValue = 10000, allocationSize = 1)
public class NotificationEmail extends EntityHistory {

	private static final long serialVersionUID = 1L;
	private Long id;

	private String emailType;

	private String subject;

	private String content;

	private String status;

	private Date effectiveDate;

	private Notification notification;

	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_NOTIFICATION_EMAIL")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "EMAIL_TYPE", nullable = false, length = 20)
	public String getEmailType() {
		return emailType;
	}

	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}

	@Column(name = "SUBJECT", nullable = false, length = 500)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Lob
	@Column(name = "CONTENT", nullable = false)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "STATUS", nullable = false, length = 10)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "EFFECTIVE_DATE", nullable = false)
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "TRX_CODE", referencedColumnName = "TRX_CODE"),
			@JoinColumn(name = "TRX_TYPE", referencedColumnName = "TRX_TYPE"),
			@JoinColumn(name = "NOTIFICATION_TYPE", referencedColumnName = "NOTIFICATION_TYPE") })
	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
}
