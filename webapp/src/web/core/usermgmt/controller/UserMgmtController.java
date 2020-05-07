package web.core.usermgmt.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
import app.core.security.UserPrincipal;
import app.core.service.FunctionService;
import app.core.service.MenuService;
import app.core.spring.UrlPattern;
import app.core.usermgmt.dto.RoleDTO;
import app.core.usermgmt.dto.RoleFunctionDTO;
import app.core.usermgmt.dto.UserDTO;
import app.core.usermgmt.model.Function;
import app.core.usermgmt.model.Role;
import app.core.usermgmt.model.RoleFunction;
import app.core.usermgmt.model.RoleFunctionPK;
import app.core.usermgmt.model.User;
import app.core.usermgmt.model.UserRole;
import app.core.usermgmt.service.UserMgmtService;
import app.core.utils.MD5Utils;
import web.core.controller.BaseController;
import web.core.helper.WebConstant;
import web.core.usermgmt.model.ChangePasswordForm;
import web.core.usermgmt.model.ChangeRoleEditForm;
import web.core.usermgmt.model.RoleForm;
import web.core.usermgmt.model.RoleFunctionForm;
import web.core.usermgmt.model.UserForm;
import web.core.usermgmt.validator.ChangePasswordFormValidator;
import web.core.usermgmt.validator.RoleEditValidator;
import web.core.usermgmt.validator.RoleFormValidator;
import web.core.usermgmt.validator.RoleFunctionFormValidator;
import web.core.usermgmt.validator.UserEditFormValidator;
import web.core.usermgmt.validator.UserNewFormValidator;

@Controller
@RequestMapping("/secured/usermgmt")
@SessionAttributes({ "userForm", "roleFunctionForm", "changeRoleEditForm", "roleForm" })
public class UserMgmtController extends BaseController {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(UserMgmtController.class);

	@Autowired
	private UserMgmtService userMgmtService;
	
	@Autowired
	private FunctionService functionService;
	
