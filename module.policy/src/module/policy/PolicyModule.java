package module.policy;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import app.core.annotation.Menu;
import app.core.annotation.MenuItem;
import app.core.annotation.Permission;
import app.core.registry.Module;
import app.core.utils.AppConstant;

@Component("PolicyModule")
@DependsOn({ "SetupModule" })
@Menu({ @MenuItem(id = 4000L, sortOrder = 40, isParent = true, parentId = AppConstant.MENU_HOME_ID, name = "Policy Setup", description = "Policy Setup", function = PolicyModule.FUNC_POLICY_SETUP) })
public class PolicyModule extends Module {

	@Permission(name = "Policy Management - Setup", path = "/secured/policymgmt/setup")
	public static final String FUNC_POLICY_SETUP = "P01.POLICYMGMT_SETUP";

	@Permission(name = "Policy Management - Setup New", path = "/secured/policymgmt/setup/new")
	public static final String FUNC_POLICY_SETUP_NEW = "P01.POLICYMGMT_SETUP_NEW";

	@Permission(name = "Policy Management - Setup Edit", path = "/secured/policymgmt/setup/edit")
	public static final String FUNC_POLICY_SETUP_EDIT = "P01.POLICYMGMT_SETUP_EDIT";

	@Permission(name = "Policy Management - AJAX - Refresh Company", path = "/secured/policymgmt/setup/refresh/company")
	public static final String FUNC_POLICY_SETUP_AJAX_REFRESH_COMPANY = "P01.POLICYMGMT_SETUP_AJAX_REFRESH_COMPANY";

	@Permission(name = "Policy Management - Upload File", path = "/secured/policymgmt/setup/uploadfile")
	public static final String FUNC_POLICY_SETUP_UPLOAD_FILE = "P01.POLICYMGMT_SETUP_UPLOAD_FILE";

	@Permission(name = "Policy Management - Delete File", path = "/secured/policymgmt/setup/deletefile")
	public static final String FUNC_POLICY_SETUP_DELETE_FILE = "P01.POLICYMGMT_SETUP_DELETE_FILE";

	@Permission(name = "Policy Management - Download File", path = "/secured/policymgmt/setup/downloadfile")
	public static final String FUNC_POLICY_SETUP_DOWNLOAD_FILE = "P01.POLICYMGMT_SETUP_DOWNLOAD_FILE";

	@Permission(name = "Policy Management - Delete New File", path = "/secured/policymgmt/setup/deleteNewFile")
	public static final String FUNC_POLICY_SETUP_DELETE_NEW_FILE = "P01.POLICYMGMT_SETUP_DELETE_NEW_FILE";

	@Permission(name = "Policy Management - Edit Policy No", path = "")
	public static final String FUNC_POLICY_SETUP_EDIT_POLICY_NO= "P01.POLICYMGMT_SETUP_EDIT_POLICY_NO";
	
	@Permission(name = "Policy Management - Delete Endorsement", path = "")
	public static final String FUNC_POLICY_SETUP_DELETE_ENDORSEMENT= "P01.POLICYMGMT_SETUP_DELETE_ENDORSEMENT";
	
	@Override
	protected void init() throws Exception {
		// Initialization
	}

	@Override
	public String getModuleName() {
		return "[MODULE] Policy Management Module";
	}
}
