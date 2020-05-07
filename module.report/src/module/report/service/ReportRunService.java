package module.report.service;

import module.report.handler.ReportParamHandler;
import module.report.model.ReportSubmission;

public interface ReportRunService {
	public ReportSubmission start(ReportParamHandler handler, ReportSubmission submission);
}
