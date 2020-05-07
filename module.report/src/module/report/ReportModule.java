package module.report;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import app.core.usermgmt.model.Role;
import app.core.usermgmt.service.UserMgmtService;
import app.core.utils.AppConstant;
import module.report.annotation.OutputFormat;
import module.report.annotation.Report;
import module.report.annotation.ReportItem;
import module.report.model.GeneratorType;
import module.report.model.OutputFileFormat;
import module.report.model.ReportAccessControl;
import module.report.model.ReportCategory;
import module.report.model.ReportDefinition;
import module.report.service.ReportSetupService;

@Component("ReportModule")
@DependsOn({ "ClaimModule" })
@Report({
		@ReportItem(id = "1", name = "Policy Listing", category = "POLICY", generatorType = GeneratorType.JASPER, handlerClass = "module.report.handler.PolicyListingParamHandler", jspPath = "/WEB-INF/jsp/secured/report/include/policylisting.jsp", format = {
				@OutputFormat(outputFormat = OutputFileFormat.PDF, reportClass = "module/report/template/jasper/PolicyListingPDF.jasper"),
				@OutputFormat(outputFormat = OutputFileFormat.XLS, reportClass = "module/report/template/jasper/PolicyListing.jasper") }),
		@ReportItem(id = "2", name = "Claim Bordereaux", category = "CLAIM", generatorType = GeneratorType.BIRT, handlerClass = "module.report.handler.ClaimBordereauxParamHandler", jspPath = "/WEB-INF/jsp/secured/report/include/claimbordereaux.jsp", format = {
				@OutputFormat(outputFormat = OutputFileFormat.PDF, reportClass = "module/report/template/birt/ClaimBordereaux.rptdesign"),
				@OutputFormat(outputFormat = OutputFileFormat.XLS, reportClass = "module/report/template/birt/ClaimBordereaux.rptdesign") }),
		@ReportItem(id = "3", name = "Overall Claim  ( Notification Date ) Statistics", category = "CLAIM", generatorType = GeneratorType.JAVA, handlerClass = "module.report.handler.OverallClaimStatParamHandler", jspPath = "/WEB-INF/jsp/secured/report/include/overallclaimstat.jsp", format = {
				@OutputFormat(outputFormat = OutputFileFormat.XLS, reportClass = "module/report/template/java/OverallClaimStat.class") }),
		@ReportItem(id = "4", name = "Overall Claim ( Loss Date ) Statistics", category = "CLAIM", generatorType = GeneratorType.JAVA, handlerClass = "module.report.handler.OverallClaimStatLossParamHandler", jspPath = "/WEB-INF/jsp/secured/report/include/overallclaimstatloss.jsp", format = {
				@OutputFormat(outputFormat = OutputFileFormat.XLS, reportClass = "module/report/template/java/OverallClaimStatLoss.class") }),
		@ReportItem(id = "5", name = "Claim Aging Report", category = "CLAIM", generatorType = GeneratorType.BIRT, handlerClass = "module.report.handler.ClaimAgingParamHandler", jspPath = "/WEB-INF/jsp/secured/report/include/claimaging.jsp", format = {
				@OutputFormat(outputFormat = OutputFileFormat.XLS, reportClass = "module/report/template/birt/ClaimAging.rptdesign") }),
		@ReportItem(id = "6", name = "Premium Tabulation Details", category = "POLICY", generatorType = GeneratorType.JAVA, handlerClass = "module.report.handler.PremiumTabulationDetailsParamHandler", jspPath = "/WEB-INF/jsp/secured/report/include/premiumTabulationDetails.jsp", format = {
				@OutputFormat(outputFormat = OutputFileFormat.XLS, reportClass = "module/report/template/java/PremiumTabulationDetails.class") }),
		@ReportItem(id = "7", name = "Premium Tabulation Summary", category = "POLICY", generatorType = GeneratorType.JAVA, handlerClass = "module.report.handler.PremiumTabulationSummaryParamHandler", jspPath = "/WEB-INF/jsp/secured/report/include/premiumTabulationDetails.jsp", format = {
				@OutputFormat(outputFormat = OutputFileFormat.XLS, reportClass = "module/report/template/java/PremiumTabulationSummary.class") }),
		@ReportItem(id = "8", name = "Premium vs Claim", category = "POLICY", generatorType = GeneratorType.JAVA, handlerClass = "module.report.handler.PremiumVsClaimParamHandler", jspPath = "/WEB-INF/jsp/secured/report/include/premiumvsclaim.jsp", format = {
				@OutputFormat(outputFormat = OutputFileFormat.XLS, reportClass = "module/report/template/java/PremiumVsClaim.class") }),
		@ReportItem(id = "9", name = "Claim Summary", category = "CLAIM", generatorType = GeneratorType.JAVA, handlerClass = "module.report.handler.ClaimSummaryParamHandler", jspPath = "/WEB-INF/jsp/secured/report/include/claimsummary.jsp", format = {
				@OutputFormat(outputFormat = OutputFileFormat.XLS, reportClass = "module/report/template/java/ClaimSummary.class") }),
		@ReportItem(id = "10", name = "Claim By Cause of Incident", category = "CLAIM", generatorType = GeneratorType.BIRT, handlerClass = "module.report.handler.ClaimByLossTypeParamHandler", jspPath = "/WEB-INF/jsp/secured/report/include/claimbylosstype.jsp", format = {
				@OutputFormat(outputFormat = OutputFileFormat.XLS, reportClass = "module/report/template/birt/ClaimByLossType.rptdesign") }),
		@ReportItem(id = "11", name = "Claim By Class of Insurance", category = "CLAIM", generatorType = GeneratorType.BIRT, handlerClass = "module.report.handler.ClaimByClassOfInsuranceParamHandler", jspPath = "/WEB-INF/jsp/secured/report/include/claimbyclassofinsurance.jsp", format = {
				@OutputFormat(outputFormat = OutputFileFormat.XLS, reportClass = "module/report/template/birt/ClaimByClassOfInsurance.rptdesign") }),
		@ReportItem(id = "12", name = "Claim By Class of Insurance (Detail)", category = "CLAIM", generatorType = GeneratorType.BIRT, handlerClass = "module.report.handler.ClaimByClassOfInsuranceDetailParamHandler", jspPath = "/WEB-INF/jsp/secured/report/include/claimbyclassofinsuranceDetail.jsp", format = {
				@OutputFormat(outputFormat = OutputFileFormat.XLS, reportClass = "module/report/template/birt/ClaimByClassOfInsuranceDetail.rptdesign") }),
		// @ReportItem(id = "18", name = "Aging on Compilation of Supporting Documents", category = "CLAIM", generatorType = GeneratorType.BIRT, handlerClass = "module.report.handler.AgingDocumentParamHandler", jspPath = "/WEB-INF/jsp/secured/report/include/claimbyclassofinsuranceDetail.jsp", format = {
			//	@OutputFormat(outputFormat = OutputFileFormat.XLS, reportClass = "module/report/template/birt/AgingDocument.rptdesign") }),
		// @ReportItem(id = "19", name = "Aging on Broker-Insurance Performance", category = "CLAIM", generatorType = GeneratorType.BIRT, handlerClass = "module.report.handler.AgingBrokerParamHandler", jspPath = "/WEB-INF/jsp/secured/report/include/agingbroker.jsp", format = {
			//	@OutputFormat(outputFormat = OutputFileFormat.XLS, reportClass = "module/report/template/birt/AgingBroker.rptdesign") }),
		// @ReportItem(id = "20", name = "Aging on Overall Claim Processing", category = "CLAIM", generatorType = GeneratorType.BIRT, handlerClass = "module.report.handler.ClaimComparisonParamHandler", jspPath = "/WEB-INF/jsp/secured/report/include/claimcomparison.jsp", format = {
			//	@OutputFormat(outputFormat = OutputFileFormat.XLS, reportClass = "module/report/template/birt/ClaimComparison.rptdesign") }),
		})

