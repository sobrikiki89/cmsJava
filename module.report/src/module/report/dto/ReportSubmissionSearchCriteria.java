package module.report.dto;

import java.io.Serializable;
import java.util.Date;

import module.report.model.ReportStatus;

public class ReportSubmissionSearchCriteria implements Serializable {

	private static final long serialVersionUID = 1L;

	private String categoryCode;

	private Long definitionId;

	private Date requestedDateFrom;

	private Date requestedDateTo;

	private ReportStatus status;

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public Long getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(Long definitionId) {
		this.definitionId = definitionId;
	}

	public Date getRequestedDateFrom() {
		return requestedDateFrom;
	}

	public void setRequestedDateFrom(Date requestedDateFrom) {
		this.requestedDateFrom = requestedDateFrom;
	}

	public Date getRequestedDateTo() {
		return requestedDateTo;
	}

	public void setRequestedDateTo(Date requestedDateTo) {
		this.requestedDateTo = requestedDateTo;
	}

	public ReportStatus getStatus() {
		return status;
	}

	public void setStatus(ReportStatus status) {
		this.status = status;
	}
}
