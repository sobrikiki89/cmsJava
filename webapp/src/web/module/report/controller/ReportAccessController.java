package web.module.report.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.dandelion.core.util.StringUtils;
import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.github.dandelion.datatables.core.ajax.DatatablesResponse;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;

import app.core.exception.BaseApplicationException;
import app.core.spring.UrlPattern;
import app.core.usermgmt.service.UserMgmtService;
import module.report.dto.ReportAccessDTO;
import module.report.model.ReportAccessControl;
import module.report.service.ReportSetupService;
import web.core.controller.BaseController;
import web.core.helper.WebConstant;
import web.module.report.model.ReportAccessForm;
import web.module.report.validator.ReportAccessEditFormValidator;
import web.module.report.validator.ReportAccessNewFormValidator;

@Controller
@RequestMapping("/secured/report/access")
public class ReportAccessController extends BaseController {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportAccessController.class);

	private static final String[][] GRID_COLUMN_MAPPING = { { "reportName", "definition.name" },
			{ "reportCategory", "definition.category.name" }, { "role", "role.name" } };

	@Autowired
	private ReportSetupService reportSetupService;

	@Autowired
	private UserMgmtService userMgmtService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Long.class, "accessControl.definition.id",
				new CustomNumberEditor(Long.class, true));
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getReportAccessList(Locale locale, HttpServletRequest req, Model model) {
		model.addAttribute("reportAccessForm", new ReportAccessForm());
		return "secured.report.access";
	}

	@RequestMapping(value = "/accesscontrolgrid", method = RequestMethod.GET)
	public @ResponseBody DatatablesResponse<ReportAccessDTO> getReportAccessGrid(
			@DatatablesParams DatatablesCriterias criterias) {
		try {
			List<ColumnDef> columns = criterias.getColumnDefs();
			for (String[] mapping : GRID_COLUMN_MAPPING) {
				for (ColumnDef column : columns) {
					if (column.getName().equals(mapping[0])) {
						LOGGER.info("Column Name : " + column.getName() + " ->" + mapping[1]);
						column.setName(mapping[1]);
					}
				}
			}
			DataSet<ReportAccessDTO> dataSet = reportSetupService.getReportAccessGrid(criterias);
			return DatatablesResponse.build(dataSet, criterias);
		} catch (BaseApplicationException e) {
			LOGGER.error("Error on getting report access grid listing", e);
		}
		return null;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String reportAccessPostByAction(HttpServletRequest req, Model model,
			@ModelAttribute("reportAccessForm") ReportAccessForm reportAccessForm, BindingResult result) {
		// We will determine the action
		if (WebConstant.ACTION_NEW.equalsIgnoreCase(reportAccessForm.getAction())) {
			reportAccessForm.setCategoryList(reportSetupService.getCategories());
			reportAccessForm.setRoleList(userMgmtService.getRoles());
			return "secured.report.access.new";
		} else if (WebConstant.ACTION_BACK.equalsIgnoreCase(reportAccessForm.getAction())) {
			return "redirect:/secured";
		} else if (WebConstant.ACTION_DELETE.equalsIgnoreCase(reportAccessForm.getAction())) {
			String[] selected = req.getParameterValues("selected");
			if (selected != null && selected.length > 0) {
				List<ReportAccessControl> reportAccessControls = new ArrayList<ReportAccessControl>();
				for (String rf : selected) {
					ReportAccessControl accessControl = new ReportAccessControl();
					accessControl.setId(Long.parseLong(rf));
					reportAccessControls.add(accessControl);
				}
				reportSetupService.deleteAccessCountrol(reportAccessControls);
			}

		} else {
		}
		return "secured.report.access";
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String saveReportAccessNew(Model model,
			@ModelAttribute("reportAccessForm") ReportAccessForm reportAccessForm, BindingResult result) {
		ReportAccessNewFormValidator validator = new ReportAccessNewFormValidator();
		validator.validate(reportAccessForm, result);

		if (result.hasErrors()) {
			reportAccessForm.setCategoryList(reportSetupService.getCategories());
			reportAccessForm.setRoleList(userMgmtService.getRoles());
			if (StringUtils.isNotBlank(reportAccessForm.getAccessControl().getDefinition().getCategory().getCode())) {
				reportAccessForm.setDefinitionList(reportSetupService.getReportDefinitionByCategory(
						reportAccessForm.getAccessControl().getDefinition().getCategory()));
			}
			return "secured.report.access.new";
		} else {
			reportSetupService.createAccessControl(reportAccessForm.getAccessControl());
			return "secured.report.access";
		}
	}

	@RequestMapping(value = "/edit/" + UrlPattern.PARAM_PREFIX + "/{accessId}", method = RequestMethod.GET)
	public String getReportAccessEdit(@PathVariable("accessId") Long accessId, Model model) {
		ReportAccessControl accessControl = reportSetupService.getAccessControlById(accessId);
		if (accessControl != null) {
			ReportAccessForm reportAccessForm = new ReportAccessForm();
			reportAccessForm.setAccessControl(accessControl);
			reportAccessForm.setCategoryList(reportSetupService.getCategories());
			reportAccessForm.setDefinitionList(
					reportSetupService.getReportDefinitionByCategory(accessControl.getDefinition().getCategory()));
			reportAccessForm.setRoleList(userMgmtService.getRoles());
			model.addAttribute("reportAccessForm", reportAccessForm);
			return "secured.report.access.edit";
		}
		return "secured.report.access";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String saveReportAccessEdit(Model model,
			@ModelAttribute("reportAccessForm") ReportAccessForm reportAccessForm, BindingResult result) {
		ReportAccessEditFormValidator validator = new ReportAccessEditFormValidator();
		validator.validate(reportAccessForm, result);

		if (result.hasErrors()) {
			reportAccessForm.setCategoryList(reportSetupService.getCategories());
			reportAccessForm.setRoleList(userMgmtService.getRoles());
			if (StringUtils.isNotBlank(reportAccessForm.getAccessControl().getDefinition().getCategory().getCode())) {
				reportAccessForm.setDefinitionList(reportSetupService.getReportDefinitionByCategory(
						reportAccessForm.getAccessControl().getDefinition().getCategory()));
			}
			return "secured.report.access.edit";
		} else {
			ReportAccessControl accessControl = reportSetupService
					.updateAccessControl(reportAccessForm.getAccessControl());
			reportAccessForm.setAccessControl(accessControl);
			return "secured.report.access";
		}
	}
}
