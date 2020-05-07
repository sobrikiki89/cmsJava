package module.policy.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import app.core.model.EntityHistory;
import module.setup.model.Company;
import module.setup.model.InsuranceClass;
import module.setup.model.Insurer;

@Entity
@Table(name = "POLICY")
public class Policy extends EntityHistory {
	private static final long serialVersionUID = 1L;

	private Long id;

	private String policyNo;

	private Insurer insurer;

	private InsuranceClass insuranceClass;

	private Company company;

	private String insuredContent;

	private String riskLocation;

	private Date startDate;

	private Date endDate;

	private Integer maintPeriod;

	private MaintPeriodUnit maintPeriodUnit;

	private String broker;

	private String remark;

	private List<PolicyInterestInsured> interestInsuredList;

	private List<PolicyExcessDeductible> excessDeductibleList;
	
	private List<PolicyEndorsement> endorsementList;
	
	// batch 3 enhancement
	private BigDecimal premiumGross;

	private BigDecimal premiumRebate;

	private BigDecimal premiumNet;
	
	private BigDecimal premiumTax;

	private BigDecimal sumInsured;
	
	private BigDecimal stampDuty;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "entityKeys")
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "POLICY_NO")
	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	@ManyToOne(optional = true)
	@JoinColumn(name = "INSURER_CODE")
	public Insurer getInsurer() {
		return insurer;
	}

	public void setInsurer(Insurer insurer) {
		this.insurer = insurer;
	}

	@ManyToOne(optional = true, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "INSURANCE_CLASS_CODE")
	public InsuranceClass getInsuranceClass() {
		return insuranceClass;
	}

	public void setInsuranceClass(InsuranceClass insuranceClass) {
		this.insuranceClass = insuranceClass;
	}

	@ManyToOne(optional = true)
	@JoinColumn(name = "COMPANY_ID")
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Lob
	@Column(name = "INSURED_CONTENT")
	public String getInsuredContent() {
		return insuredContent;
	}

	public void setInsuredContent(String insuredContent) {
		this.insuredContent = insuredContent;
	}

	@Column(name = "RISK_LOCATION")
	public String getRiskLocation() {
		return riskLocation;
	}

	public void setRiskLocation(String riskLocation) {
		this.riskLocation = riskLocation;
	}

	@Column(name = "START_DATE")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name = "END_DATE")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name = "MAINT_PERIOD")
	public Integer getMaintPeriod() {
		return maintPeriod;
	}

	public void setMaintPeriod(Integer maintPeriod) {
		this.maintPeriod = maintPeriod;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "MAINT_PERIOD_UNIT")
	public MaintPeriodUnit getMaintPeriodUnit() {
		return maintPeriodUnit;
	}

	public void setMaintPeriodUnit(MaintPeriodUnit maintPeriodUnit) {
		this.maintPeriodUnit = maintPeriodUnit;
	}

	@Column(name = "BROKER")
	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	@Lob
	@Column(name = "REMARK")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "POLICY_ID")
	public List<PolicyInterestInsured> getInterestInsuredList() {
		return interestInsuredList;
	}

	public void setInterestInsuredList(List<PolicyInterestInsured> interestInsuredList) {
		this.interestInsuredList = interestInsuredList;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "POLICY_ID")
	public List<PolicyExcessDeductible> getExcessDeductibleList() {
		return excessDeductibleList;
	}

	public void setExcessDeductibleList(List<PolicyExcessDeductible> excessDeductibleList) {
		this.excessDeductibleList = excessDeductibleList;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "POLICY_ID")
	public List<PolicyEndorsement> getEndorsementList() {
		return endorsementList;
	}

	public void setEndorsementList(List<PolicyEndorsement> endorsementList) {
		this.endorsementList = endorsementList;
	}

	@Column(name = "PREMIUM_GROSS", precision = 12, scale = 2)
	public BigDecimal getPremiumGross() {
		return premiumGross;
	}

	public void setPremiumGross(BigDecimal premiumGross) {
		this.premiumGross = premiumGross;
	}

	@Column(name = "PREMIUM_REBATE", precision = 12, scale = 2)
	public BigDecimal getPremiumRebate() {
		return premiumRebate;
	}

	public void setPremiumRebate(BigDecimal premiumRebate) {
		this.premiumRebate = premiumRebate;
	}

	@Column(name = "PREMIUM_NET", precision = 12, scale = 2)
	public BigDecimal getPremiumNet() {
		return premiumNet;
	}

	public void setPremiumNet(BigDecimal premiumNet) {
		this.premiumNet = premiumNet;
	}

	@Column(name = "PREMIUM_TAX", precision = 12, scale = 2)
	public BigDecimal getPremiumTax() {
		return premiumTax;
	}

	public void setPremiumTax(BigDecimal premiumTax) {
		this.premiumTax = premiumTax;
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
	
}
