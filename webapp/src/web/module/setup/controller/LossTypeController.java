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
import module.setup.dto.LossTypeDTO;
import module.setup.model.LossType;
import module.setup.service.LossTypeService;
import web.core.controller.BaseController;
import web.core.helper.WebConstant;
import web.module.setup.model.LossTypeForm;
import web.module.setup.validator.LossTypeEditFormValidator;
import web.module.setup.validator.LossTypeNewFormValidator;

@Controller
@RequestMapping("/secured/setup/losstype")
@SessionAttributes({ "lossTypeForm" })
public class LossTypeController extends BaseController {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(LossTypeController.class);

	@Autowired
	private LossTypeService lossTypeService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getLossTypeList(Locale locale, HttpServletRequest req, Model model) {
		model.addAttribute("lossTypeForm", new LossTypeForm());
		return "secured.setup.losstype";
	}

	@RequestMapping(value = "/losstypegrid", method = RequestMethod.GET)
	public @ResponseBody DatatablesResponse<LossTypeDTO> getLossTypeGrid(
			@DatatablesParams DatatablesCriterias criterias) {
		try {
			DataSet<LossTypeDTO> dataSet = lossTypeService.getLossTypes(criterias);
			return DatatablesResponse.build(dataSet, criterias);
		} catch (BaseApplicationException e) {
			LOGGER.error("Error on getting type of loss grid listing", e);
		}
		return null;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String lossTypePostByAction(Locale locale, HttpServletRequest req, HttpServletResponse resp, Model model,
			@ModelAttribute("lossTypeForm") LossTypeForm lossTypeForm, BindingResult result) {
		// We will determine the action
		if (WebConstant.ACTION_NEW.equalsIgnoreCase(lossTypeForm.getAction())) {
			model.addAttribute("lossTypeForm", new LossTypeForm());
			return "secured.setup.losstype.new";
		}

		else if (WebConstant.ACTION_BACK.equalsIgnoreCase(lossTypeForm.getAction())) {
			return "redirect:/secured";
		}

		else if (WebConstant.ACTION_DELETE.equalsIgnoreCase(lossTypeForm.getAction())) {
			if (lossTypeForm.getSelected() != null) {
				for (String code : lossTypeForm.getSelected()) {
					LossType lossType = lossTypeService.getLossType(code);
					try {
						lossTypeService.deleteObject(code);
					} catch (Exception e) {
						result.reject("error.inuse.delete",
								new Object[] { "Loss Type", lossType.getCode()}, "errors");
					}
				}
			} else {
				result.reject("error.required.deleteRecord");
			}
		}
		lossTypeForm.setSelected(null);
		return "secured.setup.losstype";
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String saveLossTypeNew(Model model, @ModelAttribute("lossTypeForm") LossTypeForm lossTypeForm,
			BindingResult result) {
		LossTypeNewFormValidator validator = new LossTypeNewFormValidator(lossTypeService);
		validator.validate(lossTypeForm, result);

		if (result.hasErrors()) {
			return "secured.setup.losstype.new";
		} else {
			lossTypeService.createLossType(lossTypeForm.getLossType());
			return "secured.setup.losstype";
		}
	}

	@RequestMapping(value = "/edit/" + UrlPattern.PARAM_PREFIX + "/{lossTypeCode}", method = RequestMethod.GET)
	public String getLossTypeEdit(@PathVariable("lossTypeCode") String lossTypeCode, Model model) {
		LossType lossType = lossTypeService.getLossType(lossTypeCode);
		if (lossType != null) {
			LossTypeForm lossTypeForm = new LossTypeForm();
			lossTypeForm.setLossType(lossType);
			model.addAttribute("lossTypeForm", lossTypeForm);
			return "secured.setup.losstype.edit";
		}
		return "secured.setup.losstype";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String saveLossTypeEdit(Model model, @ModelAttribute("lossTypeForm") LossTypeForm lossTypeForm,
			BindingResult result) {
		LossTypeEditFormValidator validator = new LossTypeEditFormValidator(lossTypeService);
		validator.validate(lossTypeForm, result);
		if (result.hasErrors()) {
			return "secured.setup.losstype.edit";
		} else {
			LossType lossType = lossTypeService.updateLossType(lossTypeForm.getLossType());
			lossTypeForm.setLossType(lossType);
			return "secured.setup.losstype";
		}
	}
}
