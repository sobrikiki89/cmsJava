package module.report.dto;

import java.io.Serializable;

import app.core.dto.DTOBase;

public class ReportAccessDTO extends DTOBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String reportName;

	private String reportCategory;

	private String role;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportCategory() {
		return reportCategory;
	}

	public void setReportCategory(String reportCategory) {
		this.reportCategory = reportCategory;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
