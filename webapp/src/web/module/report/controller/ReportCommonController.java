package web.module.report.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.dandelion.core.util.StringUtils;

import app.core.spring.UrlPattern;
import module.report.ReportModule;
import module.report.model.ReportCategory;
import module.report.model.ReportDefinition;
import module.report.model.ReportSubmission;
import module.report.service.ReportSetupService;
import module.report.service.ReportSubmissionService;
import web.core.controller.BaseController;
import web.module.report.model.ReportDefinitionAjaxDTO;
import web.module.report.model.ReportDefinitionsAjaxDTO;
import web.module.report.model.ReportDownloadAjaxDTO;

@Controller
@RequestMapping("/secured/report/common")
public class ReportCommonController extends BaseController {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportCommonController.class);

	@Autowired
	@Qualifier("ReportModule")
	private ReportModule reportModule;

	@Autowired
	private ReportSetupService reportSetupService;

	@Autowired
	private ReportSubmissionService reportSubmissionService;

	@RequestMapping(value = "/refresh/reportdefinition", method = RequestMethod.POST)
	public @ResponseBody ReportDefinitionsAjaxDTO refreshReportDefinition(
			@RequestBody ReportDefinitionsAjaxDTO request) {
		ReportDefinitionsAjaxDTO dto = new ReportDefinitionsAjaxDTO();
		dto.setCategoryCode(request.getCategoryCode());
		LOGGER.info("Refresh report definition for category [" + request.getCategoryCode() + "]");
		if (StringUtils.isNotBlank(request.getCategoryCode())) {
			ReportCategory cat = new ReportCategory();
			cat.setCode(request.getCategoryCode());
			List<ReportDefinition> definitionList = reportSetupService.getReportDefinitionByCategory(cat);
			LOGGER.info("Report definition found with size [" + definitionList.size() + "]");
			List<ReportDefinitionAjaxDTO> definitionDTOList = new ArrayList<ReportDefinitionAjaxDTO>();
			for (ReportDefinition def : definitionList) {
				ReportDefinitionAjaxDTO o = new ReportDefinitionAjaxDTO();
				o.setId(def.getId());
				o.setName(def.getName());
				definitionDTOList.add(o);
			}
			dto.setDefinitions(definitionDTOList);
		}
		return dto;
	}

	@RequestMapping(value = "/download/status", method = RequestMethod.POST)
	public @ResponseBody ReportDownloadAjaxDTO getDownloadStatus(@RequestBody ReportDownloadAjaxDTO request) {
		ReportDownloadAjaxDTO dto = new ReportDownloadAjaxDTO();
		dto.setSubmissionId(request.getSubmissionId());

		LOGGER.info("Getting report download status, submission id [" + request.getSubmissionId() + "]");
		ReportSubmission submission = reportSubmissionService.getSubmissionById(request.getSubmissionId());
		if (submission != null) {
			dto.setStatus(submission.getStatus().getLabel());
			dto.setReportFile(submission.getOutputFile());
			dto.setLogFile(submission.getLogFile());
		}
		return dto;
	}

	@RequestMapping(value = "/download/file/" + UrlPattern.PARAM_PREFIX
			+ "/{submissionId}/{selectedFile}", method = RequestMethod.GET)
	public void getFile(HttpServletResponse response, @PathVariable("submissionId") Long submissionId,
			@PathVariable("selectedFile") String selectedFile) {
		try {
			LOGGER.info("Downloading report file, submission id [" + submissionId + "]");
			LOGGER.info("Downloading report file, selected file [" + selectedFile + "]");

			ReportSubmission submission = reportSubmissionService.getSubmissionById(submissionId);
			if (submission != null) {
				String file = reportModule.getReportDir() + File.separator;
				if (submission.getOutputFile() != null && submission.getOutputFile().equals(selectedFile)) {
					file += submission.getOutputFile();
				} else if (submission.getLogFile() != null && submission.getLogFile().equals(selectedFile)) {
					file += submission.getLogFile();
				}

				Path p = Paths.get(file);
				if (Files.exists(p) && Files.isRegularFile(p)) {
					LOGGER.info("File exists, prepare to read the file content [" + p.toString() + "]");
					File f = p.toFile();
					response.setHeader("Content-Disposition", "attachment; filename=" + f.getName());
					response.setHeader("Content-Length", String.valueOf(f.length()));

					// get your file as InputStream
					FileInputStream fis = new FileInputStream(f);
					// copy it to response's OutputStream
					IOUtils.copy(fis, response.getOutputStream());
					String mimeType = URLConnection.guessContentTypeFromName(f.getName());
					if (mimeType == null) {
						LOGGER.info("mimetype is not detectable, will take default");
						mimeType = "application/octet-stream";
					}
					LOGGER.info("MIME Content Type [" + mimeType + "]");
					response.setContentType(mimeType);
					response.flushBuffer();
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException("IOError writing file to output stream");
		}
	}
}
