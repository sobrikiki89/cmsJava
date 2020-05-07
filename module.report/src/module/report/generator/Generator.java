package module.report.generator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;

import module.report.handler.ReportParamHandler;
import module.report.model.OutputFileFormat;
import module.report.model.ReportSubmission;

public abstract class Generator implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(Generator.class);
	
	public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS");
	
	protected ApplicationContext context;
	protected String logFile;

	private byte[] result;

	public Generator(ApplicationContext context, String logFile) {
		this.context = context;
		this.logFile = logFile;
	}

	public abstract ReportSubmission getSubmission();

	protected abstract void setup(ReportParamHandler handler, ReportSubmission submission);

	public byte[] getResult() {
		return result;
	}

	public void setResult(byte[] result) {
		this.result = result;
	}

	public void start() {
		// LogUtils.addFileAppender(LOGGER, logFile);
		String fileName = formatReportName(getSubmission());
		LOGGER.info("Report Output FileName [" + fileName + "]");
		if (getSubmission() != null) {
			getSubmission().setOutputFile(fileName);
		}
		TaskExecutor executor = context.getBean("reportTaskExecutor", TaskExecutor.class);
		executor.execute(this);
	}

	public static String getDuration(LocalDateTime start, LocalDateTime end) {
		String result = "";
		ChronoUnit[] units = new ChronoUnit[] { ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.DAYS, ChronoUnit.HOURS,
				ChronoUnit.MINUTES, ChronoUnit.SECONDS, ChronoUnit.MILLIS };
		for (ChronoUnit unit : units) {
			Long amount = start.until(end, unit);
			start = start.plus(amount, unit);

			if (amount > 0) {
				result += (result.length() > 0 ? ", " : "") + amount + " " + unit.toString();
			}
		}

		return result;
	}

	protected String formatReportName(ReportSubmission submission) {
		if (submission != null) {
			String reportName = submission.getOutputFormat().getDefinition().getName().replaceAll(" ", "_");
			OutputFileFormat format = submission.getOutputFormat().getId().getFormat();
			return submission.getId() + "_" + reportName + "." + format.name().toLowerCase();
		}
		return "";
	}
}
