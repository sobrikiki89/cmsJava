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
import module.setup.dto.InsuranceClassDTO;
import module.setup.model.InsuranceClass;
import module.setup.model.InsuranceClassCategory;
import module.setup.service.InsuranceClassService;
import web.core.controller.BaseController;
import web.core.helper.WebConstant;
import web.module.setup.model.InsuranceClassForm;
import web.module.setup.validator.InsuranceClassEditFormValidator;
import web.module.setup.validator.InsuranceClassNewFormValidator;

@Controller
@RequestMapping("/secured/setup/insuranceclass")
@SessionAttributes({ "insuranceClassForm" })
public class InsuranceClassController extends BaseController {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(InsuranceClassController.class);

	@Autowired
	private InsuranceClassService insuranceClassService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getInsuranceClassList(Locale locale, HttpServletRequest req, Model model) {
		model.addAttribute("insuranceClassForm", new InsuranceClassForm());
		return "secured.setup.insuranceclass";
	}

	@RequestMapping(value = "/insuranceclassgrid", method = RequestMethod.GET)
	public @ResponseBody DatatablesResponse<InsuranceClassDTO> getInsuranceClassGrid(
			@DatatablesParams DatatablesCriterias criterias) {
		try {
			DataSet<InsuranceClassDTO> dataSet = insuranceClassService.getInsuranceClasses(criterias);
			return DatatablesResponse.build(dataSet, criterias);
		} catch (BaseApplicationException e) {
			LOGGER.error("Error on getting insurance class grid listing", e);
		}
		return null;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String insuranceClassPostByAction(Locale locale, HttpServletRequest req, HttpServletResponse resp,
			Model model, @ModelAttribute("insuranceClassForm") InsuranceClassForm insuranceClassForm,
			BindingResult result) {
		// We will determine the action
		if (WebConstant.ACTION_NEW.equalsIgnoreCase(insuranceClassForm.getAction())) {
			insuranceClassForm = new InsuranceClassForm();
			insuranceClassForm.setCategories(insuranceClassService.getCategories());
			model.addAttribute("insuranceClassForm", insuranceClassForm);
			return "secured.setup.insuranceclass.new";
		}

		else if (WebConstant.ACTION_BACK.equalsIgnoreCase(insuranceClassForm.getAction())) {
			return "redirect:/secured";
		}

		else if (WebConstant.ACTION_DELETE.equalsIgnoreCase(insuranceClassForm.getAction())) {
			if (insuranceClassForm.getSelected() != null) {
				for (String code : insuranceClassForm.getSelected()) {
					InsuranceClass insClass = insuranceClassService.getInsuranceClass(code);
					try {
						insuranceClassService.deleteObject(code);
					} catch (Exception e) {
						result.reject("error.inuse.delete", new Object[] { "Insurance Class", insClass.getCode() },
								"errors");
					}
				}
			} else {
				result.reject("error.required.deleteRecord");
			}
		}

		return "secured.setup.insuranceclass";
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String saveInsuranceClassNew(Model model,
			@ModelAttribute("insuranceClassForm") InsuranceClassForm insuranceClassForm, BindingResult result) {
		InsuranceClassNewFormValidator validator = new InsuranceClassNewFormValidator(insuranceClassService);
		validator.validate(insuranceClassForm, result);

		if (result.hasErrors()) {
			return "secured.setup.insuranceclass.new";
		} else {
			insuranceClassService.createInsuranceClass(insuranceClassForm.getInsuranceClass());
			return "secured.setup.insuranceclass";
		}
	}

	@RequestMapping(value = "/edit/" + UrlPattern.PARAM_PREFIX + "/{insuranceClassCode}", method = RequestMethod.GET)
	public String getInsuranceClassEdit(@PathVariable("insuranceClassCode") String insuranceClassCode, Model model) {
		InsuranceClass insuranceClass = insuranceClassService.getInsuranceClass(insuranceClassCode);
		if (insuranceClass != null) {
			InsuranceClassForm insuranceClassForm = new InsuranceClassForm();
			insuranceClassForm.setInsuranceClass(insuranceClass);
			if (insuranceClass.getCategory() != null) {
				insuranceClassForm.setCategoryCode(insuranceClass.getCategory().getCode());
			}
			insuranceClassForm.setCategories(insuranceClassService.getCategories());
			model.addAttribute("insuranceClassForm", insuranceClassForm);
			return "secured.setup.insuranceclass.edit";
		}
		return "secured.setup.insuranceclass";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String saveInsuranceClassEdit(Model model,
			@ModelAttribute("insuranceClassForm") InsuranceClassForm insuranceClassForm, BindingResult result) {
		InsuranceClassEditFormValidator validator = new InsuranceClassEditFormValidator(insuranceClassService);
		validator.validate(insuranceClassForm, result);
		if (result.hasErrors()) {
			return "secured.setup.insuranceclass.edit";
		} else {
			InsuranceClass insuranceClass = insuranceClassForm.getInsuranceClass();
			InsuranceClassCategory category = insuranceClassService.getClassCategoryByCode(insuranceClassForm.getCategoryCode());
			insuranceClass.setCategory(category);
			insuranceClass = insuranceClassService.updateInsuranceClass(insuranceClass);
			insuranceClassForm.setInsuranceClass(insuranceClass);
			return "secured.setup.insuranceclass";
		}
	}
}
