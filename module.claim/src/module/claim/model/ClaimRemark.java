package module.claim.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import app.core.model.EntityBase;
import app.core.utils.AppConstant;

@Entity
@Table(name = "CLAIM_REMARK")
public class ClaimRemark extends EntityBase {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Integer order;

	private Claim claim;

	private String remark;

	private String updateUser;

	private Date updateDate;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "entityKeys")
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "SORT_ORDER")
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@ManyToOne(optional = true)
	@JoinColumn(name = "CLAIM_ID")
	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	@Lob
	@Column(name = "REMARK")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(name = "UPDUSER", updatable = true, insertable = true)
	public String getUpdateUser() {
		return this.updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	@DateTimeFormat(pattern = AppConstant.DATE_TIME_FORMAT)
	@Column(name = "UPDDATE", updatable = true, insertable = true)
	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
}
