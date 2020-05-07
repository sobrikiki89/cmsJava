package module.report.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import app.core.model.EntityBase;

@Entity
@Table(name = "REPORT_OUTPUT_FORMAT")
public class ReportOutputFormat extends EntityBase {

	private static final long serialVersionUID = 1L;

	private ReportOutputFormatPK id;

	private String reportClass;

	private ReportDefinition definition;

	@EmbeddedId
	public ReportOutputFormatPK getId() {
		return id;
	}

	public void setId(ReportOutputFormatPK id) {
		this.id = id;
	}

	@Column(name = "REPORT_CLASS")
	public String getReportClass() {
		return reportClass;
	}

	public void setReportClass(String reportClass) {
		this.reportClass = reportClass;
	}

	@ManyToOne
	@JoinColumn(name = "DEFINITION_ID", insertable = false, updatable = false)
	public ReportDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(ReportDefinition definition) {
		this.definition = definition;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ReportOutputFormat that = (ReportOutputFormat) o;

		if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null)
			return false;

		return true;
	}

	public int hashCode() {
		return (getId() != null ? getId().hashCode() : 0);
	}
}
