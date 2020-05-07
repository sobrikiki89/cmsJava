package web.module.setup.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.github.dandelion.datatables.core.ajax.DatatablesResponse;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;

import app.core.exception.BaseApplicationException;
import app.core.spring.UrlPattern;
import module.setup.dto.AdjusterDTO;
import module.setup.service.AdjusterService;
import module.setup.service.StateService;
import web.core.controller.BaseController;
import web.core.helper.WebConstant;
import web.module.setup.model.AdjusterForm;
import web.module.setup.validator.AdjusterValidator;

@Controller
@RequestMapping("/secured/setup/adjuster")
@SessionAttributes({ "adjusterForm" })
public class AdjusterController extends BaseController {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(AdjusterController.class);

	@Autowired
	private AdjusterService adjusterService;

	@Autowired
	private StateService stateService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	}
	
	@GetMapping()
	public ModelAndView getAdjusterList() {
		ModelAndView modelView = new ModelAndView("secured.setup.adjuster");
		modelView.addObject("adjusterForm", new AdjusterForm());
		return modelView;
	}

	@GetMapping(value = "/grid")
	public @ResponseBody DatatablesResponse<AdjusterDTO> getAdjusterGrid(
			@DatatablesParams DatatablesCriterias criterias) {
		try {
			DataSet<AdjusterDTO> dataSet = adjusterService.findAdjusterBy(criterias);
			return DatatablesResponse.build(dataSet, criterias);
		} catch (BaseApplicationException e) {
			LOGGER.error("Error on getting type of loss grid listing", e);
		}
		return null;
	}

	@PostMapping()
	public String formPostByAction(Locale locale, HttpServletRequest req, Model model, 
			@ModelAttribute("adjusterForm") AdjusterForm adjusterForm, BindingResult result) {
		switch (adjusterForm.getAction()) {
			case WebConstant.ACTION_DELETE:
				return delete(adjusterForm, locale, result);
			case WebConstant.ACTION_NEW:
				return newAdjuster(model);
			default:
				return "redirect:/secured";
		}
	}
	
	private String delete(AdjusterForm adjusterForm, Locale locale, BindingResult result) {
		validateRecordForDeletion(adjusterForm, result, locale);
		if (!result.hasErrors()) {
			adjusterService.postDeleteAdjuster(adjusterForm.getSelected());
		}
		return "secured.setup.adjuster";
	}

	private void validateRecordForDeletion(AdjusterForm adjusterForm, BindingResult result, Locale locale) {
		AdjusterValidator validator = new AdjusterValidator(adjusterService);
		validator.validateDelete(adjusterForm, result);
	}

	private String newAdjuster(Model model) {
		AdjusterForm adjusterForm = new AdjusterForm();
		AdjusterDTO adjusterDTO = new AdjusterDTO();
		adjusterDTO.setActiveFlag(true);
		adjusterForm.setAdjusterDTO(adjusterDTO);
		adjusterForm.setStates(stateService.findAll());
		model.addAttribute("adjusterForm", adjusterForm);
		return "secured.setup.adjuster.new";
	}
	
	@PostMapping(value = "/new")
	public String formPostByNewAction(Locale locale, HttpServletRequest req, Model model, 
			@ModelAttribute("adjusterForm") AdjusterForm adjusterForm, BindingResult result) {
		switch (adjusterForm.getAction()) {
	        case WebConstant.ACTION_BACK:
	    		return "secured.setup.adjuster";	        	
	        case WebConstant.ACTION_SUBMIT:
	        	validateAdjusterForm(adjusterForm, result, locale);
	    		if (result.hasErrors()) {
	    			return "secured.setup.adjuster.new";
	    		} else {
	    			adjusterService.postAdjuster(adjusterForm.getAdjusterDTO(), adjusterForm.getContactDTO());
	    		}
	    		return "secured.setup.adjuster";
	        default:
	    		model.addAttribute("adjusterForm", adjusterForm);
    			return "secured.setup.adjuster.new";
		}
	}
	
	@GetMapping(value = "/edit/" + UrlPattern.PARAM_PREFIX + "/{id}")
	public String getAdjusterEdit(@PathVariable("id") Long id, Model model) {
		AdjusterDTO adjusterDTO = adjusterService.findAdjusterById(id);
		if (adjusterDTO != null) {
			AdjusterForm adjusterForm = new AdjusterForm();
			adjusterForm.setAdjusterDTO(adjusterDTO);
			adjusterForm.setContactDTO(adjusterDTO.getContactDTO());
			adjusterForm.setStates(stateService.findAll());
			model.addAttribute("adjusterForm", adjusterForm);
			return "secured.setup.adjuster.edit";
		} else {
			
		}
		return "secured.setup.adjuster";
	}
	
	@PostMapping(value = "/edit")
	public String formPostByEditAction(Locale locale, HttpServletRequest req, Model model, 
			@ModelAttribute("adjusterForm") AdjusterForm adjusterForm, BindingResult result) {
		switch (adjusterForm.getAction()) {
	        case WebConstant.ACTION_BACK:
	    		return "secured.setup.adjuster";	        	
	        case WebConstant.ACTION_SUBMIT:
	        	validateAdjusterForm(adjusterForm, result, locale);
	    		if (result.hasErrors()) {
	    			return "secured.setup.adjuster.edit";
	    		} else {
	    			adjusterService.postEditAdjuster(adjusterForm.getAdjusterDTO(), adjusterForm.getContactDTO());
	    		}
	    		return "secured.setup.adjuster";
	        default:
	    		model.addAttribute("adjusterForm", adjusterForm);
    			return "secured.setup.adjuster.edit";
		}
	}
	
	private void validateAdjusterForm(AdjusterForm adjusterForm, BindingResult result, Locale locale) {
		AdjusterValidator validator = new AdjusterValidator(adjusterService);
		validator.validate(adjusterForm, result);
	}
}
