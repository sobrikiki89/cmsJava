package module.claim.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import app.core.model.EntityHistory;

@Entity
@Table(name = "CLAIM_NOTIFICATION_EMAIL")
public class ClaimNotificationEmail extends EntityHistory {
	private static final long serialVersionUID = 1L;

	private Long id;
	private Claim claim;
	private String recipientInsurer;
	private String recipientSib;
	private String recipientCust;
	private String subject;
	private String content;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "entityKeys")
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(optional = true)
	@JoinColumn(name = "CLAIM_ID")
	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	@Column(name = "RECIPIENT_INSURER")
	public String getRecipientInsurer() {
		return recipientInsurer;
	}

	public void setRecipientInsurer(String recipientInsurer) {
		this.recipientInsurer = recipientInsurer;
	}

	@Column(name = "RECIPIENT_SIB")
	public String getRecipientSib() {
		return recipientSib;
	}

	public void setRecipientSib(String recipientSib) {
		this.recipientSib = recipientSib;
	}

	@Column(name = "RECIPIENT_CUST")
	public String getRecipientCust() {
		return recipientCust;
	}

	public void setRecipientCust(String recipientCust) {
		this.recipientCust = recipientCust;
	}

	@Column(name = "SUBJECT")
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Lob
	@Column(name = "CONTENT")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
