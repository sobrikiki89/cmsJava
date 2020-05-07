package web.module.report.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.github.dandelion.core.util.StringUtils;

import app.core.logging.LogUtils;
import app.core.security.UserPrincipal;
import app.core.utils.AppConstant;
import app.core.utils.ReflectionUtils;
import module.report.ReportModule;
import module.report.dto.ReportSubmissionSearchCriteria;
import module.report.handler.ReportParamHandler;
import module.report.model.ReportCategory;
import module.report.model.ReportDefinition;
import module.report.model.ReportOutputFormat;
import module.report.model.ReportStatus;
import module.report.model.ReportSubmission;
import module.report.service.ReportRunService;
import module.report.service.ReportSetupService;
import module.report.service.ReportSubmissionService;
import module.setup.model.UserCompany;
import module.setup.service.CompanyService;
import module.setup.service.SetupConstant;
import web.core.controller.BaseController;
import web.core.helper.WebConstant;
import web.module.report.model.ReportSubmissionForm;
import web.module.report.model.ReportSubmissionSearchForm;

@Controller
@RequestMapping("/secured/report/submission")
@SessionAttributes({ "reportSubmissionSearchForm", "reportSubmissionForm" })
public class ReportSubmissionController extends BaseController {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportSubmissionController.class);

	@Autowired
	private ApplicationContext context;

	@Autowired
	@Qualifier("ReportModule")
	private ReportModule reportModule;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private ReportSetupService reportSetupService;

	@Autowired
	private ReportSubmissionService reportSubmissionService;

	@Autowired
	private ReportRunService reportRunService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.DATE_FORMAT);
		binder.registerCustomEditor(Date.class, "criteria.requestedDateFrom", new CustomDateEditor(sdf, true));
		binder.registerCustomEditor(Date.class, "criteria.requestedDateTo", new CustomDateEditor(sdf, true));
		binder.registerCustomEditor(Long.class, "criteria.definitionId", new CustomNumberEditor(Long.class, true));
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getSearchScreen(Locale locale, HttpServletRequest req, Model model) {
		ReportSubmissionSearchForm reportSubmissionSearchForm = new ReportSubmissionSearchForm();
		reportSubmissionSearchForm.setCriteria(new ReportSubmissionSearchCriteria());
		reportSubmissionSearchForm.getCriteria()
				.setRequestedDateFrom(java.sql.Date.valueOf(LocalDate.now().minusDays(3)));
		reportSubmissionSearchForm.getCriteria().setRequestedDateTo(java.sql.Date.valueOf(LocalDate.now()));
		reportSubmissionSearchForm.setCategoryList(reportSetupService.getCategories());
		reportSubmissionSearchForm.setStatuses(ReportStatus.values());
		model.addAttribute("reportSubmissionSearchForm", reportSubmissionSearchForm);
		return "secured.report.submission";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String searchReportSubmission(Model model,
			@ModelAttribute("reportSubmissionSearchForm") ReportSubmissionSearchForm reportSubmissionSearchForm,
			BindingResult result) {
		// We will determine the action
		if (WebConstant.ACTION_NEW.equalsIgnoreCase(reportSubmissionSearchForm.getAction())) {
			ReportSubmissionForm reportSubmissionForm = new ReportSubmissionForm();
			reportSubmissionForm.setCategoryList(reportSetupService.getCategories());
			reportSubmissionForm.setSubmission(new ReportSubmission());
			model.addAttribute("reportSubmissionForm", reportSubmissionForm);
			return "secured.report.submission.new";
		} else if (WebConstant.ACTION_BACK.equalsIgnoreCase(reportSubmissionSearchForm.getAction())) {
			return "redirect:/secured";
		} else {
			reportSubmissionSearchForm.setSearched(true);
			if (StringUtils.isNotBlank(reportSubmissionSearchForm.getCriteria().getCategoryCode())) {
				ReportCategory cat = new ReportCategory();
				cat.setCode(reportSubmissionSearchForm.getCriteria().getCategoryCode());
				reportSubmissionSearchForm.setDefinitionList(reportSetupService.getReportDefinitionByCategory(cat));
			}
			reportSubmissionSearchForm.setSubmissions(
					reportSubmissionService.searchReportSubmission(reportSubmissionSearchForm.getCriteria()));
		}

		return "secured.report.submission";
	}

	@RequestMapping(value = "/new", method = { RequestMethod.POST, RequestMethod.GET })
	public String renderReportSubmission(Model model,
			@ModelAttribute("reportSubmissionForm") ReportSubmissionForm reportSubmissionForm, BindingResult result) {

		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		List<UserCompany> userCompany = companyService.getUserCompany(principal);

		if (WebConstant.ACTION_SUBMIT.equalsIgnoreCase(reportSubmissionForm.getAction())) {
			executeReport(reportSubmissionForm, result);
			reportSubmissionForm.setAction(null);
			if (result.hasErrors()) {
				return "secured.report.submission.new";
			}
			ReportSubmissionSearchForm searchForm = (ReportSubmissionSearchForm) model.asMap()
					.get("reportSubmissionSearchForm");
			searchForm.setAction(null);
			return searchReportSubmission(model, searchForm, result);
		} else if (WebConstant.ACTION_BACK.equalsIgnoreCase(reportSubmissionForm.getAction())) {
			return "secured.report.submission";
		} else {
			reportSubmissionForm.setAllowedFlag(false);
		
			if (userCompany != null) {
				for (UserCompany uc : userCompany) {
					if (uc.getCompanyId().getCode().equalsIgnoreCase(SetupConstant.COMPANY_CODE_SIB)) {
						reportSubmissionForm.setAllowedFlag(true);
						break;
					}
				}
				
				if(userCompany.size() > 1) {
					reportSubmissionForm.setAllowedFlag(true);
				}
			} 
						
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("@@Reporting : Render Report Submission, Category Code ["
						+ reportSubmissionForm.getSubmission().getOutputFormat().getDefinition().getCategory().getCode()
						+ "]");
			}
			// Render definition list based on the current category
			if (reportSubmissionForm.getSubmission().getOutputFormat().getDefinition().getCategory() == null
					|| StringUtils.isBlank(reportSubmissionForm.getSubmission().getOutputFormat().getDefinition()
							.getCategory().getCode())) {
				reportSubmissionForm.setDefinitionList(new ArrayList<ReportDefinition>());
			} else {
				reportSubmissionForm.setDefinitionList(reportSetupService.getReportDefinitionByCategory(
						reportSubmissionForm.getSubmission().getOutputFormat().getDefinition().getCategory()));
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("@@Reporting : Render Report Submission, Definition ID ["
						+ reportSubmissionForm.getSubmission().getOutputFormat().getId().getDefinitionId() + "]");
			}

			if (reportSubmissionForm.getSubmission().getOutputFormat().getId().getDefinitionId() != null) {
				ReportDefinition definition = reportSetupService.getReportDefinitionById(
						reportSubmissionForm.getSubmission().getOutputFormat().getId().getDefinitionId());
				reportSubmissionForm.getSubmission().getOutputFormat().setDefinition(definition);
				setupReport(reportSubmissionForm, definition, result);
			}

			return "secured.report.submission.new";
		}
	}

	protected void setupReport(ReportSubmissionForm reportSubmissionForm, ReportDefinition definition,
			BindingResult result) {
		if (definition != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("@@Reporting : Setting Up Report Param Handler, definition name [" + definition.getName()
						+ "]");
				LOGGER.debug("@@Reporting : Setting Up Report Param Handler, param handler class ["
						+ definition.getParamHandlerClass() + "]");
				LOGGER.debug(
						"@@Reporting : Setting Up Report Param Handler, jsp path [" + definition.getJspPath() + "]");
			}

			// Setting up report format
			List<String> formats = new ArrayList<String>();
			for (ReportOutputFormat f : definition.getOutputFormats()) {
				formats.add(f.getId().getFormat().name());
			}
			reportSubmissionForm.setFormats(formats);

			// Setting up report param handler
			ReportParamHandler handler = ReflectionUtils.create(ReportParamHandler.class,
					definition.getParamHandlerClass());
			if (handler != null) {
				handler.setContext(context);
				handler.setupLookup();
				reportSubmissionForm.setHandler(handler);
			} else {
				// No report handler found
				result.reject("report.submission.invalidReport");
			}
		} else {
			// No report found
			result.reject("report.submission.noReportFound");
		}
	}

	protected void executeReport(ReportSubmissionForm reportSubmissionForm, BindingResult result) {
		// No report found
		ReportDefinition definition = reportSetupService.getReportDefinitionById(
				reportSubmissionForm.getSubmission().getOutputFormat().getId().getDefinitionId());
		if (definition == null) {
			result.reject("report.submission.noReportFound");
			return;
		}

		if (reportSubmissionForm.getSubmission().getOutputFormat().getId().getFormat() == null) {
			result.reject("report.submission.outputFormatRequired");
			return;
		}
		reportSubmissionForm.getSubmission().setOutputFormat(reportSetupService.getReportOutputFormatByFormat(
				definition.getId(), reportSubmissionForm.getSubmission().getOutputFormat().getId().getFormat()));
		reportSubmissionForm.getSubmission().getOutputFormat().setDefinition(definition);

		reportSubmissionForm.getHandler().validateParams(result);
		if (result.hasErrors()) {
			result.reject("report.submission.parameterFormatError");
			LOGGER.warn("@@Reporting : Error found when validating parameters [" + result.getAllErrors() + "]");
			return;
		}
		String paramStr = reportSubmissionForm.getHandler().formatParams();

		// Create new submission report in table
		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		reportSubmissionForm.getSubmission().setRequestedDate(new Date());
		reportSubmissionForm.getSubmission().setRequestedBy(principal.getUsername());
		reportSubmissionForm.getSubmission().setParam(paramStr);
		reportSubmissionForm.getSubmission().setStatus(ReportStatus.SUBMITTED);
		reportSubmissionForm.getSubmission().setOutputFormat(reportSubmissionForm.getSubmission().getOutputFormat());
		reportSubmissionForm.getSubmission().setReportDir(reportModule.getReportDir());
		Long submissionId = reportSubmissionService.createSubmission(reportSubmissionForm.getSubmission());

		reportSubmissionForm.getSubmission().setId(submissionId);
		reportSubmissionForm.getSubmission().setLogFile(submissionId + "L.log");

		LogUtils.addSiftAppender(reportModule.getModuleName());
		LogUtils.startLogging(reportSubmissionForm.getSubmission().getFullLogFile());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("@@Reporting : Report job submitted, Submission Id [" + submissionId + "]");
			LOGGER.debug("@@Reporting : Executing Report, definition name [" + definition.getName() + "]");
			LOGGER.debug(
					"@@Reporting : Executing Report, param handler class [" + definition.getParamHandlerClass() + "]");
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("@@Reporting : Formatted parameter string [" + paramStr + "]");
		}

		reportSubmissionForm.setSubmission(
				reportRunService.start(reportSubmissionForm.getHandler(), reportSubmissionForm.getSubmission()));
		reportSubmissionForm
				.setSubmission(reportSubmissionService.updateSubmission(reportSubmissionForm.getSubmission()));
		LogUtils.stopLogging();
	}
}
