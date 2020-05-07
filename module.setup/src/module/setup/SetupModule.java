package module.setup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import app.core.annotation.Menu;
import app.core.annotation.MenuItem;
import app.core.annotation.Permission;
import app.core.registry.Module;
import app.core.utils.AppConstant;
import module.setup.model.Company;
import module.setup.service.CompanyService;
import module.setup.service.StateService;

@Component("SetupModule")
@DependsOn({ "MenuMgmtModule" })
@Menu({ @MenuItem(id = 3000L, sortOrder = 30, isParent = true, parentId = AppConstant.MENU_HOME_ID, name = "Setup", description = "Setup", function = ""),
		@MenuItem(id = 3100L, sortOrder = 10, isParent = false, parentId = 3000L, name = "Insurer Setup", description = "Insurer Setup", function = SetupModule.FUNC_SETUP_INSURER_LIST),
		@MenuItem(id = 3200L, sortOrder = 20, isParent = false, parentId = 3000L, name = "Insurance Class Setup", description = "Insurance Class Setup", function = SetupModule.FUNC_SETUP_INSURANCE_CLASS_LIST),
		@MenuItem(id = 3300L, sortOrder = 30, isParent = false, parentId = 3000L, name = "Type of Loss Setup", description = "Type of Loss Setup", function = SetupModule.FUNC_SETUP_LOSS_TYPE_LIST),
		@MenuItem(id = 3400L, sortOrder = 40, isParent = false, parentId = 3000L, name = "Company Setup", description = "Company Setup", function = SetupModule.FUNC_SETUP_COMPANY_LIST),
		@MenuItem(id = 3500L, sortOrder = 50, isParent = false, parentId = 3000L, name = "Solicitor Setup", description = "Solicitor Setup", function = SetupModule.FUNC_SETUP_SOLICITOR),
		@MenuItem(id = 3600L, sortOrder = 60, isParent = false, parentId = 3000L, name = "Adjuster Setup", description = "Adjuster Setup", function = SetupModule.FUNC_SETUP_ADJUSTER) })
public class SetupModule extends Module {

	private static final Logger LOGGER = LoggerFactory.getLogger(SetupModule.class);

	@Permission(name = "Insurer List", path = "/secured/setup/insurer")
	public static final String FUNC_SETUP_INSURER_LIST = "S01.INSURER_LIST";

	@Permission(name = "Insurer Grid", path = "/secured/setup/insurer/insurergrid")
	public static final String FUNC_SETUP_INSURER_GRID = "S01.INSURER_GRID";

	@Permission(name = "Insurer New", path = "/secured/setup/insurer/new")
	public static final String FUNC_SETUP_INSURER_NEW = "S01.INSURER_NEW";

	@Permission(name = "Insurer Edit", path = "/secured/setup/insurer/edit")
	public static final String FUNC_SETUP_INSURER_EDIT = "S01.INSURER_EDIT";

	@Permission(name = "Insurance Class List", path = "/secured/setup/insuranceclass")
	public static final String FUNC_SETUP_INSURANCE_CLASS_LIST = "S02.INSURANCE_CLASS_LIST";

	@Permission(name = "Insurance Class Grid", path = "/secured/setup/insuranceclass/insuranceclassgrid")
	public static final String FUNC_SETUP_INSURANCE_CLASS_GRID = "S02.INSURANCE_CLASS_GRID";

	@Permission(name = "Insurance Class New", path = "/secured/setup/insuranceclass/new")
	public static final String FUNC_SETUP_INSURANCE_CLASS_NEW = "S02.INSURANCE_CLASS_NEW";

	@Permission(name = "Insurance Class Edit", path = "/secured/setup/insuranceclass/edit")
	public static final String FUNC_SETUP_INSURANCE_CLASS_EDIT = "S02.INSURANCE_CLASS_EDIT";

	@Permission(name = "Type of Loss List", path = "/secured/setup/losstype")
	public static final String FUNC_SETUP_LOSS_TYPE_LIST = "S03.LOSS_TYPE_LIST";

	@Permission(name = "Type of Loss Grid", path = "/secured/setup/losstype/losstypegrid")
	public static final String FUNC_SETUP_LOSS_TYPE_GRID = "S03.LOSS_TYPE_GRID";

	@Permission(name = "Type of Loss New", path = "/secured/setup/losstype/new")
	public static final String FUNC_SETUP_LOSS_TYPE_NEW = "S03.LOSS_TYPE_NEW";

	@Permission(name = "Type of Loss Edit", path = "/secured/setup/losstype/edit")
	public static final String FUNC_SETUP_LOSS_TYPE_EDIT = "S03.LOSS_TYPE_EDIT";

