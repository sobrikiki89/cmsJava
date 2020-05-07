package app.core.usermgmt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import app.core.annotation.Menu;
import app.core.annotation.MenuItem;
import app.core.annotation.Permission;
import app.core.registry.Module;
import app.core.usermgmt.model.Function;
import app.core.usermgmt.model.Role;
import app.core.usermgmt.model.RoleFunction;
import app.core.usermgmt.model.RoleFunctionPK;
import app.core.usermgmt.model.User;
import app.core.usermgmt.model.UserRole;
import app.core.usermgmt.model.UserRoleId;
import app.core.usermgmt.service.UserMgmtService;
import app.core.utils.AppConstant;
import app.core.utils.MD5Utils;

@Component("UserMgmtModule")
@Menu({ @MenuItem(id = 2000L, sortOrder = 20, isParent = true, parentId = AppConstant.MENU_HOME_ID, name = "User Management", description = "User Setup", function = ""),
		@MenuItem(id = 2100L, sortOrder = 10, isParent = true, parentId = 2000L, name = "User Management", description = "User Setup", function = ""),
		@MenuItem(id = 2110L, sortOrder = 10, isParent = false, parentId = 2100L, name = "User Listing", description = "User Listing", function = UserMgmtModule.FUNC_USER_LIST),
		@MenuItem(id = 2120L, sortOrder = 20, isParent = false, parentId = 2100L, name = "Role Listing", description = "Role Listing", function = UserMgmtModule.USER_ROLE_LIST),
		@MenuItem(id = 2130L, sortOrder = 30, isParent = false, parentId = 2100L, name = "Role Function Listing", description = "Role Function Listing", function = UserMgmtModule.FUNC_ROLE_FUNCTION_LIST),
		@MenuItem(id = 2200L, sortOrder = 20, isParent = false, parentId = 2000L, name = "Change Role", description = "Change Role", function = UserMgmtModule.FUNC_CHANGE_ROLE),
		@MenuItem(id = 2300L, sortOrder = 30, isParent = false, parentId = 2000L, name = "Change Password", description = "Change Password", function = UserMgmtModule.FUNC_CHANGE_PASSWORD)})
