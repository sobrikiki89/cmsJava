package module.report.service;

import java.util.List;

import app.core.security.UserPrincipal;
import module.report.dto.ReportSubmissionDTO;
import module.report.dto.ReportSubmissionSearchCriteria;
import module.report.model.ReportSubmission;
import module.setup.model.UserCompany;

public interface ReportSubmissionService {
	public List<ReportSubmissionDTO> searchReportSubmission(ReportSubmissionSearchCriteria criteria);

	public Long createSubmission(ReportSubmission submission);

	public ReportSubmission updateSubmission(ReportSubmission submission);

	public ReportSubmission getSubmissionById(Long id);
}