	@Permission(name = "Company List", path = "/secured/setup/company")
	public static final String FUNC_SETUP_COMPANY_LIST = "S04.COMPANY_LIST";

	@Permission(name = "Company Grid", path = "/secured/setup/company/companygrid")
	public static final String FUNC_SETUP_COMPANY_GRID = "S04.COMPANY_GRID";

	@Permission(name = "Company New", path = "/secured/setup/company/new")
	public static final String FUNC_SETUP_COMPANY_NEW = "S04.COMPANY_NEW";

	@Permission(name = "Company Edit", path = "/secured/setup/company/edit")
	public static final String FUNC_SETUP_COMPANY_EDIT = "S04.COMPANY_EDIT";

	@Permission(name = "Company - User Grid", path = "/secured/setup/company/usergrid")
	public static final String FUNC_SETUP_COMPANY_USER_GRID = "S04.COMPANY_USER_GRID";

	@Permission(name = "Company - User Grid New", path = "/secured/setup/company/usergridnew")
	public static final String FUNC_SETUP_COMPANY_USER_GRID_NEW = "S04.COMPANY_USER_GRID_NEW";

	@Permission(name = "Company - User View", path = "/secured/setup/company/userview")
	public static final String FUNC_SETUP_COMPANY_USER_VIEW = "S04.COMPANY_USER_VIEW";

	@Permission(name = "Company - Department Edit", path = "/secured/setup/company/departmentedit")
	public static final String FUNC_SETUP_COMPANY_DEPARTMENT_EDIT = "S04.COMPANY_DEPARTMENT_EDIT";

	@Permission(name = "Company - Department Remove", path = "/secured/setup/company/departmentremove")
	public static final String FUNC_SETUP_COMPANY_DEPARTMENT_REMOVE = "S04.COMPANY_DEPARTMENT_REMOVE";

	@Permission(name = "Company - Department Save", path = "/secured/setup/company/departmentsave")
	public static final String FUNC_SETUP_COMPANY_DEPARTMENT_SAVE = "S04.COMPANY_DEPARTMENT_SAVE";

	@Permission(name = "Company - Department Grid", path = "/secured/setup/company/departmentgrid")
	public static final String FUNC_SETUP_COMPANY_DEPARTMENT_GRID = "S04.COMPANY_DEPARTMENT_GRID";

	@Permission(name = "Update User Company", path = "")
	public static final String FUNC_SETUP_UPDATE_UC = "S05.UPDATE_USER_COMPANY";

	@Permission(name = "Solicitor - List", path = "/secured/setup/solicitor")
	public static final String FUNC_SETUP_SOLICITOR = "S06.SOLICITOR_LIST";

	@Permission(name = "Solicitor - Grid", path = "/secured/setup/solicitor/grid")
	public static final String FUNC_SETUP_SOLICITOR_GRID = "S06.SOLICITOR_GRID";

	@Permission(name = "Solicitor - New", path = "/secured/setup/solicitor/new")
	public static final String FUNC_SETUP_SOLICITOR_NEW = "S06.SOLICITOR_NEW";

	@Permission(name = "Solicitor - Edit", path = "/secured/setup/solicitor/edit")
	public static final String FUNC_SETUP_SOLICITOR_EDIT = "S06.SOLICITOR_EDIT";

	@Permission(name = "Adjuster - List", path = "/secured/setup/adjuster")
	public static final String FUNC_SETUP_ADJUSTER = "S07.ADJUSTER_LIST";

	@Permission(name = "Adjuster - Grid", path = "/secured/setup/adjuster/grid")
	public static final String FUNC_SETUP_ADJUSTER_GRID = "S07.ADJUSTER_GRID";

	@Permission(name = "Adjuster - New", path = "/secured/setup/adjuster/new")
	public static final String FUNC_SETUP_ADJUSTER_NEW = "S07.ADJUSTER_NEW";

	@Permission(name = "Adjuster - Edit", path = "/secured/setup/adjuster/edit")
	public static final String FUNC_SETUP_ADJUSTER_EDIT = "S07.ADJUSTER_EDIT";

	@Value("${setup.company.sib.code}")
	private String sibCompanyCode;

	@Value("${setup.company.sib.name}")
	private String sibCompanyName;

	@Autowired
	private StateService stateService;

	@Autowired
	private CompanyService companyService;

	@Override
	protected void init() throws Exception {
		// Initialization
		stateService.initState();

		// Create company
		Company company = new Company();
		company.setCode(sibCompanyCode);
		company.setName(sibCompanyName);
		company.setActive(true);
		Long companyId = companyService.createDefaultCompany(company);
		LOGGER.info("Default system company created ? Company Id = " + companyId);
	}

	@Override
	public String getModuleName() {
		return "[MODULE] Master Data Setup Module";
	}
}