public class UserMgmtModule extends Module {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserMgmtModule.class);

	@Permission(name = "Secured Landing Page", path = "/secured")
	public static final String FUNC_LANDING = "CU00.LANDING";

	@Permission(name = "Change Role", path = "/secured/usermgmt/changerole")
	public static final String FUNC_CHANGE_ROLE = "CU01.CHANGE_ROLE";

	@Permission(name = "Change Password", path = "/secured/usermgmt/changepasswd")
	public static final String FUNC_CHANGE_PASSWORD = "CU01.CHANGE_PASSWORD";
	
	@Permission(name = "Role Function List", path = "/secured/usermgmt/rolefunction")
	public static final String FUNC_ROLE_FUNCTION_LIST = "CU01.ROLE_FUNCTION_LIST";

	@Permission(name = "Role Function Grid", path = "/secured/usermgmt/rolefunction/rolefunctiongrid")
	public static final String FUNC_ROLE_FUNCTION_GRID = "CU01.ROLE_FUNCTION_GRID";

	@Permission(name = "Role Function New", path = "/secured/usermgmt/rolefunction/new")
	public static final String FUNC_ROLE_FUNCTION_NEW = "CU01.ROLE_FUNCTION_NEW";

	@Permission(name = "Role Function Edit", path = "/secured/usermgmt/rolefunction/edit")
	public static final String FUNC_ROLE_FUNCTION_EDIT = "CU01.ROLE_FUNCTION_EDIT";

	@Permission(name = "User List", path = "/secured/usermgmt/user")
	public static final String FUNC_USER_LIST = "CU02.USER_LIST";

	@Permission(name = "User Grid", path = "/secured/usermgmt/user/usergrid")
	public static final String FUNC_USER_GRID = "CU02.USER_GRID";

	@Permission(name = "User New", path = "/secured/usermgmt/user/new")
	public static final String FUNC_USER_NEW = "CU02.USER_NEW";

	@Permission(name = "User Edit", path = "/secured/usermgmt/user/edit")
	public static final String FUNC_USER_EDIT = "CU02.USER_EDIT";
	// Chuyen changed here: "/secured/usermgmt/role/role"
	@Permission(name = "User Role List", path = "/secured/usermgmt/role")
	public static final String USER_ROLE_LIST = "CU04.USER_ROLE_LIST";

	@Permission(name = "User Role Grid", path = "/secured/usermgmt/role/rolegrid")
	public static final String USER_ROLE_GRID = "CU04.USER_ROLE_GRID";

	@Permission(name = "User Role New", path = "/secured/usermgmt/role/new")
	public static final String USER_ROLE_NEW = "CU04.USER_ROLE_NEW";

	@Permission(name = "User Role Edit", path = "/secured/usermgmt/role/edit")
	public static final String USER_ROLE_EDIT = "CU04.USER_ROLE_EDIT";

	@Permission(name = "User Role Change", path = "")
	public static final String USER_ROLE_CHANGE = "CU05.USER_ROLE_CHANGE";
	
	@Autowired
	private UserMgmtService userMgmtService;

	@Value("${usermgmt.default.admin.firstname}")
	private String defaultAdminFirstName;

	@Value("${usermgmt.default.admin.lastname}")
	private String defaultAdminLastName;

	@Value("${usermgmt.default.admin.username}")
	private String defaultAdminUsername;

	@Value("${usermgmt.default.admin.password}")
	private String defaultAdminPassword;

	@Value("${usermgmt.default.admin.passwordexpiryday}")
	private Long defaultAdminPasswordExpiryDay;

	@Value("${usermgmt.default.admin.maxinvalidlogincount}")
	private Long defaultAdminMaxInvalidLoginCount;

	@Value("${usermgmt.default.admin.sessiontimeoutinminute}")
	private Long defaultAdminSessionTimeoutInMinute;

	@Value("${usermgmt.default.admin.role}")
	private String defaultAdminRoleName;

	@Value("${usermgmt.default.admin.function.list}")
	private String defaultAdminFunctionList;

	@Override
	protected void init() throws Exception {
		// Initialization
		userMgmtService.initContextVarType();
		userMgmtService.resetConnectedFlag();

		// Create role
		Long roleId = -1L;
		if (!StringUtils.isEmpty(defaultAdminRoleName)) {
			Role role = new Role();
			role.setName(defaultAdminRoleName);
			role.setDescription(defaultAdminRoleName);
			role.setSortOrder(1L);
			roleId = userMgmtService.createRoleIfNotFound(role);
			LOGGER.info("Default system role created ? Role Id = " + roleId);
		}

		// Create role function
		if (roleId != -1L) {
			if (!StringUtils.isEmpty(defaultAdminFunctionList)) {
				String[] functionList = defaultAdminFunctionList.split(",");
				for (String function : functionList) {
					RoleFunctionPK roleFunctionPK = new RoleFunctionPK();
					roleFunctionPK.setFunctionCode(function);
					roleFunctionPK.setRoleId(roleId);
					RoleFunction roleFunction = new RoleFunction();
					roleFunction.setPk(roleFunctionPK);
					roleFunction.setFunction(new Function(function));
					roleFunction.setRole(new Role(roleId));
					roleFunction.setReadable(true);
					roleFunction.setCreateable(true);
					roleFunction.setUpdateable(true);
					roleFunction.setDeleteable(true);
					roleFunctionPK = userMgmtService.createRoleFunctionIfNotFound(roleFunction);
					if (roleFunctionPK != null) {
						LOGGER.info("Default system role function created. Role Id = " + roleFunctionPK.getRoleId()
								+ ", Function Code = " + roleFunctionPK.getFunctionCode());
					} else {
						LOGGER.info("Default system role function NOT created !");
					}
				}
			}
		}

		// Create user
		User user = new User();
		user.setUsername(defaultAdminUsername);
		String salt = MD5Utils.getNewSaltValue();
		user.setSalt(salt);
		user.setPassword(MD5Utils.encrypt(salt, defaultAdminPassword));
		user.setDateFormat("dd/MM/yyyy");
		user.setConnectedFlag(false);
		user.setActiveFlag(true);
		user.setFirstName(defaultAdminFirstName);
		user.setLastName(defaultAdminLastName);
		user.setForceChangePwdFlag(false);
		user.setPwdExpiryDays(defaultAdminPasswordExpiryDay);
		user.setInvalidLoginCount(0L);
		user.setMaxInvalidLoginCount(defaultAdminMaxInvalidLoginCount);
		user.setSessionTimeoutMinutes(defaultAdminSessionTimeoutInMinute);
		user.setCreateUserId(0L);
		user.setCreateDate(new Date());
		Long userId = userMgmtService.createUserIfNotFound(user);
		LOGGER.info("Default system user created ? User Id = " + userId);

		// Create user role
		if (roleId != -1L && userId != -1L) {
			UserRoleId userRolePK = new UserRoleId();
			userRolePK.setRoleId(roleId);
			userRolePK.setUserId(userId);
			UserRole userRole = new UserRole();
			userRole.setId(userRolePK);
			userRole.setRole(new Role(roleId));
			userRole.setUser(new User(userId));
			userRole.setStartDate(new Date());
			userRolePK = userMgmtService.createUserRoleIfNotFound(userRole);
			LOGGER.info("Default system user role created ? User Id = " + userRolePK.getUserId() + ", Role Id = "
					+ userRolePK.getRoleId());
		} else {
			LOGGER.info("Default system user role NOT created !");
		}
	}

	@Override
	public String getModuleName() {
		return "[CORE] User Management Module";
	}
}
