package module.report.generator.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.DocxRenderOption;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IPDFRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import app.core.logging.LogUtils;
import module.report.ReportModule;
import module.report.exception.OutputFormatNotSupportedException;
import module.report.generator.BirtPageHandler;
import module.report.generator.DataSourceAware;
import module.report.generator.Generator;
import module.report.handler.ReportParamHandler;
import module.report.model.OutputFileFormat;
import module.report.model.ReportStatus;
import module.report.model.ReportSubmission;
import module.report.service.ReportSubmissionService;

public class BirtGenerator extends Generator implements DataSourceAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(BirtGenerator.class);
	private ReportParamHandler handler;
	private ReportSubmission submission;
	private DataSource dataSource;

	public BirtGenerator(ApplicationContext context, String logFile) {
		super(context, logFile);
	}

	public ReportSubmission getSubmission() {
		return this.submission;
	}

	@Override
	protected void setup(ReportParamHandler handler, ReportSubmission submission) {
		this.handler = handler;
		this.submission = submission;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		LogUtils.addSiftAppender(((ReportModule) context.getBean("ReportModule")).getModuleName());
		LogUtils.startLogging(submission.getFullLogFile());

		LocalDateTime startDate = LocalDateTime.now();
		LOGGER.info("Overall Start date : " + startDate.format(DATETIME_FORMAT));

		boolean exception = false;
		IReportEngine engine = null;
		IRunTask runTask = null;
		IRenderTask renderTask = null;
		IReportDocument document = null;
		try {

			OutputFileFormat format = this.submission.getOutputFormat().getId().getFormat();

			final EngineConfig config = new EngineConfig();
			// Directory Name or folder name for this log
			config.setLogConfig(submission.getReportDir(), Level.FINEST);
			config.setLogFile(this.getSubmission().getId() + "_birt_L.log");

			LOGGER.info("Start up Birt platform");
			Platform.startup(config);

			final IReportEngineFactory FACTORY = (IReportEngineFactory) Platform
					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			engine = FACTORY.createReportEngine(config);

			// ------------------------------------------------------------------------
			// STEP 1. Run report into report document .rptdocument
			// ------------------------------------------------------------------------
			// Open the report design
			LOGGER.info("Opening report design file [" + submission.getOutputFormat().getReportClass() + "]");
			Resource resource = new ClassPathResource(submission.getOutputFormat().getReportClass());
			InputStream in = resource.getInputStream();

			String reportDocument = submission.getReportDir() + File.separator + this.getSubmission().getId()
					+ ".rptdocument";
			LOGGER.info("Report document file [" + reportDocument + "]");

			// Create task to run the report
			final IReportRunnable design = engine.openReportDesign(in);
			final BirtPageHandler pageHandler = new BirtPageHandler();
			runTask = engine.createRunTask(design);
			runTask.setPageHandler(pageHandler);
			// Set parent classloader for engine
			runTask.setLocale(Locale.getDefault());
			runTask.getAppContext().put("OdaJDBCDriverPassInConnection", dataSource.getConnection());
			runTask.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, this.getClass().getClassLoader());
			runTask.setParameterValues(handler.getParamMap());
			boolean result = runTask.validateParameters();
			if (!result) {
				throw new BirtException("Birt report validation fail");
			}
			LOGGER.info("Generating report document");
			LocalDateTime docStartDate = LocalDateTime.now();
			LOGGER.info("rptdocument Start date : " + docStartDate.format(DATETIME_FORMAT));
			runTask.run(reportDocument);
			runTask.close();
			LocalDateTime docEndDate = LocalDateTime.now();
			LOGGER.info("rptdocument End date : " + docEndDate.format(DATETIME_FORMAT));
			LOGGER.info("rptdocument Duration : " + getDuration(docStartDate, docEndDate));

			// ------------------------------------------------------------------------
			// STEP 2. Render report into desired document
			// ------------------------------------------------------------------------
			// Open the report document
			document = engine.openReportDocument(reportDocument);

			final IRenderOption options = new RenderOption();
			options.setOutputFormat(format.name().toLowerCase());
			options.setOutputFileName(submission.getFullOutputFile());
			long pgCnt = document.getPageCount();
			LOGGER.info("Page Count = " + pgCnt);

			switch (format) {
			case PDF:
				final PDFRenderOption pdfOptions = new PDFRenderOption(options);
				pdfOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW, IPDFRenderOption.FIT_TO_PAGE_SIZE);
				pdfOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW, IPDFRenderOption.OUTPUT_TO_MULTIPLE_PAGES);
				break;
			case XLS:
				final EXCELRenderOption excelOption = new EXCELRenderOption(options);
				excelOption.setEmitterID("org.eclipse.birt.report.engine.emitter.prototype.excel");
				excelOption.setHideGridlines(false);
				excelOption.setOfficeVersion("office2007");
				excelOption.setWrappingText(true);
				excelOption.setEnableMultipleSheet(false);
				break;
			case DOCX:
				final DocxRenderOption docxRenderOption = new DocxRenderOption();
				// docxRenderOption.setEmitterID("org.eclipse.birt.report.engine.emitter.docx");
				docxRenderOption.setOption(IRenderOption.HTML_PAGINATION, Boolean.TRUE);
				docxRenderOption.setOption(IRenderOption.RENDER_DPI, 96);
				docxRenderOption.setOption(IPDFRenderOption.PAGE_OVERFLOW, IPDFRenderOption.FIT_TO_PAGE_SIZE);
				break;
			case HTML:
				final HTMLRenderOption htmlOptions = new HTMLRenderOption(options);
				htmlOptions.setHtmlPagination(false);
				htmlOptions.setHtmlRtLFlag(false);
				htmlOptions.setEmbeddable(false);
				break;
			default:
				throw new OutputFormatNotSupportedException(
						format + " is not supportted in Birt report at this moment.");
			}

			renderTask = engine.createRenderTask(document);
			renderTask.setRenderOption(options);

			// Prepare to generate Byte[] Data
			ByteArrayOutputStream outs = new ByteArrayOutputStream();
			options.setOutputStream(outs);

			// run and render report
			LOGGER.info("Rendering report document");
			LocalDateTime renderStartDate = LocalDateTime.now();
			LOGGER.info("render Start date : " + renderStartDate.format(DATETIME_FORMAT));
			renderTask.setLocale(Locale.getDefault());
			renderTask.render();
			setResult(outs.toByteArray());
			document.close();
			renderTask.close();
			LocalDateTime renderEndDate = LocalDateTime.now();
			LOGGER.info("render End date : " + renderEndDate.format(DATETIME_FORMAT));
			LOGGER.info("render Duration : " + getDuration(renderStartDate, renderEndDate));

			engine.destroy();

			submission.setStatus(ReportStatus.FINISHED);

			LOGGER.info("Deleting report document file [" + reportDocument + "]");
			FileUtils.deleteQuietly(new File(reportDocument));
			LOGGER.info("Finish delete report document file");
		} catch (OutputFormatNotSupportedException ex) {
			LOGGER.error(ex.getMessage(), ex);
			exception = true;
		} catch (BirtException e) {
			LOGGER.error("Error in Birt", e);
			exception = true;
		} catch (SQLException e) {
			LOGGER.error("Error in SQL", e);
			exception = true;
		} catch (IOException e) {
			LOGGER.error("Error in opening birt report file", e);
			exception = true;
		} finally {
			if (exception) {
				submission.setOutputFile(null);
				submission.setStatus(ReportStatus.ERROR);
			}
			submission.setEndDate(new Date());

			if (document != null) {
				document.close();
			}

			if (runTask != null) {
				runTask.close();
			}

			if (renderTask != null) {
				renderTask.close();
			}

			if (engine != null) {
				engine.destroy();
			}
			Platform.shutdown();
		}

		ReportSubmissionService reportSubmissionService = context.getBean(ReportSubmissionService.class);
		reportSubmissionService.updateSubmission(submission);
		LocalDateTime endDate = LocalDateTime.now();
		LOGGER.info("Overall End date : " + endDate.format(DATETIME_FORMAT));
		LOGGER.info("Overall Duration : " + getDuration(startDate, endDate));
		LogUtils.stopLogging();
	}

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}
}
