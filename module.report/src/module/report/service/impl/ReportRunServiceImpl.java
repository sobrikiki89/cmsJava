package module.report.service.impl;

import java.time.LocalDateTime;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import app.core.security.UserPrincipal;
import module.report.generator.Generator;
import module.report.generator.ReportGeneratorFactory;
import module.report.handler.ReportParamHandler;
import module.report.model.ReportStatus;
import module.report.model.ReportSubmission;
import module.report.service.ReportRunService;

@Service
public class ReportRunServiceImpl implements ReportRunService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportRunServiceImpl.class);

	public ReportSubmission start(ReportParamHandler handler, ReportSubmission submission) {

		if (!handler.getParamMap().containsKey(ReportParamHandler.REPORT_DATETIME)
				|| handler.getParamMap().get(ReportParamHandler.REPORT_DATETIME) == null) {
			handler.getParamMap().put(ReportParamHandler.REPORT_DATETIME,
					LocalDateTime.now().format(Generator.DATETIME_FORMAT));
		}
		if (!handler.getParamMap().containsKey(ReportParamHandler.REPORT_CREATOR)
				|| handler.getParamMap().get(ReportParamHandler.REPORT_CREATOR) == null) {
			UserPrincipal principal = UserPrincipal.class
					.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
			handler.getParamMap().put(ReportParamHandler.REPORT_CREATOR,
					principal == null ? null : principal.getUsername());
		}

		Generator g = ReportGeneratorFactory.getInstance().getReportGenerator(handler, submission);

		if (g != null) {
			g.start();
		} else {
			submission.setStatus(ReportStatus.ERROR);
			submission.setEndDate(new Date());
		}
		return submission;
	}
}
