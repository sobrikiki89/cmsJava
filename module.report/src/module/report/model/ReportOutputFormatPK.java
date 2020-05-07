package module.report.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class ReportOutputFormatPK implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long definitionId;

	private OutputFileFormat format;

	@Column(name = "DEFINITION_ID")
	public Long getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(Long definitionId) {
		this.definitionId = definitionId;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "FORMAT")
	public OutputFileFormat getFormat() {
		return format;
	}

	public void setFormat(OutputFileFormat format) {
		this.format = format;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ReportOutputFormatPK that = (ReportOutputFormatPK) o;

		if (definitionId != null ? !definitionId.equals(that.definitionId) : that.definitionId != null)
			return false;
		if (format != null ? !format.equals(that.format) : that.format != null)
			return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = (definitionId != null ? definitionId.hashCode() : 0);
		result = 31 * result + (format != null ? format.hashCode() : 0);
		return result;
	}
}
