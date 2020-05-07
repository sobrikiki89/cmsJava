package module.report.generator;

import java.lang.reflect.Constructor;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

import module.report.generator.impl.BirtGenerator;
import module.report.generator.impl.JasperGenerator;
import module.report.handler.ReportParamHandler;
import module.report.model.OutputFileFormat;
import module.report.model.ReportOutputFormat;
import module.report.model.ReportSubmission;

public class ReportGeneratorFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportGeneratorFactory.class);

	private static ReportGeneratorFactory instance;

	private ReportGeneratorFactory() {
	}

	public synchronized static ReportGeneratorFactory getInstance() {
		if (instance == null) {
			instance = new ReportGeneratorFactory();
		}

		return instance;
	}

	@SuppressWarnings("unchecked")
	public Generator getReportGenerator(ReportParamHandler handler, ReportSubmission submission) {
		if (handler == null) {
			LOGGER.error("Report Parameter Handler is not defined");
			return null;
		}

		if (handler.getContext() == null) {
			LOGGER.error("Application Context is not set in handler");
			return null;
		}

		ReportOutputFormat f = submission.getOutputFormat();
		if (f == null || f.getId() == null) {
			LOGGER.error("No output format definition found");
			return null;
		}

		OutputFileFormat format = f.getId().getFormat();
		String reportClass = f.getReportClass();
		LOGGER.info("Output Format : [" + format + "]");
		LOGGER.info("Report Class : [" + reportClass + "]");

		Generator generator = null;

		switch (f.getDefinition().getGeneratorType()) {
		case BIRT:
			generator = new BirtGenerator(handler.getContext(), submission.getFullLogFile());
			break;
		case JASPER:
			generator = new JasperGenerator(handler.getContext(), submission.getFullLogFile());
			break;
		case JAVA:
			try {
				Class<Generator> clazz = (Class<Generator>) ClassUtils.forName(
						reportClass.substring(0, reportClass.lastIndexOf(".")).replaceAll("/", "."),
						ReportGeneratorFactory.class.getClassLoader());
				Constructor<Generator> cons = clazz.getConstructor(ApplicationContext.class, String.class);
				generator = cons.newInstance(handler.getContext(), submission.getFullLogFile());
			} catch (Exception e) {
				LOGGER.error("Unable to load the report class", e);
			}
			break;
		}

		if (generator == null) {
			LOGGER.error("Unable to find any generator");
		}

		generator.setup(handler, submission);
		if (generator instanceof DataSourceAware) {
			((DataSourceAware) generator).setDataSource(handler.getContext().getBean(DataSource.class));
		}

		return generator;
	}
}
