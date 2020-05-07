package module.report.model;

import java.io.File;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import app.core.model.EntityHistory;

@Entity
@Table(name = "REPORT_SUBMISSION")
public class ReportSubmission extends EntityHistory {
	private static final long serialVersionUID = 1L;

	private Long id;

	private Date requestedDate;

	private String requestedBy;

	private Date endDate;

	private String logFile;

	private String outputFile;

	private String param;

	private ReportOutputFormat outputFormat;

	private ReportStatus status;

	private String reportDir;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "entityKeys")
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "REQUESTED_DATE")
	public Date getRequestedDate() {
		return requestedDate;
	}

	public void setRequestedDate(Date requestedDate) {
		this.requestedDate = requestedDate;
	}

	@Column(name = "REQUESTED_BY")
	public String getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}

	@Column(name = "END_DATE")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name = "LOG_FILE")
	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	@Column(name = "OUTPUT_FILE")
	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	@Lob
	@Column(name = "PARAMETER")
	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "DEFINITION_ID", referencedColumnName = "DEFINITION_ID"),
			@JoinColumn(name = "OUTPUT_FORMAT", referencedColumnName = "FORMAT") })
	public ReportOutputFormat getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(ReportOutputFormat outputFormat) {
		this.outputFormat = outputFormat;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	public ReportStatus getStatus() {
		return status;
	}

	public void setStatus(ReportStatus status) {
		this.status = status;
	}

	@Transient
	public String getReportDir() {
		return reportDir;
	}

	public void setReportDir(String reportDir) {
		this.reportDir = reportDir;
	}

	@Transient
	public String getFullLogFile() {
		return reportDir + File.separator + logFile;
	}

	@Transient
	public String getFullOutputFile() {
		return reportDir + File.separator + outputFile;
	}
}