	@Autowired
	private MenuService menuService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	}

	// ------- user module ------
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public String getUserList(Locale locale, HttpServletRequest req, Model model) {
		model.addAttribute("userForm", new UserForm());
		return "secured.usermgmt.user";
	}

	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public String userPostByAction(Locale locale, HttpServletRequest req, HttpServletResponse resp, Model model,
			@ModelAttribute("userForm") UserForm userForm, BindingResult result) {
		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		Boolean checkedPermission = functionService.isAuthorizedByCode(principal.getCurrentRoleId(), "CU05.USER_ROLE_CHANGE");
		
		// We will determine the action
		if (WebConstant.ACTION_NEW.equalsIgnoreCase(userForm.getAction())) {
			// Getting list of role
			userForm.setUser(null);
			List<Role> roles = userMgmtService.getRoles();
			userForm.setRoles(roles);
			userForm.setAllowedChange(checkedPermission);
			return "secured.usermgmt.user.new";
		}

		if (WebConstant.ACTION_BACK.equalsIgnoreCase(userForm.getAction())) {
			return "redirect:/secured";
		}

		if (WebConstant.ACTION_DELETE.equalsIgnoreCase(userForm.getAction())) {
			// TODO : there is no delete function in user
		}
		return "secured.usermgmt.user";
	}

	@RequestMapping(value = "/user/usergrid", method = RequestMethod.GET)
	public @ResponseBody DatatablesResponse<UserDTO> getUserGrid(@DatatablesParams DatatablesCriterias criterias) {
		try {
			DataSet<UserDTO> dataSet = userMgmtService.getUser(criterias);
			return DatatablesResponse.build(dataSet, criterias);
		} catch (BaseApplicationException e) {
			logger.error("Error on getting user grid listing", e);
		}
		return null;
	}

	@RequestMapping(value = "/user/new", method = RequestMethod.POST)
	public String saveUserNew(Model model, @ModelAttribute("userForm") UserForm newForm, BindingResult result) {
		UserNewFormValidator validator = new UserNewFormValidator(userMgmtService);
		validator.validate(newForm, result);

		if (result.hasErrors()) {
			return "secured.usermgmt.user.new";
		} else {
			User user = newForm.getUser();
			String salt = MD5Utils.getNewSaltValue();
			user.setSalt(salt);
			user.setPassword(MD5Utils.encrypt(salt, user.getPassword()));
						
			Long userId = userMgmtService.createUser(user);

			if (newForm.getRoleIds() != null) {
				List<UserRole> userRoles = new ArrayList<UserRole>();
				for (Long roleId : newForm.getRoleIds()) {
					userRoles.add(new UserRole(userId, roleId));
				}
				user.setUserRoles(userRoles);
			}
			userMgmtService.updateUser(user);
			return "secured.usermgmt.user";
		}
	}

	@RequestMapping(value = "/user/edit/" + UrlPattern.PARAM_PREFIX + "/{userId}", method = RequestMethod.GET)
	public String getUserEdit(@PathVariable("userId") Long userId, Model model) {
		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		Boolean checkedPermission = functionService.isAuthorizedByCode(principal.getCurrentRoleId(), "CU05.USER_ROLE_CHANGE");
		
		// Getting selected user
		UserForm editForm = new UserForm();
		model.addAttribute("userForm", editForm);
		editForm.setAllowedChange(checkedPermission);
		User user = userMgmtService.getUser(userId);
		
		if (user != null) {
			
			List<Long> userRolesId = new ArrayList<Long>();
			for (UserRole userRole : user.getUserRoles()) {
				userRolesId.add(userRole.getRole().getId());
			}
			editForm.setRoleIds(userRolesId);
			
			// Getting list of role
			List<Role> roles = userMgmtService.getRoles();
			editForm.setRoles(roles);
			
			// Store the original password into temp password and then reset
			// password to null
			user.setTempPassword(user.getPassword());
			user.setPassword(null);
			editForm.setUser(user);
			return "secured.usermgmt.user.edit";
		} else {
			return "secured.usermgmt.user";
		}
	}

	@RequestMapping(value = "/user/edit", method = RequestMethod.POST)
	public String saveUserEdit(Model model, @ModelAttribute("userForm") UserForm editForm, BindingResult result) {
		UserEditFormValidator validator = new UserEditFormValidator();
		validator.validate(editForm, result);

		if (result.hasErrors()) {
			return "secured.usermgmt.user.edit";
		} else {
			User user = editForm.getUser();

			// Restore the password from temp password field
			if (!user.isPwdChanged()) {
				user.setPassword(user.getTempPassword());
			} else {
				String salt = MD5Utils.getNewSaltValue();
				user.setSalt(salt);
				user.setPassword(MD5Utils.encrypt(salt, user.getPassword()));
			}

			if (editForm.getAllowedChange()) {
				if (editForm.getRoleIds() != null) {
					List<UserRole> userRoles = new ArrayList<UserRole>();
					for (Long roleId : editForm.getRoleIds()) {
						userRoles.add(new UserRole(user.getId(), roleId));
					}
					user.setUserRoles(userRoles);
				}
			}
			
			userMgmtService.updateUser(user);
			return "secured.usermgmt.user";
		}
	}
	// ------- user module ------

	// ------- role function module ------
	@RequestMapping(value = "/rolefunction", method = RequestMethod.GET)
	public String getRoleFunctionList(Locale locale, HttpServletRequest req, Model model) {
		model.addAttribute("roleFunctionForm", new RoleFunctionForm());
		return "secured.usermgmt.rolefunction";
	}

	@RequestMapping(value = "/rolefunction", method = RequestMethod.POST)
	public String roleFunctionPostByAction(Locale locale, HttpServletRequest req, HttpServletResponse resp, Model model,
			@ModelAttribute("roleFunctionForm") RoleFunctionForm roleFunctionForm, BindingResult result) {

		// We will determine the action
		if (WebConstant.ACTION_NEW.equalsIgnoreCase(roleFunctionForm.getAction())) {
			// Getting list of role
			RoleFunction roleFunction = new RoleFunction();
			// get APP_ROLE
			List<Role> roles = userMgmtService.getRoles();
			// get APP_FUNCTION
			List<Function> functions = userMgmtService.getFunction();
			roleFunctionForm.setRoleFunction(roleFunction);
			roleFunctionForm.setRoles(roles);
			roleFunctionForm.setFunctions(functions);
			return "secured.usermgmt.rolefunction.new";
		}

		if (WebConstant.ACTION_BACK.equalsIgnoreCase(roleFunctionForm.getAction())) {
			return "redirect:/secured";
		}

		if (WebConstant.ACTION_DELETE.equalsIgnoreCase(roleFunctionForm.getAction())) {
			String[] selected = req.getParameterValues("selected");
			if (selected != null && selected.length > 0) {
				List<RoleFunction> roleFunctions = new ArrayList<RoleFunction>();
				for (String rf : selected) {
					RoleFunction roleFunction = new RoleFunction();
					RoleFunctionPK roleFunctionPK = new RoleFunctionPK();
					String[] codes = rf.split(":");
					roleFunctionPK.setRoleId(Long.parseLong(codes[0]));
					roleFunctionPK.setFunctionCode(codes[1]);
					roleFunction.setPk(roleFunctionPK);
					roleFunctions.add(roleFunction);
				}
				userMgmtService.deleteRoleFunction(roleFunctions);
			}
		}
		return "secured.usermgmt.rolefunction";
	}

	@RequestMapping(value = "/rolefunction/rolefunctiongrid", method = RequestMethod.GET)
	public @ResponseBody DatatablesResponse<RoleFunctionDTO> getRoleFunctionGrid(
			@DatatablesParams DatatablesCriterias criterias) {
		try {
			DataSet<RoleFunctionDTO> dataSet = userMgmtService.getRoleFunction(criterias);
			return DatatablesResponse.build(dataSet, criterias);
		} catch (BaseApplicationException e) {
			logger.error("Error on getting role function grid listing", e);
		}
		return null;
	}

	@RequestMapping(value = "/rolefunction/new", method = RequestMethod.POST)
	public String saveRoleFunctionNew(@ModelAttribute("roleFunctionForm") RoleFunctionForm roleFunctionForm,
			BindingResult result, Model model) {

		RoleFunctionFormValidator validator = new RoleFunctionFormValidator(userMgmtService);
		validator.validate(roleFunctionForm, result);
		RoleFunction roleFunction = roleFunctionForm.getRoleFunction();

		if (result.hasErrors()) {
			return "secured.usermgmt.rolefunction.edit";
		} else {
			RoleFunctionPK roleFunctionPK = new RoleFunctionPK();
			roleFunctionPK.setRoleId(roleFunction.getRole().getId());
			roleFunctionPK.setFunctionCode(roleFunction.getFunction().getCode());
			roleFunction.setPk(roleFunctionPK);
		}

		userMgmtService.createRoleFunction(roleFunction);
		return "secured.usermgmt.rolefunction";
	}

	@RequestMapping(value = "/rolefunction/edit/" + UrlPattern.PARAM_PREFIX
			+ "/{roleId}/{functionCode}", method = RequestMethod.GET)
	public String getRoleFunctionEdit(@PathVariable("roleId") Long roleId,
			@PathVariable("functionCode") String functionCode, Model model) {
		RoleFunctionForm roleFunctionForm = new RoleFunctionForm();
		model.addAttribute("roleFunctionForm", roleFunctionForm);

		RoleFunctionPK roleFunctionPk = new RoleFunctionPK();
		roleFunctionPk.setRoleId(roleId);
		roleFunctionPk.setFunctionCode(functionCode);

		RoleFunction roleFunction = userMgmtService.getRoleFuncEdit(roleFunctionPk);
		if (roleFunction != null) {
			roleFunctionForm.setRoleFunction(roleFunction);
			roleFunctionForm.setRoles(userMgmtService.getRoles());
			roleFunctionForm.setFunctions(userMgmtService.getFunction());
			return "secured.usermgmt.rolefunction.edit";
		} else {
			return "secured.usermgmt.rolefunction";
		}
	}

	@RequestMapping(value = "/rolefunction/edit", method = RequestMethod.POST)
	public String saveRoleFunctionEdit(Model model,
			@ModelAttribute("roleFunctionForm") RoleFunctionForm roleFunctionForm, BindingResult result) {
		RoleFunction roleFunction = roleFunctionForm.getRoleFunction();
		RoleFunctionPK roleFunctionPK = new RoleFunctionPK();
		roleFunctionPK.setRoleId(roleFunction.getRole().getId());
		roleFunctionPK.setFunctionCode(roleFunction.getFunction().getCode());
		roleFunction.setPk(roleFunctionPK); // roleFunction.getPk().clear();
		userMgmtService.updateRoleFunction(roleFunction);
		return "secured.usermgmt.rolefunction";
	}
	// ------- role function module ------

	// ------- role module ------
	@RequestMapping(value = "/changerole", method = RequestMethod.GET)
	public String getChangeRoleEdit(Locale locale, HttpServletRequest req, Model model) {
		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		List<Role> roles = userMgmtService.getRoleByUserId(principal.getUserId());
		ChangeRoleEditForm form = new ChangeRoleEditForm();
		form.setRoleId(principal.getCurrentRoleId());
		model.addAttribute("changeRoleEditForm", form);
		model.addAttribute("roles", roles);

		return "secured.usermgmt.changerole";
	}

	@RequestMapping(value = "/changerole", method = RequestMethod.POST)
	public String saveChangeRoleEdit(@ModelAttribute("changeRoleEditForm") ChangeRoleEditForm changeRoleEditForm,
			BindingResult result, Locale locale, HttpServletRequest req, Model model) {
		if (logger.isDebugEnabled()) {
			logger.debug("Selected ROLE : [" + changeRoleEditForm.getRoleId() + "]");
		}

		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		User user = new User();
		user.setId(principal.getUserId());
		Role role = new Role();
		role.setId(changeRoleEditForm.getRoleId());
		userMgmtService.updateLastRoleId(user, role);

		// Update UserPrincipal
		principal.setCurrentRoleId(changeRoleEditForm.getRoleId());
		// Refresh menu
		menuService.getMenu(principal, locale);

		// Redirect here is important, else 405 error will happen when change
		// theme, please ask on this matter.
		return "redirect:/secured";
	}

	@RequestMapping(value = "/changepasswd", method = RequestMethod.GET)
	public String getChangePasswordEdit(Locale locale, HttpServletRequest req, Model model) {
		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		ChangePasswordForm form = new ChangePasswordForm();
		form.setUserId(principal.getUserId());
		model.addAttribute("changePasswordEditForm", form);

		return "secured.usermgmt.changepasswd";
	}

	@RequestMapping(value = "/changepasswd", method = RequestMethod.POST)
	public String saveChangePasswordEdit(
			@ModelAttribute("changePasswordEditForm") ChangePasswordForm changePasswordForm, BindingResult result,
			Locale locale, HttpServletRequest req, Model model) {

		if (logger.isDebugEnabled()) {
			logger.debug("Change password request on user id : [" + changePasswordForm.getUserId() + "]");
		}

		ChangePasswordFormValidator validator = new ChangePasswordFormValidator();
		validator.validate(changePasswordForm, result);

		if (result.hasErrors()) {
			return "secured.usermgmt.changepasswd";
		} else {
			UserPrincipal principal = UserPrincipal.class
					.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

			User user = new User();
			user.setId(principal.getUserId());
			String salt = MD5Utils.getNewSaltValue();
			user.setSalt(salt);
			user.setPassword(MD5Utils.encrypt(salt, changePasswordForm.getPassword()));
			userMgmtService.updatePassword(user);

			// Update UserPrincipal
			principal.setPassword(user.getPassword());
			principal.setSalt(user.getSalt());
		}

		// Redirect here is important, else 405 error will happen when change
		// theme, please ask on this matter.
		return "redirect:/secured";
	}

	@RequestMapping(value = "/role/rolegrid", method = RequestMethod.GET)
	public @ResponseBody DatatablesResponse<RoleDTO> getRoleGrid(@DatatablesParams DatatablesCriterias criterias) {
		try {
			DataSet<RoleDTO> dataSet = userMgmtService.getRole(criterias);
			return DatatablesResponse.build(dataSet, criterias);
		} catch (BaseApplicationException e) {
			logger.error("Error on getting role grid listing", e);
		}
		return null;
	}

	@RequestMapping(value = "/role", method = RequestMethod.GET)
	public String getRoleList(Locale locale, HttpServletRequest req, Model model) {
		model.addAttribute("roleForm", new RoleForm());
		return "secured.usermgmt.role";
	}

	@RequestMapping(value = "/role", method = RequestMethod.POST)
	public String rolePostByAction(Locale locale, HttpServletRequest req, HttpServletResponse resp, Model model,
			@ModelAttribute("roleForm") RoleForm roleForm, BindingResult result) {

		// We will determine the action
		if (WebConstant.ACTION_NEW.equalsIgnoreCase(roleForm.getAction())) {
			Role role = new Role();
			roleForm.setRole(role);
			return "secured.usermgmt.role.new";
		}

		if (WebConstant.ACTION_BACK.equalsIgnoreCase(roleForm.getAction())) {
			return "redirect:/secured";
		}
		return "secured.usermgmt.role";
	}

	@RequestMapping(value = "/role/new", method = RequestMethod.POST)
	public String saveRoleNew(@ModelAttribute("roleForm") RoleForm roleForm, BindingResult result, Model model) {

		RoleFormValidator validator = new RoleFormValidator(userMgmtService);
		validator.validate(roleForm, result);
		Role role = roleForm.getRole();

		if (result.hasErrors()) {
			return "secured.usermgmt.role.new";
		} else {
			userMgmtService.createRole(role);
		}
		return "secured.usermgmt.role";
	}

	@RequestMapping(value = "/role/edit/" + UrlPattern.PARAM_PREFIX + "/{id}", method = RequestMethod.GET)
	public String getRoleEdit(@PathVariable("id") Long roleId, Model model) {
		Role role = userMgmtService.getRole(roleId);
		RoleForm editForm = new RoleForm();
		model.addAttribute("roleForm", editForm);

		if (role != null) {
			editForm.setRole(role);
			return "secured.usermgmt.role.edit";
		} else {
			return "secured.usermgmt.role";
		}
	}

	@RequestMapping(value = "/role/edit", method = RequestMethod.POST)
	public String saveRoleEdit(Model model, @ModelAttribute("roleForm") RoleForm roleForm, BindingResult result) {
		RoleEditValidator validator = new RoleEditValidator(userMgmtService);
		validator.validate(roleForm, result);
		Role role = roleForm.getRole();
		if (result.hasErrors()) {
			return "secured.usermgmt.role.edit";
		} else {
			userMgmtService.updateRoleObject(role);
		}
		return "secured.usermgmt.role";
	}

	// ------- role module ------
}
