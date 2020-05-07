package web.module.setup.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.github.dandelion.core.util.StringUtils;
import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.github.dandelion.datatables.core.ajax.DatatablesResponse;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import app.core.exception.BaseApplicationException;
import app.core.security.UserPrincipal;
import app.core.service.FunctionService;
import app.core.spring.UrlPattern;
import app.core.usermgmt.model.User;
import module.setup.dto.CompanyDTO;
import module.setup.dto.UserCompanyDTO;
import module.setup.dto.UserDTO;
import module.setup.model.Company;
import module.setup.model.CompanyDepartment;
import module.setup.model.UserCompany;
import module.setup.service.CompanyService;
import module.setup.service.StateService;
import web.core.controller.BaseController;
import web.core.helper.WebConstant;
import web.module.setup.model.CompanyForm;
import web.module.setup.model.DepartmentAjaxDTO;
import web.module.setup.validator.CompanyEditFormValidator;
import web.module.setup.validator.CompanyNewFormValidator;

@Controller
@RequestMapping("/secured/setup/company")
@SessionAttributes({ "companyForm" })
public class CompanyController extends BaseController {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyController.class);

	@Autowired
	private CompanyService companyService;

	@Autowired
	private StateService stateService;

	@Autowired
	private FunctionService functionService;

	@Autowired
	private MessageSource messageSource;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getCompanyList(Locale locale, HttpServletRequest req, Model model) {
		model.addAttribute("companyForm", new CompanyForm());
		return "secured.setup.company";
	}

	@RequestMapping(value = "/companygrid", method = RequestMethod.GET)
	public @ResponseBody DatatablesResponse<CompanyDTO> getCompanyGrid(@DatatablesParams DatatablesCriterias criterias) {
		try {
			DataSet<CompanyDTO> dataSet = companyService.getCompanies(criterias);
			return DatatablesResponse.build(dataSet, criterias);
		} catch (BaseApplicationException e) {
			LOGGER.error("Error on getting company grid listing", e);
		}
		return null;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String companyPostByAction(Locale locale, HttpServletRequest req, HttpServletResponse resp, Model model,
			@ModelAttribute("companyForm") CompanyForm companyForm, BindingResult result) {

		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		Boolean checkedPermission = functionService.isAuthorizedByCode(principal.getCurrentRoleId(),
				"S05.UPDATE_USER_COMPANY");

		// We will determine the action
		if (WebConstant.ACTION_NEW.equalsIgnoreCase(companyForm.getAction())) {
			companyForm.setCompany(new Company());
			companyForm.setStates(stateService.getStates());
			companyForm.setPermissionFlag(checkedPermission);
			companyForm.setUserDTO(companyService.getUserList());
			companyForm.getCompany().setActive(true);
			return "secured.setup.company.new";
		}

		else if (WebConstant.ACTION_BACK.equalsIgnoreCase(companyForm.getAction())) {
			return "redirect:/secured";
		}

		else if (WebConstant.ACTION_DELETE.equalsIgnoreCase(companyForm.getAction())) {
			if (companyForm.getSelected() != null) {
				for (Long id : companyForm.getSelected()) {
					Company company = companyService.getCompany(id);
					try {
						companyService.deleteObject(id);
					} catch (Exception e) {
						result.reject("error.inuse.delete", new Object[] { "Company", company.getName() }, "");
						companyForm.setSelected(null);
					}
				}
			} else {
				result.reject("error.required.deleteRecord");
			}
		}
		companyForm.setSelected(null);
		return "secured.setup.company";
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String saveCompanyNew(Model model, @ModelAttribute("companyForm") CompanyForm companyForm,
			BindingResult result) {
		CompanyNewFormValidator validator = new CompanyNewFormValidator(companyService);
		validator.validate(companyForm, result);
		if (result.hasErrors()) {
			return "secured.setup.company.new";
		} else {
			List<UserCompany> uc = new ArrayList<UserCompany>();
			if (companyForm.getNewUser() != null) {
				for (Long userId : companyForm.getNewUser()) {
					User user = companyService.getUserById(userId);
					if (user != null) {
						UserCompany userCompany = new UserCompany();
						userCompany.setCompanyId(companyForm.getCompany());
						userCompany.setUserId(user);
						uc.add(userCompany);
					}
				}
			}
			Long compId = companyService.createCompany(companyForm.getCompany(), uc, companyForm.getDepartments());
			if (compId == null) {
				result.reject("error.save", new Object[] { "Company, ", companyForm.getCompany().getName() }, "error");
				return "secured.setup.company.new";
			}
			return "secured.setup.company";
		}
	}

	@RequestMapping(value = "/edit/" + UrlPattern.PARAM_PREFIX + "/{companyId}", method = RequestMethod.GET)
	public String getCompanyEdit(@PathVariable("companyId") Long companyId, Model model) {
		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		Boolean checkedPermission = functionService.isAuthorizedByCode(principal.getCurrentRoleId(),
				"S05.UPDATE_USER_COMPANY");
		Company company = companyService.getCompany(companyId);

		if (company != null) {
			CompanyForm companyForm = new CompanyForm();
			companyForm.setStates(stateService.getStates());
			companyForm.setCompany(company);
			companyForm.setPermissionFlag(checkedPermission);
			companyForm.setSelectedUser(companyService.getUserListById(companyId));
			companyForm.setUserDTO(companyService.getUserListEdit(company));
			companyForm.setDepartments(companyService.getCompanyDepartment(company));
			model.addAttribute("companyForm", companyForm);
			return "secured.setup.company.edit";
		}
		return "secured.setup.company";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String saveCompanyEdit(Model model, @ModelAttribute("companyForm") CompanyForm companyForm,
			BindingResult result) {
		CompanyEditFormValidator validator = new CompanyEditFormValidator(companyService);
		validator.validate(companyForm, result);
		if (result.hasErrors()) {
			companyForm.setStates(stateService.getStates());
			return "secured.setup.company.edit";
		} else {
			List<UserCompany> uc = new ArrayList<UserCompany>();
			if (companyForm.getNewUser() != null) {
				for (Long userId : companyForm.getNewUser()) {
					User user = companyService.getUserById(userId);
					if (user != null) {
						UserCompany userCompany = new UserCompany();
						userCompany.setUserId(user);
						uc.add(userCompany);
					}
				}
			}

			try {
				Company company = companyService.updateCompany(companyForm.getCompany(), uc,
						companyForm.getDepartments());
				companyForm.setCompany(company);
				return "secured.setup.company";
			} catch (Exception e) {
				companyForm.setStates(stateService.getStates());
				LOGGER.error("Unable to save company", e);
				result.reject("setup.company.updateErrorDueToDeletedDepartment",
						new Object[] { companyForm.getCompany().getName() }, "");
				return "secured.setup.company.edit";
			}
		}
	}

	@RequestMapping(value = "/usergridnew", method = RequestMethod.GET)
	public @ResponseBody DatatablesResponse<UserDTO> getUserGridNew(@DatatablesParams DatatablesCriterias criterias) {
		try {
			DataSet<UserDTO> dataSet = companyService.getUserGridNew(criterias);
			return DatatablesResponse.build(dataSet, criterias);
		} catch (BaseApplicationException e) {
			LOGGER.error("Error in getUserGridEdit", e);
		}
		return null;
	}

	@RequestMapping(value = "/userview/" + UrlPattern.PARAM_PREFIX + "/{id}", method = RequestMethod.GET)
	public @ResponseBody List<UserCompanyDTO> getInsurerList(@PathVariable("id") Long id, Locale locale,
			HttpServletRequest req, HttpServletResponse response, Model model,
			@ModelAttribute("excessForm") CompanyForm excessForm, BindingResult result) {
		List<UserCompanyDTO> insList = companyService.getUserCompanyList(id);
		return insList;
	}

	@RequestMapping(value = "/departmentedit", method = RequestMethod.POST)
	public @ResponseBody DepartmentAjaxDTO editDepartment(@RequestBody DepartmentAjaxDTO req, Locale locale,
			@ModelAttribute("companyForm") CompanyForm companyForm) {
		DepartmentAjaxDTO dto = new DepartmentAjaxDTO();

		String code = req.getCode();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("retrieve department with code [" + code + "]");
		}

		if (!StringUtils.isBlank(code) && companyForm.getDepartments() != null) {
			for (CompanyDepartment department : companyForm.getDepartments()) {
				if (code.equals(department.getCode())) {
					dto.setCode(code);
					dto.setName(department.getName());
					break;
				}
			}
		}

		return dto;
	}

	@RequestMapping(value = "/departmentremove", method = RequestMethod.POST)
	public @ResponseBody String removeDepartment(@RequestBody DepartmentAjaxDTO req, Locale locale,
			@ModelAttribute("companyForm") CompanyForm companyForm) {
		String code = req.getCode();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("delete department with code [" + code + "]");
		}

		if (!StringUtils.isBlank(code) && companyForm.getDepartments() != null) {
			for (CompanyDepartment department : companyForm.getDepartments()) {
				if (code.equals(department.getCode())) {
					break;
				}
			}

			Iterables.removeIf(companyForm.getDepartments(), new Predicate<CompanyDepartment>() {
				@Override
				public boolean apply(CompanyDepartment item) {
					if (code.equals(item.getCode())) {
						return true;
					}
					return false;
				}
			});
		}

		return "";
	}

	@RequestMapping(value = "/departmentsave", method = RequestMethod.POST)
	public @ResponseBody DepartmentAjaxDTO saveDepartment(@RequestBody DepartmentAjaxDTO req, Locale locale,
			@ModelAttribute("companyForm") CompanyForm companyForm) {
		DepartmentAjaxDTO dto = new DepartmentAjaxDTO();

		// Validate to ensure code and name is not empty
		String code = req.getCode();
		String name = req.getName();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("departmentCode [" + code + "], departmentName [" + name + "]");
		}

		if (StringUtils.isBlank(code) || StringUtils.isBlank(name)) {
			dto.setStatus(messageSource.getMessage("setup.company.departmentCodeNameRequired", null, locale));
		} else {
			if (companyForm.getDepartments() == null) {
				companyForm.setDepartments(new ArrayList<CompanyDepartment>());
			}

			boolean found = false;
			for (CompanyDepartment entity : companyForm.getDepartments()) {
				if (code.equals(entity.getCode())) {
					found = true;
					entity.setName(name);
				}
			}
			if (!found) {
				CompanyDepartment department = new CompanyDepartment();
				department.setCode(code);
				department.setName(name);
				companyForm.getDepartments().add(department);
			}
		}

		return dto;
	}

	@RequestMapping(value = "/departmentgrid", method = RequestMethod.GET)
	public @ResponseBody DatatablesResponse<DepartmentAjaxDTO> getDepartmentList(
			@ModelAttribute("companyForm") CompanyForm companyForm, @DatatablesParams DatatablesCriterias criterias) {
		List<DepartmentAjaxDTO> dtoList = new ArrayList<>();
		if (companyForm.getDepartments() != null) {
			dtoList = Lists.newArrayList(Iterables.transform(companyForm.getDepartments(),
					new Function<CompanyDepartment, DepartmentAjaxDTO>() {
						@Override
						public DepartmentAjaxDTO apply(CompanyDepartment obj) {
							DepartmentAjaxDTO dto = new DepartmentAjaxDTO();
							dto.setCode(obj.getCode());
							dto.setName(obj.getName());
							return dto;
						}
					}));
		}
		DataSet<DepartmentAjaxDTO> dataSet = new DataSet<>(dtoList, (long) dtoList.size(), (long) dtoList.size());
		return DatatablesResponse.build(dataSet, criterias);
	}
}
