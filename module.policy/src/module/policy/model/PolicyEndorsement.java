package module.policy.model;

import java.math.BigDecimal;

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
@Table(name = "POLICY_ENDORSEMENT")
public class PolicyEndorsement extends EntityHistory {
	
	private static final long serialVersionUID = 1L;

	private Long id;

	private Policy policy;

	private String policyNo;

	private String endorsmentNo;

	private BigDecimal grossPremium;

	private BigDecimal rebatePremium;
	
	private BigDecimal taxAmount;

	private BigDecimal netPremium;

	private BigDecimal sumInsured;

	private BigDecimal stampDuty;

	private Integer order;

	private String description;
	
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
	@JoinColumn(name = "POLICY_ID")
	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}

	@Column(name = "POLICY_NO")
	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}
	
	@Column(name = "SORT_ORDER")
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
	
	@Column(name = "ENDORSEMENT_NO", precision = 12, scale = 2)
	public String getEndorsmentNo() {
		return endorsmentNo;
	}

	public void setEndorsmentNo(String endorsmentNo) {
		this.endorsmentNo = endorsmentNo;
	}
	
	@Column(name = "GROSS_PREMIUM", precision = 12, scale = 2)
	public BigDecimal getGrossPremium() {
		return grossPremium;
	}

	public void setGrossPremium(BigDecimal grossPremium) {
		this.grossPremium = grossPremium;
	}

	@Column(name = "REBATE_PREMIUM", precision = 12, scale = 2)
	public BigDecimal getRebatePremium() {
		return rebatePremium;
	}

	public void setRebatePremium(BigDecimal rebatePremium) {
		this.rebatePremium = rebatePremium;
	}

	@Column(name = "NET_PREMIUM", precision = 12, scale = 2)
	public BigDecimal getNetPremium() {
		return netPremium;
	}

	public void setNetPremium(BigDecimal netPremium) {
		this.netPremium = netPremium;
	}

	@Column(name = "TAX_AMOUNT", precision = 12, scale = 2)
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	@Column(name = "SUM_INSURED", precision = 12, scale = 2)
	public BigDecimal getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(BigDecimal sumInsured) {
		this.sumInsured = sumInsured;
	}

	@Column(name = "STAMP_DUTY", precision = 12, scale = 2)
	public BigDecimal getStampDuty() {
		return stampDuty;
	}

	public void setStampDuty(BigDecimal stampDuty) {
		this.stampDuty = stampDuty;
	}

	@Lob
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}