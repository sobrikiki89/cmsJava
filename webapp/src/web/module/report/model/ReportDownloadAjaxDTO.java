package web.module.report.model;

import com.fasterxml.jackson.annotation.JsonView;

public class ReportDownloadAjaxDTO {

	private Long submissionId;

	private String status;

	private String reportFile;

	private String logFile;

	private String selectedFile;

	public static class PublicView {
	}

	@JsonView(PublicView.class)
	public Long getSubmissionId() {
		return submissionId;
	}

	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}

	@JsonView(PublicView.class)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@JsonView(PublicView.class)
	public String getReportFile() {
		return reportFile;
	}

	public void setReportFile(String reportFile) {
		this.reportFile = reportFile;
	}

	@JsonView(PublicView.class)
	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	@JsonView(PublicView.class)
	public String getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(String selectedFile) {
		this.selectedFile = selectedFile;
	}
}
