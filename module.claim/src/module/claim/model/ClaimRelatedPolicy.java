package module.claim.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import app.core.model.EntityHistory;

@Entity
@Table(name = "CLAIM_RELATED_POLICY")
public class ClaimRelatedPolicy extends EntityHistory{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Claim claim;
	private String policyNo;
	
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

	@Column(name = "POLICY_NO")
	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}	
	
}
