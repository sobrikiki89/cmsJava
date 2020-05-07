package web.module.report.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import app.core.spring.UrlPattern;
import module.report.handler.ReportParamHandler;
import module.setup.model.InsuranceClass;
import module.setup.service.InsuranceClassService;
import web.module.report.model.ReportSubmissionForm;

@Controller
@RequestMapping("/secured/report/submission/lookup")
@SessionAttributes({ "reportSubmissionSearchForm", "reportSubmissionForm" })
public class ReportCommonLookupController {

	@Autowired
	private InsuranceClassService insuranceClassService;
	
	@RequestMapping(value = "/insuranceClass/" + UrlPattern.PARAM_PREFIX + "/{code}", method = RequestMethod.GET)
	public @ResponseBody List<InsuranceClass> renderReportSubmission(@PathVariable("code") String code, 
			Model model, @ModelAttribute("reportSubmissionForm") ReportSubmissionForm reportSubmissionForm, 
			BindingResult result) {
		List<InsuranceClass> list = new ArrayList<InsuranceClass>();
		ReportParamHandler handler = reportSubmissionForm.getHandler();
		if (handler != null && code != null && !"all".equalsIgnoreCase(code)) {
			list = insuranceClassService.getInsuranceClassesByGroup(code);
		} else {
			list = insuranceClassService.getInsuranceClasses();
		}
		Map<String, List<? extends Object>> lookupMap = handler.getLookupMap();
		lookupMap.put("insuranceClasses", list);
		return list;
	}

}
