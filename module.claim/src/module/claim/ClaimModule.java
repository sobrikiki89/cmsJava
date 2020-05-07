package module.claim;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import app.core.annotation.Menu;
import app.core.annotation.MenuItem;
import app.core.annotation.Permission;
import app.core.registry.Module;
import app.core.utils.AppConstant;

@Component("ClaimModule")
@DependsOn({ "PolicyModule" })
@Menu({ @MenuItem(id = 5000L, sortOrder = 50, isParent = true, parentId = AppConstant.MENU_HOME_ID, name = "Claim Register", description = "Claim Register", function = ClaimModule.FUNC_CLAIM_SEARCH)})
public class ClaimModule extends Module {

	@Permission(name = "Claim - Search", path = "/secured/claim")
	public static final String FUNC_CLAIM_SEARCH = "C01.CLAIM_SEARCH";

	@Permission(name = "Claim - Policy Search", path = "/secured/claim/policysearch")
	public static final String FUNC_CLAIM_POLICY_SEARCH = "C01.CLAIM_POLICY_SEARCH";

	@Permission(name = "Claim - New Claim", path = "/secured/claim/new")
	public static final String FUNC_CLAIM_NEW = "C01.CLAIM_NEW";

	@Permission(name = "Claim - Edit Claim", path = "/secured/claim/edit")
	public static final String FUNC_CLAIM_EDIT = "C01.CLAIM_EDIT";

	@Permission(name = "Claim - Upload File", path = "/secured/claim/uploadfile")
	public static final String FUNC_CLAIM_UPLOAD_FILE = "C01.CLAIM_UPLOAD_FILE";
	
	@Permission(name = "Claim - Delete File", path = "/secured/claim/deletefile")
	public static final String FUNC_CLAIM_DELETE_FILE = "C01.CLAIM_DELETE_FILE";
	
	@Permission(name = "Claim - Download File", path = "/secured/claim/downloadfile")
	public static final String FUNC_CLAIM_DOWNLOAD_FILE = "C01.CLAIM_DOWNLOAD_FILE";
	
	@Permission(name = "Claim - Request Delete Claim", path = "/secured/claim/reqDelete")
	public static final String FUNC_CLAIM_REQ_DELETE = "C01.CLAIM_REQ_DELETE";
	
	@Permission(name = "Claim - Request Revert Deleted Claim", path = "/secured/claim/reqRevert")
	public static final String FUNC_CLAIM_REQ_REVERT = "C01.CLAIM_REQ_REVERT";
	
	@Permission(name = "Claim - Revert Deleted Claim", path = "/secured/claim/revert")
	public static final String FUNC_CLAIM_REVERT = "C01.CLAIM_REVERT";

	@Permission(name = "Claim - Delete Claim", path = "/secured/claim/delete")
	public static final String FUNC_CLAIM_DELETE = "C01.CLAIM_DELETE";

	@Permission(name = "Claim - Notification", path = "/secured/claim/notification")
	public static final String FUNC_NOTIFICATION = "C01.CLAIM_NOTIFICATION";
	
	@Permission(name = "Claim - Send Notification", path = "/secured/claim/notification/send")
	public static final String FUNC_NOTIFICATION_SEND = "C01.CLAIM_NOTIFICATION_SEND";

	@Permission(name = "Claim - Permission Revert", path = "")
	public static final String FUNC_PERMISSION_REVERT_CLAIM = "C02.PERMISSION_REVERT_CLAIM";

	@Permission(name = "Claim - Permission Delete", path = "")
	public static final String FUNC_PERMISSION_DELETE_CLAIM = "C02.PERMISSION_DELETE_CLAIM";

	@Override
	protected void init() throws Exception {
		// Initialization
	}

	@Override
	public String getModuleName() {
		return "[MODULE] Claim Management Module";
	}
}
