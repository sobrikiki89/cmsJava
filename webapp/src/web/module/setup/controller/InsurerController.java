package web.module.setup.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.SessionAttributes;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.github.dandelion.datatables.core.ajax.DatatablesResponse;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;

import app.core.exception.BaseApplicationException;
import app.core.spring.UrlPattern;
import module.setup.dto.InsurerDTO;
import module.setup.model.Insurer;
import module.setup.service.InsurerService;
import module.setup.service.StateService;
import web.core.controller.BaseController;
import web.core.helper.WebConstant;
import web.module.setup.model.InsurerForm;
import web.module.setup.validator.InsurerEditFormValidator;
import web.module.setup.validator.InsurerNewFormValidator;

@Controller
@RequestMapping("/secured/setup/insurer")
@SessionAttributes({ "insurerForm" })
public class InsurerController extends BaseController {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(InsurerController.class);

	@Autowired
	private InsurerService insurerService;

	@Autowired
	private StateService stateService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getInsurerList(Locale locale, HttpServletRequest req, Model model) {
		model.addAttribute("insurerForm", new InsurerForm());
		return "secured.setup.insurer";
	}

	@RequestMapping(value = "/insurergrid", method = RequestMethod.GET)
	public @ResponseBody DatatablesResponse<InsurerDTO> getInsurerGrid(
			@DatatablesParams DatatablesCriterias criterias) {
		try {
			DataSet<InsurerDTO> dataSet = insurerService.getInsurers(criterias);
			return DatatablesResponse.build(dataSet, criterias);
		} catch (BaseApplicationException e) {
			LOGGER.error("Error on getting insurer grid listing", e);
		}
		return null;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String insurerPostByAction(Locale locale, HttpServletRequest req, HttpServletResponse resp, Model model,
			@ModelAttribute("insurerForm") InsurerForm insurerForm, BindingResult result) {
		// We will determine the action
		if (WebConstant.ACTION_NEW.equalsIgnoreCase(insurerForm.getAction())) {
			insurerForm.setInsurer(new Insurer());
			insurerForm.getInsurer().setActive(true);
			insurerForm.setStates(stateService.getStates());
			return "secured.setup.insurer.new";
		}

		else if (WebConstant.ACTION_BACK.equalsIgnoreCase(insurerForm.getAction())) {
			return "redirect:/secured";
		}

		else if (WebConstant.ACTION_DELETE.equalsIgnoreCase(insurerForm.getAction())) {
			// TODO :
		}

		return "secured.setup.insurer";
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String saveInsurerNew(Model model, @ModelAttribute("insurerForm") InsurerForm insurerForm,
			BindingResult result) {
		InsurerNewFormValidator validator = new InsurerNewFormValidator(insurerService);
		validator.validate(insurerForm, result);

		if (result.hasErrors()) {
			return "secured.setup.insurer.new";
		} else {
			insurerService.createInsurer(insurerForm.getInsurer());
			return "secured.setup.insurer";
		}
	}

	@RequestMapping(value = "/edit/" + UrlPattern.PARAM_PREFIX + "/{insurerCode}", method = RequestMethod.GET)
	public String getInsurerEdit(@PathVariable("insurerCode") String insurerCode, Model model) {
		Insurer insurer = insurerService.getInsurer(insurerCode);
		if (insurer != null) {
			InsurerForm insurerForm = new InsurerForm();
			insurerForm.setStates(stateService.getStates());
			insurerForm.setInsurer(insurer);
			model.addAttribute("insurerForm", insurerForm);
			return "secured.setup.insurer.edit";
		}
		return "secured.setup.insurer";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String saveInsurerEdit(Model model, @ModelAttribute("insurerForm") InsurerForm insurerForm,
			BindingResult result) {
		InsurerEditFormValidator validator = new InsurerEditFormValidator();
		validator.validate(insurerForm, result);
		if (result.hasErrors()) {
			insurerForm.setStates(stateService.getStates());
			return "secured.setup.insurer.edit";
		} else {
			Insurer insurer = insurerService.updateInsurer(insurerForm.getInsurer());
			insurerForm.setInsurer(insurer);
			return "secured.setup.insurer";
		}
	}
}
