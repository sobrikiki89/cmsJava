package module.setup.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import app.core.model.EntityBase;
import app.core.usermgmt.model.User;

@Entity
@Table(name = "USER_COMPANY")
public class UserCompany extends EntityBase {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Company companyId;
	private User userId;
	private String dropDown;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "entityKeys")
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID")
	public Company getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Company companyId) {
		this.companyId = companyId;
	}

	@ManyToOne
	@JoinColumn(name = "USER_ID")
	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

	@Transient
	public String getDropDown() {
		dropDown = companyId.getCode();
		if (companyId != null) {
			dropDown = companyId.getCode() + " (" + companyId.getName() + ")";
		}
		return dropDown;
	}
	
}
