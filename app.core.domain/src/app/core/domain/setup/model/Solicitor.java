package app.core.domain.setup.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import app.core.model.EntityBase;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NonNull
@Entity
@Table(name = "SOLICITOR", uniqueConstraints= @UniqueConstraint(columnNames={"FIRM_NAME", "ACTIVE"}))
public class Solicitor extends EntityBase {

	private static final long serialVersionUID = 7077238697937977634L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "entityKeys")
	@Column(name = "ID", nullable = false, updatable = false)
	private Long id;

	@Column(name = "FIRM_NAME", nullable = false, length = 255)
	private String firmName;
	
	@OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
	@JoinColumn(name = "CONTACT_ID", nullable = true)
	private Contact contact;

	@Column(name = "ACTIVE", nullable = false)
	private Boolean activeFlag;
	
	@Column(name = "REMARK", nullable = true, length = 255)
	private String remark;
		
}