@Menu({ @MenuItem(id = 6000L, sortOrder = 60, isParent = true, parentId = AppConstant.MENU_HOME_ID, name = "Report Management", description = "Report Management", function = ""),
		@MenuItem(id = 6100L, sortOrder = 10, isParent = false, parentId = 6000L, name = "Report Access Control", description = "Report Access Control", function = ReportModule.FUNC_REPORT_ACCESS_CONTROL_LIST),
		@MenuItem(id = 6200L, sortOrder = 20, isParent = false, parentId = 6000L, name = "Report Submission", description = "Report Submission", function = ReportModule.FUNC_REPORT_SUBMISSION_SEARCH) })
public class ReportModule extends Module {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportModule.class);

	@Permission(name = "Report Common - AJAX - Refresh Report", path = "/secured/report/common/refresh/reportdefinition")
	public static final String FUNC_REPORT_COMMON_AJAX_REFRESH_REPORT = "R01.REPORT_COMMON_AJAX_REFRESH_REPORT";

	@Permission(name = "Report Common - AJAX - Download Status", path = "/secured/report/common/download/status")
	public static final String FUNC_REPORT_COMMON_AJAX_DOWNLOAD_STATUS = "R03.REPORT_COMMON_AJAX_DOWNLOAD_STATUS";

	@Permission(name = "Report Common - AJAX - Download File", path = "/secured/report/common/download/file")
	public static final String FUNC_REPORT_COMMON_AJAX_DOWNLOAD_FILE = "R03.REPORT_COMMON_AJAX_DOWNLOAD_FILE";

	@Permission(name = "Report Access Control List", path = "/secured/report/access")
	public static final String FUNC_REPORT_ACCESS_CONTROL_LIST = "R02.REPORT_ACCESS_CONTROL_LIST";

	@Permission(name = "Report Access Control Grid", path = "/secured/report/access/accesscontrolgrid")
	public static final String FUNC_REPORT_ACCESS_CONTROL_GRID = "R02.REPORT_ACCESS_CONTROL_GRID";

	@Permission(name = "Report Access Control New", path = "/secured/report/access/new")
	public static final String FUNC_REPORT_ACCESS_CONTROL_NEW = "R02.REPORT_ACCESS_CONTROL_NEW";

	@Permission(name = "Report Access Control Edit", path = "/secured/report/access/edit")
	public static final String FUNC_REPORT_ACCESS_CONTROL_EDIT = "R02.REPORT_ACCESS_CONTROL_EDIT";

	@Permission(name = "Report Submission Search", path = "/secured/report/submission")
	public static final String FUNC_REPORT_SUBMISSION_SEARCH = "R03.REPORT_SUBMISSION_SEARCH";

	@Permission(name = "Report Submission New", path = "/secured/report/submission/new")
	public static final String FUNC_REPORT_SUBMISSION_NEW = "R03.REPORT_SUBMISSION_NEW";

	@Permission(name = "Report Submission Lookup", path = "/secured/report/submission/lookup/insuranceClass")
	public static final String FUNC_REPORT_SUBMISSION_LOOKUP = "R03.REPORT_SUBMISSION_LOOKUP";
	
	@Value("${report.category.list}")
	private String predefinedCategory;

	@Value("${report.access.list}")
	private String predefinedReportAccess;

	@Value("#{systemProperties['report.dir']}")
	private String reportDir;

	@Autowired
	private ReportSetupService reportSetupService;

	@Autowired
	private UserMgmtService userMgmtService;

	@Override
	protected void init() throws Exception {
		LOGGER.info("######################################## REPORT DIR ");
		LOGGER.info("REPORT DIR : [" + reportDir + "]");
		LOGGER.info("######################################## REPORT DIR ");
		// Initialization
		// Sync report category
		if (StringUtils.isNotEmpty(predefinedCategory)) {
			List<ReportCategory> categoryList = reportSetupService.getCategories();
			LOGGER.info("Total report category found in DB [" + categoryList.size() + "]");

			String[] categoryAry = predefinedCategory.split(",");
			LOGGER.info("Total pre-defined report category waiting to be synchronized [" + categoryAry.length + "]");

			String code;
			String name;
			Long idx = 1L;
			for (String categoryAryItem : categoryAry) {
				String[] categoryStr = categoryAryItem.split(":");
				code = StringUtils.defaultString(categoryStr[0]).trim();
				name = StringUtils.defaultString(categoryStr[1]).trim();

				// No report category found, insert new records
				if (categoryList == null || categoryList.isEmpty()) {
					ReportCategory category = new ReportCategory();
					category.setCode(code);
					category.setName(name);
					category.setSortOrder(idx++);
					reportSetupService.createCategory(category);
				} else {
					ReportCategory foundEntity = null;
					for (ReportCategory entity : categoryList) {
						if (entity.getCode().equals(code)) {
							foundEntity = entity;
						}
					}
					// Found entity
					if (foundEntity != null) {
						foundEntity.setName(name);
						foundEntity.setSortOrder(idx++);
						reportSetupService.updateCategory(foundEntity);
					} else {
						foundEntity = new ReportCategory();
						foundEntity.setCode(code);
						foundEntity.setName(name);
						foundEntity.setSortOrder(idx++);
						reportSetupService.createCategory(foundEntity);
					}
				}
			}
		}

		reportSetupService.updateReport(this);

		// Sync report access
		if (StringUtils.isNotEmpty(predefinedReportAccess)) {
			List<ReportAccessControl> accessControlList = reportSetupService.getReportAccessList();
			LOGGER.info("Total report access control found in DB [" + accessControlList.size() + "]");

			String[] reportAccessControlAry = predefinedReportAccess.split(",");
			LOGGER.info("Total pre-defined report access control waiting to be synchronized ["
					+ reportAccessControlAry.length + "]");

			String reportIdStr = "";
			String roleName = "";
			for (String reportAccessControlAryItem : reportAccessControlAry) {
				String[] reportAccessControlStr = reportAccessControlAryItem.split(":");
				reportIdStr = StringUtils.defaultString(reportAccessControlStr[0]).trim();
				roleName = StringUtils.defaultString(reportAccessControlStr[1]).trim();

				if (StringUtils.isNotBlank(reportIdStr)) {
					ReportDefinition definition = reportSetupService
							.getReportDefinitionById(Long.parseLong(reportIdStr));

					Role role = userMgmtService.getRoleByName(roleName);

					if (accessControlList == null || accessControlList.isEmpty()) {
						ReportAccessControl accessControl = new ReportAccessControl();
						accessControl.setDefinition(definition);
						accessControl.setRole(role);
						accessControl.setCreateUserId(0L);
						accessControl.setCreateDate(new Date());
						reportSetupService.createAccessControl(accessControl);
					} else {
						ReportAccessControl foundEntity = null;
						for (ReportAccessControl entity : accessControlList) {
							if (entity.getRole().getName().equals(roleName)
									&& entity.getDefinition().getId().equals(Long.parseLong(reportIdStr))) {
								foundEntity = entity;
							}
						}
						// Found entity
						if (foundEntity != null) {
							foundEntity.setDefinition(definition);
							foundEntity.setRole(role);
							foundEntity.setUpdateUserId(0L);
							foundEntity.setUpdateDate(new Date());
							reportSetupService.updateAccessControl(foundEntity);
						} else {
							foundEntity = new ReportAccessControl();
							foundEntity.setDefinition(definition);
							foundEntity.setRole(role);
							foundEntity.setCreateUserId(0L);
							foundEntity.setCreateDate(new Date());
							reportSetupService.createAccessControl(foundEntity);
						}
					}
				}
			}
		}
	}

	@Override
	public String getModuleName() {
		return "[MODULE] Report Module";
	}

	public String getReportDir() {
		return reportDir;
	}

	public void setReportDir(String reportDir) {
		this.reportDir = reportDir;
	}
}
