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
@Table(name = "POLICY_EXCESS_DEDUCTIBLE")
public class PolicyExcessDeductible extends EntityHistory {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Integer order;
	
	private String description;

	private BigDecimal amount;

	private Policy policy;

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

	@Lob
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "AMOUNT")
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@ManyToOne(optional = true)
	@JoinColumn(name = "POLICY_ID")
	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}
}
