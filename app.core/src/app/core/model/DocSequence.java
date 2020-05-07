package app.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "DOC_SEQUENCE")
@SequenceGenerator(name = "SEQ_DOC_SEQUENCE", sequenceName = "SEQ_DOC_SEQUENCE", initialValue = 10000, allocationSize = 1)
public class DocSequence extends EntityBase {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String code;
	private Integer increaseNumber;
	private String format;
	private Integer year;
	private Integer month;
	private Integer day;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DOC_SEQUENCE")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "CODE")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "INCREASE_NUMBER")
	public Integer getIncreaseNumber() {
		return increaseNumber;
	}

	public void setIncreaseNumber(Integer increaseNumber) {
		this.increaseNumber = increaseNumber;
	}

	@Column(name = "FORMAT")
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	@Column(name = "YEAR")
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	@Column(name = "MONTH")
	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	@Column(name = "DAY")
	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}
}
