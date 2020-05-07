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
import module.setup.dto.SolicitorDTO;
import module.setup.service.SolicitorService;
import module.setup.service.StateService;
import web.core.controller.BaseController;
import web.core.helper.WebConstant;
import web.module.setup.model.SolicitorForm;
import web.module.setup.validator.SolicitorValidator;

@Controller
@RequestMapping("/secured/setup/solicitor")
@SessionAttributes({ "solicitorForm" })
public class SolicitorController extends BaseController {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(SolicitorController.class);

	@Autowired
	private SolicitorService solicitorService;

	@Autowired
	private StateService stateService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	}
	
	@GetMapping()
	public ModelAndView getSolicitorList() {
		ModelAndView modelView = new ModelAndView("secured.setup.solicitor");
		modelView.addObject("solicitorForm", new SolicitorForm());
		return modelView;
	}

	@GetMapping(value = "/grid")
	public @ResponseBody DatatablesResponse<SolicitorDTO> getSolicitorGrid(
			@DatatablesParams DatatablesCriterias criterias) {
		try {
			DataSet<SolicitorDTO> dataSet = solicitorService.findSolicitorBy(criterias);
			return DatatablesResponse.build(dataSet, criterias);
		} catch (BaseApplicationException e) {
			LOGGER.error("Error on getting type of loss grid listing", e);
		}
		return null;
	}

	@PostMapping()
	public String formPostByAction(Locale locale, HttpServletRequest req, Model model, 
			@ModelAttribute("solicitorForm") SolicitorForm solicitorForm, BindingResult result) {
		switch (solicitorForm.getAction()) {
			case WebConstant.ACTION_DELETE:
				return delete(solicitorForm, locale, result);
			case WebConstant.ACTION_NEW:
				return newSolicitor(model);
			default:
				return "redirect:/secured";
		}
	}

	private String delete(SolicitorForm solicitorForm, Locale locale, BindingResult result) {
		validateRecordForDeletion(solicitorForm, result, locale);
		if (!result.hasErrors()) {
			solicitorService.postDeleteSolicitor(solicitorForm.getSelected());
		}
		return "secured.setup.solicitor";
	}

	private void validateRecordForDeletion(SolicitorForm solicitorForm, BindingResult result, Locale locale) {
		SolicitorValidator validator = new SolicitorValidator(solicitorService);
		validator.validateDelete(solicitorForm, result);
	}

	private String newSolicitor(Model model) {
		SolicitorForm solicitorForm = new SolicitorForm();
		SolicitorDTO solicitorDTO = new SolicitorDTO();
		solicitorDTO.setActiveFlag(true);
		solicitorForm.setSolicitorDTO(solicitorDTO);
		solicitorForm.setStates(stateService.findAll());
		model.addAttribute("solicitorForm", solicitorForm);
		return "secured.setup.solicitor.new";
	}
	
	@PostMapping(value = "/new")
	public String formPostByNewAction(Locale locale, HttpServletRequest req, Model model, 
			@ModelAttribute("solicitorForm") SolicitorForm solicitorForm, BindingResult result) {
		switch (solicitorForm.getAction()) {
	        case WebConstant.ACTION_BACK:
	    		return "secured.setup.solicitor";	        	
	        case WebConstant.ACTION_SUBMIT:
	        	validateSolicitorForm(solicitorForm, result, locale);
	    		if (result.hasErrors()) {
	    			return "secured.setup.solicitor.new";
	    		} else {
	    			solicitorService.postSolicitor(solicitorForm.getSolicitorDTO(), solicitorForm.getContactDTO());
	    		}
	    		return "secured.setup.solicitor";
	        default:
	    		model.addAttribute("solicitorForm", solicitorForm);
    			return "secured.setup.solicitor.new";
		}
	}
	
	@GetMapping(value = "/edit/" + UrlPattern.PARAM_PREFIX + "/{id}")
	public String getSolicitorEdit(@PathVariable("id") Long id, Model model) {
		SolicitorDTO solicitorDTO = solicitorService.findSolicitorById(id);
		if (solicitorDTO != null) {
			SolicitorForm solicitorForm = new SolicitorForm();
			solicitorForm.setSolicitorDTO(solicitorDTO);
			solicitorForm.setContactDTO(solicitorDTO.getContactDTO());
			solicitorForm.setStates(stateService.findAll());
			model.addAttribute("solicitorForm", solicitorForm);
			return "secured.setup.solicitor.edit";
		} else {
			
		}
		return "secured.setup.solicitor";
	}
	
	@PostMapping(value = "/edit")
	public String formPostByEditAction(Locale locale, HttpServletRequest req, Model model, 
			@ModelAttribute("solicitorForm") SolicitorForm solicitorForm, BindingResult result) {
		switch (solicitorForm.getAction()) {
	        case WebConstant.ACTION_BACK:
	    		return "secured.setup.solicitor";	        	
	        case WebConstant.ACTION_SUBMIT:
	        	validateSolicitorForm(solicitorForm, result, locale);
	    		if (result.hasErrors()) {
	    			return "secured.setup.solicitor.edit";
	    		} else {
	    			solicitorService.postEditSolicitor(solicitorForm.getSolicitorDTO(), solicitorForm.getContactDTO());
	    		}
	    		return "secured.setup.solicitor";
	        default:
	    		model.addAttribute("solicitorForm", solicitorForm);
    			return "secured.setup.solicitor.edit";
		}
	}
	
	private void validateSolicitorForm(SolicitorForm solicitorForm, BindingResult result, Locale locale) {
		SolicitorValidator validator = new SolicitorValidator(solicitorService);
		validator.validate(solicitorForm, result);
	}
}
