package module.report.generator.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import app.core.logging.LogUtils;
import module.report.ReportModule;
import module.report.generator.DataSourceAware;
import module.report.generator.Generator;
import module.report.handler.ReportParamHandler;
import module.report.model.OutputFileFormat;
import module.report.model.ReportStatus;
import module.report.model.ReportSubmission;
import module.report.service.ReportSubmissionService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

public class JasperGenerator extends Generator implements DataSourceAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(JasperGenerator.class);
	private ReportParamHandler handler;
	private ReportSubmission submission;
	private DataSource dataSource;

	public JasperGenerator(ApplicationContext context, String logFile) {
		super(context, logFile);
	}

	@Override
	protected void setup(ReportParamHandler handler, ReportSubmission submission) {
		this.handler = handler;
		this.submission = submission;
	}

	public ReportSubmission getSubmission() {
		return this.submission;
	}

	@Override
	public void run() {
		LogUtils.addSiftAppender(((ReportModule) context.getBean("ReportModule")).getModuleName());
		LogUtils.startLogging(submission.getFullLogFile());

		LocalDateTime startDate = LocalDateTime.now();
		LOGGER.info("Start date : " + startDate.format(DATETIME_FORMAT));

		boolean exception = false;
		try {
			Resource resource = new ClassPathResource(submission.getOutputFormat().getReportClass());

			InputStream in = resource.getInputStream();

			OutputFileFormat format = this.submission.getOutputFormat().getId().getFormat();
			Connection connection = dataSource.getConnection();
			JasperPrint jasperPrint = JasperFillManager.fillReport(in, handler.getParamMap(), connection);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			FileOutputStream fOut = new FileOutputStream(submission.getFullOutputFile());

			switch (format) {
			case CSV:
				JRCsvExporter csvExporter = new JRCsvExporter();
				csvExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				csvExporter.setExporterOutput(new SimpleWriterExporterOutput(out));
				csvExporter.exportReport();
				setResult(out.toByteArray());
				IOUtils.write(getResult(), fOut);
				fOut.close();
				out.close();
				break;

			case PDF:
				JRPdfExporter pdfExporter = new JRPdfExporter();
				pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
				pdfExporter.exportReport();
				setResult(out.toByteArray());
				IOUtils.write(getResult(), fOut);
				fOut.close();
				out.close();
				break;

			case RTF:
				JRRtfExporter rtfExporter = new JRRtfExporter();
				rtfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				rtfExporter.setExporterOutput(new SimpleWriterExporterOutput(out));
				rtfExporter.exportReport();
				setResult(out.toByteArray());
				IOUtils.write(getResult(), fOut);
				fOut.close();
				out.close();
				break;

			case XLS:
				JRXlsExporter xlsExporter = new JRXlsExporter();
				xlsExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				xlsExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
				xlsExporter.exportReport();
				setResult(out.toByteArray());
				IOUtils.write(getResult(), fOut);
				fOut.close();
				out.close();
				break;

			case DOCX:
				JRDocxExporter docxExporter = new JRDocxExporter();
				docxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				docxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
				docxExporter.exportReport();
				setResult(out.toByteArray());
				IOUtils.write(getResult(), fOut);
				fOut.close();
				out.close();
				break;

			case HTML:
				HtmlExporter htmlExporter = new HtmlExporter();
				htmlExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				htmlExporter.setExporterOutput(new SimpleHtmlExporterOutput(out));
				htmlExporter.exportReport();
				setResult(out.toByteArray());
				IOUtils.write(getResult(), fOut);
				fOut.close();
				out.close();
				break;
			}

			submission.setStatus(ReportStatus.FINISHED);
			if (jasperPrint.getPages().size() == 0) {
				submission.setStatus(ReportStatus.NO_DATA_FOUND);
			}

		} catch (IOException e) {
			LOGGER.error("Unable to read content of resource [" + submission.getOutputFormat().getReportClass() + "]",
					e);
			exception = true;
		} catch (SQLException e) {
			LOGGER.error("Unable to perform any database IO", e);
			exception = true;
		} catch (JRException e) {
			LOGGER.error("Error on filling up Jasper report", e);
			exception = true;
		} catch (Exception e) {
			LOGGER.error("Error on report", e);
			exception = true;
		} finally {

			if (exception) {
				submission.setOutputFile(null);
				submission.setStatus(ReportStatus.ERROR);
			}
			submission.setEndDate(new Date());
		}

		ReportSubmissionService reportSubmissionService = context.getBean(ReportSubmissionService.class);
		reportSubmissionService.updateSubmission(submission);
		LocalDateTime endDate = LocalDateTime.now();
		LOGGER.info("End date : " + endDate.format(DATETIME_FORMAT));
		LOGGER.info("Duration : " + getDuration(startDate, endDate));
		LogUtils.stopLogging();
	}

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}
}
