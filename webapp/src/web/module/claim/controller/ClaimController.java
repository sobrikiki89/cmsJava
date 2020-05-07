package web.module.claim.controller;

import java.math.BigDecimal;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.dandelion.core.util.StringUtils;

import app.core.security.UserPrincipal;
import app.core.service.FunctionService;
import app.core.spring.UrlPattern;
import app.core.utils.AppConstant;
import module.claim.ClaimModule;
import module.claim.dto.ClaimSearchCriteria;
import module.claim.dto.ClaimSearchDTO;
import module.claim.model.Claim;
import module.claim.model.ClaimNotificationEmail;
import module.claim.model.ClaimRemark;
import module.claim.model.ClaimStatusEnum;
import module.claim.service.ClaimNotificationService;
import module.claim.service.ClaimSearchService;
import module.claim.service.ClaimService;
import module.notification.constant.NotificationConstant;
import module.notification.model.NotificationType;
import module.notification.object.JMSMessage;
import module.notification.object.JMSMessageContent;
import module.notification.object.NotificationTransaction;
import module.notification.service.JMSMessageService;
import module.policy.dto.PolicyRelatedClaimDTO;
import module.policy.dto.PolicySearchCriteria;
import module.policy.model.Policy;
import module.policy.service.PolicySearchService;
import module.policy.service.PolicyService;
import module.setup.model.CompanyDepartment;
import module.setup.model.LossType;
import module.setup.model.UserCompany;
import module.setup.service.AdjusterService;
import module.setup.service.CompanyService;
import module.setup.service.InsuranceClassService;
import module.setup.service.InsurerService;
import module.setup.service.LossTypeService;
import module.setup.service.SetupConstant;
import module.setup.service.SolicitorService;
import module.upload.model.UploadedFile;
import module.upload.model.UploadedFileCategory;
import module.upload.service.UploadedFileService;
import web.core.controller.BaseController;
import web.core.helper.WebConstant;
import web.module.claim.model.ClaimDeleteForm;
import web.module.claim.model.ClaimNotificationForm;
import web.module.claim.model.ClaimSearchForm;
import web.module.claim.model.ClaimSetupForm;
import web.module.claim.model.ClaimStatusEnumConverter;
import web.module.claim.validator.ClaimFormValidator;
import web.module.claim.validator.ClaimNotificationFormValidator;

@Controller
@RequestMapping("/secured/claim")
@SessionAttributes({ "claimSearchForm", "claimDeleteForm" })
public class ClaimController extends BaseController {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ClaimController.class);

	@Autowired
	private InsurerService insurerService;

	@Autowired
	private InsuranceClassService insuranceClassService;

	@Autowired
	private ClaimSearchService claimSearchService;

	@Autowired
	private ClaimService claimService;

	@Autowired
	private ClaimNotificationService claimNotificationService;

	@Autowired
	private PolicyService policyService;

	@Autowired
	private PolicySearchService policySearchService;

	@Autowired
	private LossTypeService lossTypeService;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private UploadedFileService uploadService;

	@Autowired
	private FunctionService functionService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private JMSMessageService jmsMessageService;
	
	@Autowired
	private SolicitorService solicitorService;
	
	@Autowired
	private AdjusterService adjusterService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.DATE_FORMAT);
		SimpleDateFormat sdfFullTime = new SimpleDateFormat(AppConstant.DATE_TIME_FORMAT);
		binder.registerCustomEditor(Date.class, "criteria.startDate", new CustomDateEditor(sdf, true));
		binder.registerCustomEditor(Date.class, "criteria.endDate", new CustomDateEditor(sdf, true));
		binder.registerCustomEditor(Date.class, "criteria.fromLossDate", new CustomDateEditor(sdf, true));
		binder.registerCustomEditor(Date.class, "criteria.toLossDate", new CustomDateEditor(sdf, true));
		binder.registerCustomEditor(Date.class, "claim.approvalDate", new CustomDateEditor(sdf, true));
		binder.registerCustomEditor(Date.class, "claim.notifyDate", new CustomDateEditor(sdf, true));
		binder.registerCustomEditor(Date.class, "claim.paidDate", new CustomDateEditor(sdf, true));
		binder.registerCustomEditor(Date.class, "claim.lossDate", new CustomDateEditor(sdf, true));
		binder.registerCustomEditor(Date.class, "claim.docCompletionDate", new CustomDateEditor(sdf, true));
		binder.registerCustomEditor(Date.class, "claim.adjFinalReportDate", new CustomDateEditor(sdf, true));
		binder.registerCustomEditor(Date.class, new CustomDateEditor(sdfFullTime, true));

		DecimalFormat decimalFormat = new DecimalFormat();
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(',');
		decimalFormat.setDecimalFormatSymbols(symbols);
		decimalFormat.setMaximumFractionDigits(2);
		binder.registerCustomEditor(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, decimalFormat, true));

		binder.registerCustomEditor(ClaimStatusEnum.class, new ClaimStatusEnumConverter());
	}

	List<UploadedFile> files = new ArrayList<>();

	@GetMapping()
	public String getClaimSearch(Locale locale, HttpServletRequest req, Model model) {
		newSearch(model);
		return "secured.claim.search";
	}

	private void newSearch(Model model) {
		ClaimSearchForm claimSearchForm = new ClaimSearchForm();
		claimSearchForm.setCriteria(new ClaimSearchCriteria());
		claimSearchForm.setCompanies(companyService.getAllCompanies());
		claimSearchForm.setInsurers(insurerService.getAllInsurers());
		claimSearchForm.setInsuranceClasses(insuranceClassService.getInsuranceClasses());
		claimSearchForm.setSolicitors(solicitorService.getAllSolicitors());
		claimSearchForm.setAdjusters(adjusterService.getAllAdjusters());
		claimSearchForm.setStatuses(ClaimStatusEnum.values());
		files = new ArrayList<>();

		UserPrincipal principal = UserPrincipal.class.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		claimSearchForm.setHasRevertPermission(functionService.isAuthorizedByCode(principal.getCurrentRoleId(),
				ClaimModule.FUNC_PERMISSION_REVERT_CLAIM));
		claimSearchForm.setHasDeletePermission(functionService.isAuthorizedByCode(principal.getCurrentRoleId(),
				ClaimModule.FUNC_PERMISSION_DELETE_CLAIM));

		model.addAttribute("claimSearchForm", claimSearchForm);
		model.addAttribute("claimDeleteForm", new ClaimDeleteForm());
	}

	@PostMapping()
	public String formPostFromSearchScreen(Model model, @ModelAttribute("claimSearchForm") ClaimSearchForm claimSearchForm,
			BindingResult result) {
		switch(claimSearchForm.getAction()) {
		 	case WebConstant.ACTION_BACK:
		 		return "redirect:/secured";
	        case WebConstant.ACTION_SEARCH:
	        	validateUserCompany(result);
	        	if (result.hasErrors()) {
	        		return "secured.claim.search";
	        	}
	        	return searchClaim(claimSearchForm, result);
	        case WebConstant.ACTION_NEW:
	        	validateUserCompany(result);
	        	if (result.hasErrors()) {
	        		return "secured.claim.search";
	        	}
	        	return newClaim(model);
	        default:
	    		return "secured.claim.search";
		}
	}

	private String searchClaim(ClaimSearchForm claimSearchForm, BindingResult result) {
		claimSearchForm.setSearched(true);
		List<ClaimSearchDTO> claims = claimSearchService.searchClaim(claimSearchForm.getCriteria());
		claimSearchForm.setClaims(claims);
		return "secured.claim.search";
	}

	private String newClaim(Model model) {
		ClaimSetupForm claimSetupForm = new ClaimSetupForm();
		claimSetupForm.setPolicySearchCriteria(new PolicySearchCriteria());
		claimSetupForm.setCompanies(companyService.getAllCompanies());
		claimSetupForm.setInsurers(insurerService.getInsurers());
		claimSetupForm.setInsuranceClasses(insuranceClassService.getInsuranceClasses());
		List<UploadedFile> newFile = new ArrayList<UploadedFile>();
		this.files = newFile;
		model.addAttribute("claimSetupForm", claimSetupForm);
		return "secured.claim.new.policysearch"; //result.reject("claim.noUserCompany");
	}

	private void validateUserCompany(BindingResult result) {
		UserPrincipal principal = UserPrincipal.class.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		List<UserCompany> userCompany = claimSearchService.getUserCompany(principal);
		if (userCompany == null) {
			result.reject("claim.noUserCompany");
		}
	}

	@PostMapping(value = "/policysearch")
	public String searchPolicy(Model model, @ModelAttribute("claimSetupForm") ClaimSetupForm claimSetupForm,
			BindingResult result) {
		if (WebConstant.ACTION_BACK.equalsIgnoreCase(claimSetupForm.getAction())) {
			return "secured.claim.search";
		} else {
			claimSetupForm.setPolicySearched(true);
			claimSetupForm.setCompanies(companyService.getAllCompanies());
			claimSetupForm.setInsurers(insurerService.getInsurers());
			claimSetupForm.setInsuranceClasses(insuranceClassService.getInsuranceClasses());
			claimSetupForm.setPolicies(policySearchService.searchPolicy(claimSetupForm.getPolicySearchCriteria()));
			return "secured.claim.new.policysearch";
		}
	}

	@RequestMapping(value = "/new/" + UrlPattern.PARAM_PREFIX + "/{policyId}", method = RequestMethod.GET)
	public String getClaimNew(@PathVariable("policyId") Long policyId, Model model,
			@ModelAttribute("claimSetupForm") ClaimSetupForm claimSetupForm, BindingResult result) {

		if (policyId != null) {
			Policy policy = policyService.getPolicy(policyId);
			if (policy != null) {
				UserPrincipal principal = UserPrincipal.class
						.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
				List<UserCompany> userCompany = claimSearchService.getUserCompany(principal);
				if (policy.getCompany() == null && userCompany == null) {
					result.reject("claim.noPolicyFound");
					claimSetupForm.setPolicySearched(false);
					setupDropdownList(claimSetupForm);
					result.reject("claim.required.company");
					return "secured.claim.new.policysearch";
				}

				claimSetupForm.setClaim(new Claim());
				claimSetupForm.getClaim().setNotifyDate(new Date());
				claimSetupForm.getClaim().setStatus(ClaimStatusEnum.OPN);
				claimSetupForm.getClaim().setPolicy(policy);
				claimSetupForm.getClaim().setCompanyCode(claimSetupForm.getCompanyCode());
				claimSetupForm.setInsurers(insurerService.getInsurers());
				claimSetupForm.setInsuranceClasses(insuranceClassService.getInsuranceClasses());
				claimSetupForm.setStatuses(ClaimStatusEnum.values());
				claimSetupForm.setLossTypes(lossTypeService.getActiveLossTypes());
				claimSetupForm.setOtherPolicy(claimService.getOtherPolicy(policy, true));
				claimSetupForm.setDepartments(companyService.getCompanyDepartment(policy.getCompany()));
				return "secured.claim.new";
			}
		}

		// Negative case
		result.reject("claim.noPolicyFound");
		claimSetupForm.setPolicySearched(false);
		claimSetupForm.setCompanies(companyService.getAllCompanies());
		claimSetupForm.setInsurers(insurerService.getInsurers());
		claimSetupForm.setInsuranceClasses(insuranceClassService.getInsuranceClasses());
		claimSetupForm.setPolicies(policySearchService.searchPolicy(claimSetupForm.getPolicySearchCriteria()));
		claimSetupForm.setOtherPolicy(claimService.getOtherPolicy(claimSetupForm.getClaim().getPolicy(), true));
		return "secured.claim.new.policysearch";
	}

	@PostMapping(value = "/new")
	public String formPostByNewAction(Model model, @ModelAttribute("claimSetupForm") ClaimSetupForm claimSetupForm, BindingResult result) {
		switch(claimSetupForm.getAction()) {
	 	case WebConstant.ACTION_BACK:
    		return "secured.claim.search";
        case WebConstant.ACTION_SUBMIT:
        	validateUserCompany(result);
        	if (result.hasErrors()) {
    			return "secured.claim.new";
        	}
        	return postNewClaim(model, claimSetupForm, result);
        case "add":
			if (claimSetupForm.getRelatedPolicyRef3Flag().equalsIgnoreCase("RP3")) {
				claimSetupForm.setRelatedPolicyRef4Flag("RP4");
			} else if (claimSetupForm.getRelatedPolicyRef2Flag().equalsIgnoreCase("RP2")) {
				claimSetupForm.setRelatedPolicyRef3Flag("RP3");
			} else {
				claimSetupForm.setRelatedPolicyRef2Flag("RP2");
			}
			return "secured.claim.new";
        default:
    		return "secured.claim.search";
		}
	}

	private String postNewClaim(Model model, ClaimSetupForm claimSetupForm, BindingResult result) {
		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		ClaimFormValidator validator = new ClaimFormValidator(claimService);
		validator.validate(claimSetupForm, result);
		setupDropdownList(claimSetupForm);
		
		if (LOGGER.isDebugEnabled()) {
			if (claimSetupForm.getClaim().getRemarks() != null) {
				LOGGER.debug("### NEW : Claim Remark List");
				for (ClaimRemark item : claimSetupForm.getClaim().getRemarks()) {
					LOGGER.debug("### order : [" + item.getOrder() + "]");
					LOGGER.debug("### remark : [" + item.getRemark() + "]");
					LOGGER.debug("### updated by : [" + item.getUpdateUser() + "]");
					LOGGER.debug("### updated on : [" + item.getUpdateDate() + "]");
				}
			}
		}

		if (result.hasErrors()) {
			this.files = new ArrayList<UploadedFile>();
			return "secured.claim.new";
		} else {
			claimSetupForm.getClaim().setDeleted(false);
			claimSetupForm.getClaim().setDeleteApproval(false);
			claimSetupForm.getClaim().setCompanyCode(principal.getCompanyCode());
			constructClaimRelation(claimSetupForm);
			Long claimId = claimService.createClaim(claimSetupForm.getClaim());
			if (claimId != null) {
				Claim claim = claimService.getClaim(claimId);
				saveFile(claim);
				claimSetupForm.setFilesList(claimService.getUploadedFile(claim));
				result.reject("success.create", new Object[] { "Claim", claim.getClaimNo() }, null);
				claimSetupForm.setOtherPolicy(claimService.getOtherPolicy(claim.getPolicy(), true));
				claimSetupForm.setClaim(claim);
				claimSetupForm.setCompanyCode(claim.getCompanyCode());
				if (claim.getPolicy() != null) {
					claimSetupForm.setDepartments(companyService.getCompanyDepartment(claim.getPolicy().getCompany()));
				}
				if (claimSetupForm.getClaim().getRelatedPolicy() != null
						&& claimSetupForm.getClaim().getRelatedPolicy().size() > 0) {
					if (claimSetupForm.getClaim().getRelatedPolicy().size() == 2) {
						claimSetupForm.setRelatedPolicyRef2Flag("RP2");
						claimSetupForm.setRelatedPolicyRef3Flag("");
						claimSetupForm.setRelatedPolicyRef4Flag("");
					} else if (claimSetupForm.getClaim().getRelatedPolicy().size() == 3) {
						claimSetupForm.setRelatedPolicyRef2Flag("RP2");
						claimSetupForm.setRelatedPolicyRef3Flag("RP3");
						claimSetupForm.setRelatedPolicyRef4Flag("");
					} else if (claimSetupForm.getClaim().getRelatedPolicy().size() == 4) {
						claimSetupForm.setRelatedPolicyRef2Flag("RP2");
						claimSetupForm.setRelatedPolicyRef3Flag("RP3");
						claimSetupForm.setRelatedPolicyRef4Flag("RP4");
					}
				}

				// Check the current user is SIB user or not
				List<UserCompany> userCompany = companyService.getUserCompany(principal);
				if (userCompany != null) {

					List<PolicyRelatedClaimDTO> otherPolicies = claimService.getOtherPolicy(claim.getPolicy(),false);
					// Prepare label for all disabled dropdown
					if (claimSetupForm.getClaim().getRelatedPolicy() != null
							&& !claimSetupForm.getClaim().getRelatedPolicy().isEmpty()) {
						if (claimSetupForm.getClaim().getRelatedPolicy().size() == 4) {
							claimSetupForm.setDdRelatedPolicyRef4(getDropdownLabel(otherPolicies,
									claimSetupForm.getClaim().getRelatedPolicy().get(3).getPolicyNo()));
						}
						if (claimSetupForm.getClaim().getRelatedPolicy().size() >= 3) {
							claimSetupForm.setDdRelatedPolicyRef3(getDropdownLabel(otherPolicies,
									claimSetupForm.getClaim().getRelatedPolicy().get(2).getPolicyNo()));
						}
						if (claimSetupForm.getClaim().getRelatedPolicy().size() >= 2) {
							claimSetupForm.setDdRelatedPolicyRef2(getDropdownLabel(otherPolicies,
									claimSetupForm.getClaim().getRelatedPolicy().get(1).getPolicyNo()));
						}
						if (claimSetupForm.getClaim().getRelatedPolicy().size() >= 1) {
							claimSetupForm.setDdRelatedPolicyRef1(getDropdownLabel(otherPolicies,
									claimSetupForm.getClaim().getRelatedPolicy().get(0).getPolicyNo()));
						}
					}

					if (claimSetupForm.getClaim().getDepartment() != null) {
						claimSetupForm.setDdDepartment(getDropdownLabel(claimSetupForm.getDepartments(),
								claimSetupForm.getClaim().getDepartment().getId()));
					}

					if (claimSetupForm.getClaim().getLossType() != null) {
						claimSetupForm.setDdLossType(getLossTypeDropdownLabel(claimSetupForm.getLossTypes(),
								claimSetupForm.getClaim().getLossType().getCode()));
					}
				} else {
					claimSetupForm.setSibUser(false);
				}

				ClaimSearchForm claimSearchForm = (ClaimSearchForm) model.asMap().get("claimSearchForm");
				if (claimSearchForm != null) {
					claimSearchForm.setAction("search");
				}

				return "secured.claim.edit";
			} else {
				ClaimSearchForm searchForm = (ClaimSearchForm) model.asMap().get("claimSearchForm");
				searchForm.setAction(null);
				result.reject("error.save", new Object[] { "claim",
						", no company assigned for the selected policy or the current user" }, "error");
				return "secured.claim.new";
			}
		}
	}

	private void constructClaimRelation(ClaimSetupForm claimSetupForm) {
		if (claimSetupForm.getClaim().getAdjuster().getId() == 0) {
			claimSetupForm.getClaim().setAdjuster(null);
		}
		
		if (claimSetupForm.getClaim().getSolicitor().getId() == 0) {
			claimSetupForm.getClaim().setSolicitor(null);
		}
	}

	private void saveFile(Claim claim) {
		if (files != null) {
			List<UploadedFile> attach = new ArrayList<UploadedFile>();
			for (UploadedFile uFile : files) {
				if (uFile.getId() == null) {
					attach.add(uFile);
				}
			}
			claimService.attachFile(claim, attach);
		}
		this.files = new ArrayList<UploadedFile>();
	}

	@GetMapping(value = "/edit/" + UrlPattern.PARAM_PREFIX + "/{claimId}")
	public String getClaimEdit(@PathVariable("claimId") Long claimId, Model model) {
		Claim claim = claimService.getClaim(claimId);
		if (claim != null) {
			ClaimSetupForm claimSetupForm = new ClaimSetupForm();
			claimSetupForm.setInsurers(insurerService.getInsurers());
			claimSetupForm.setInsuranceClasses(insuranceClassService.getInsuranceClasses());
			claimSetupForm.setStatuses(ClaimStatusEnum.values());
			claimSetupForm.setLossTypes(lossTypeService.getActiveLossTypes());
			claimSetupForm.setFilesList(claimService.getUploadedFile(claim));
			List<UploadedFile> uploadedFile = claimService.getUploadedFile(claim);
			this.files = new ArrayList<UploadedFile>();
			if (uploadedFile != null) {
				for (UploadedFile uFile : uploadedFile) {
					files.add(uFile);
				}
			}
			claimSetupForm.setOtherPolicy(claimService.getOtherPolicy(claim.getPolicy(), true));
			claimSetupForm.setClaim(claim);
			claimSetupForm.setCompanyCode(claim.getCompanyCode());
			if (claim.getPolicy() != null) {
				claimSetupForm.setDepartments(companyService.getCompanyDepartment(claim.getPolicy().getCompany()));
			}
			if (claimSetupForm.getClaim().getRelatedPolicy() != null
					&& claimSetupForm.getClaim().getRelatedPolicy().size() > 0) {
				if (claimSetupForm.getClaim().getRelatedPolicy().size() == 2) {
					claimSetupForm.setRelatedPolicyRef2Flag("RP2");
					claimSetupForm.setRelatedPolicyRef3Flag("");
					claimSetupForm.setRelatedPolicyRef4Flag("");
				} else if (claimSetupForm.getClaim().getRelatedPolicy().size() == 3) {
					claimSetupForm.setRelatedPolicyRef2Flag("RP2");
					claimSetupForm.setRelatedPolicyRef3Flag("RP3");
					claimSetupForm.setRelatedPolicyRef4Flag("");
				} else if (claimSetupForm.getClaim().getRelatedPolicy().size() == 4) {
					claimSetupForm.setRelatedPolicyRef2Flag("RP2");
					claimSetupForm.setRelatedPolicyRef3Flag("RP3");
					claimSetupForm.setRelatedPolicyRef4Flag("RP4");
				}
			}
			model.addAttribute("claimSetupForm", claimSetupForm);
			List<UploadedFile> newFile = new ArrayList<UploadedFile>();
			this.files = newFile;

			// Check the current user is SIB user or not
			UserPrincipal principal = UserPrincipal.class
					.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

			List<UserCompany> userCompany = companyService.getUserCompany(principal);
			if (userCompany != null) {

				List<PolicyRelatedClaimDTO> otherPolicies = claimService.getOtherPolicy(claim.getPolicy(), false);
				// Prepare label for all disabled dropdown
				if (claimSetupForm.getClaim().getRelatedPolicy() != null
						&& !claimSetupForm.getClaim().getRelatedPolicy().isEmpty()) {
					if (claimSetupForm.getClaim().getRelatedPolicy().size() == 4) {
						claimSetupForm.setDdRelatedPolicyRef4(getDropdownLabel(otherPolicies,
								claimSetupForm.getClaim().getRelatedPolicy().get(3).getPolicyNo()));
					}
					if (claimSetupForm.getClaim().getRelatedPolicy().size() >= 3) {
						claimSetupForm.setDdRelatedPolicyRef3(getDropdownLabel(otherPolicies,
								claimSetupForm.getClaim().getRelatedPolicy().get(2).getPolicyNo()));
					}
					if (claimSetupForm.getClaim().getRelatedPolicy().size() >= 2) {
						claimSetupForm.setDdRelatedPolicyRef2(getDropdownLabel(otherPolicies,
								claimSetupForm.getClaim().getRelatedPolicy().get(1).getPolicyNo()));
					}
					if (claimSetupForm.getClaim().getRelatedPolicy().size() >= 1) {
						claimSetupForm.setDdRelatedPolicyRef1(getDropdownLabel(otherPolicies,
								claimSetupForm.getClaim().getRelatedPolicy().get(0).getPolicyNo()));
					}
				}

				if (claimSetupForm.getClaim().getDepartment() != null) {
					claimSetupForm.setDdDepartment(getDropdownLabel(claimSetupForm.getDepartments(),
							claimSetupForm.getClaim().getDepartment().getId()));
				}

				if (claimSetupForm.getClaim().getLossType() != null) {
					claimSetupForm.setDdLossType(getLossTypeDropdownLabel(claimSetupForm.getLossTypes(),
							claimSetupForm.getClaim().getLossType().getCode()));
				}
			} else {
				claimSetupForm.setSibUser(false);
			}

			return "secured.claim.edit";
		}
		return "secured.claim.search";
	}
	
	@PostMapping(value = "/edit")
	public String formPostByEditAction(Model model, @ModelAttribute("claimSetupForm") ClaimSetupForm claimSetupForm,
			BindingResult result) {
		switch(claimSetupForm.getAction()) {
	 	case WebConstant.ACTION_BACK:
	 		//newSearch(model);
    		return "secured.claim.search";
        case WebConstant.ACTION_SUBMIT:
        	validateUserCompany(result);
        	if (result.hasErrors()) {
    			return "secured.claim.edit";
        	}
        	return postEditClaim(model, claimSetupForm, result);
        default:
    		return "secured.claim.search";
		}
	}

	private String postEditClaim(Model model, ClaimSetupForm claimSetupForm, BindingResult result) {
		ClaimFormValidator validator = new ClaimFormValidator(claimService);
		validator.validateEdit(claimSetupForm, result);

		if (LOGGER.isDebugEnabled()) {
			if (claimSetupForm.getClaim().getRemarks() != null) {
				LOGGER.debug("### EDIT : Claim Remark List");
				for (ClaimRemark item : claimSetupForm.getClaim().getRemarks()) {
					LOGGER.debug("### order : [" + item.getOrder() + "]");
					LOGGER.debug("### remark : [" + item.getRemark() + "]");
					LOGGER.debug("### updated by : [" + item.getUpdateUser() + "]");
					LOGGER.debug("### updated on : [" + item.getUpdateDate() + "]");
				}
			}
			if (files != null) {
				LOGGER.debug("### EDIT : Claim Attachment List");
				for (UploadedFile file : files) {
					LOGGER.debug("### file name : [" + file.getName() + "]");
					LOGGER.debug("### file size : [" + file.getFileSize() + "]");
				}
			}
		}

		setupDropdownList(claimSetupForm);

		// Check the current user is SIB user or not
		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		List<UserCompany> userCompany = companyService.getUserCompany(principal);
		if (userCompany != null) {

			List<PolicyRelatedClaimDTO> otherPolicies = claimService
					.getOtherPolicy(claimSetupForm.getClaim().getPolicy(), false);
			// Prepare label for all disabled dropdown
			if (claimSetupForm.getClaim().getRelatedPolicy() != null
					&& !claimSetupForm.getClaim().getRelatedPolicy().isEmpty()) {
				if (claimSetupForm.getClaim().getRelatedPolicy().size() == 4) {
					claimSetupForm.setDdRelatedPolicyRef4(getDropdownLabel(otherPolicies,
							claimSetupForm.getClaim().getRelatedPolicy().get(3).getPolicyNo()));
				}
				if (claimSetupForm.getClaim().getRelatedPolicy().size() >= 3) {
					claimSetupForm.setDdRelatedPolicyRef3(getDropdownLabel(otherPolicies,
							claimSetupForm.getClaim().getRelatedPolicy().get(2).getPolicyNo()));
				}
				if (claimSetupForm.getClaim().getRelatedPolicy().size() >= 2) {
					claimSetupForm.setDdRelatedPolicyRef2(getDropdownLabel(otherPolicies,
							claimSetupForm.getClaim().getRelatedPolicy().get(1).getPolicyNo()));
				}
				if (claimSetupForm.getClaim().getRelatedPolicy().size() >= 1) {
					claimSetupForm.setDdRelatedPolicyRef1(getDropdownLabel(otherPolicies,
							claimSetupForm.getClaim().getRelatedPolicy().get(0).getPolicyNo()));
				}
			}

			if (claimSetupForm.getClaim().getDepartment() != null) {
				claimSetupForm.setDdDepartment(getDropdownLabel(claimSetupForm.getDepartments(),
						claimSetupForm.getClaim().getDepartment().getId()));
			}

			if (claimSetupForm.getClaim().getLossType() != null) {
				claimSetupForm.setDdLossType(getLossTypeDropdownLabel(claimSetupForm.getLossTypes(),
						claimSetupForm.getClaim().getLossType().getCode()));
			}
		} else {
			claimSetupForm.setSibUser(false);
		}

		List<UploadedFile> attach = new ArrayList<UploadedFile>();
		if (files != null) {
			for (UploadedFile uFile : files) {
				if (uFile.getId() == null) {
					attach.add(uFile);
				}
			}
		} else {
			this.files = new ArrayList<UploadedFile>();
		}
		if ("add".equalsIgnoreCase(claimSetupForm.getAction())) {
			if (claimSetupForm.getRelatedPolicyRef3Flag().equalsIgnoreCase("RP3")) {
				claimSetupForm.setRelatedPolicyRef4Flag("RP4");
			} else if (claimSetupForm.getRelatedPolicyRef2Flag().equalsIgnoreCase("RP2")) {
				claimSetupForm.setRelatedPolicyRef3Flag("RP3");
			} else {
				claimSetupForm.setRelatedPolicyRef2Flag("RP2");
			}
		} else {
			if (!result.hasErrors()) {
				constructClaimRelation(claimSetupForm);
				claimService.updateClaim(claimSetupForm.getClaim());
				claimService.attachFile(claimSetupForm.getClaim(), attach);
				claimSetupForm.setFilesList(claimService.getUploadedFile(claimSetupForm.getClaim()));
				this.files = new ArrayList<UploadedFile>();
				ClaimSearchForm searchForm = (ClaimSearchForm) model.asMap().get("claimSearchForm");
				searchForm.setAction(null);
				result.reject("success.update", new Object[] { "Claim", claimSetupForm.getClaim().getClaimNo() }, null);
			}
		}
		return "secured.claim.edit";
	}

	private void setupDropdownList(ClaimSetupForm claimSetupForm) {
		claimSetupForm.setCompanies(companyService.getAllCompanies());
		claimSetupForm.setInsurers(insurerService.getInsurers());
		claimSetupForm.setInsuranceClasses(insuranceClassService.getInsuranceClasses());
		claimSetupForm.setStatuses(ClaimStatusEnum.values());
		claimSetupForm.setLossTypes(lossTypeService.getActiveLossTypes());
		claimSetupForm.setOtherPolicy(claimService.getOtherPolicy(claimSetupForm.getClaim().getPolicy(), true));
		claimSetupForm.setFilesList(claimService.getUploadedFile(claimSetupForm.getClaim()));
		claimSetupForm.setDepartments(companyService.getCompanyDepartment(claimSetupForm.getClaim().getPolicy().getCompany()));
	}

	@PostMapping(value = "/uploadfile")
	public @ResponseBody List<UploadedFile> attachmentHandler(Model model, HttpServletRequest request,
			@ModelAttribute("claimSetupForm") ClaimSetupForm claimSetupForm, BindingResult result) {

		try {
			if (request instanceof MultipartHttpServletRequest) {

				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				multipartRequest.getParameter("attachment");

				Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
				if (fileMap != null) {
					for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
						MultipartFile file = entry.getValue();

						UploadedFile uploadedFile = new UploadedFile();
						uploadedFile.setName(file.getOriginalFilename());
						uploadedFile.setFileSize(file.getSize());
						uploadedFile.setFileCategory(UploadedFileCategory.CLAIM.toString());
						uploadedFile.setMimeType(file.getContentType()); // application/*,
																			// image/*
						uploadedFile.setContent(file.getBytes());
						files.add(uploadedFile);
					}
				}
			} else {
				throw new IllegalArgumentException("Method argument request must not be null.");
			}
		} catch (Exception e) {
			LOGGER.error("attachmentHandler", e);
		}
		return files;
	}

	@ResponseBody
	@GetMapping(value = "/uploadedfile")
	private void showAttachment(Model model, @ModelAttribute("claimSetupForm") ClaimSetupForm claimSetupForm,
			HttpServletResponse response, HttpServletRequest request) {
		try {
			if (files != null) {
				for (UploadedFile file : files) {
					response.setContentType(file.getMimeType());
					response.getOutputStream().write(file.getContent());
				}
			} else {
				response.setContentType("image/jpg");
				response.getOutputStream();
			}
			response.getOutputStream().close();
		} catch (Exception e) {
			LOGGER.error("showAttachment", e);
		}
	}

	@GetMapping(value = "/deletefile/" + UrlPattern.PARAM_PREFIX + "/{id}")
	private String deletefile(@PathVariable("id") Long objId, HttpServletResponse response, Model model) {
		ClaimSetupForm claimSetupForm = new ClaimSetupForm();
		Claim claim = claimService.getClaimbyClaimFile(objId);
		if (claim != null) {
			claimSetupForm.setInsurers(insurerService.getInsurers());
			claimSetupForm.setInsuranceClasses(insuranceClassService.getInsuranceClasses());
			claimSetupForm.setStatuses(ClaimStatusEnum.values());
			claimSetupForm.setLossTypes(lossTypeService.getActiveLossTypes());
			claimService.deleteClaimFilebyFileId(objId);
			claimService.deleteUploadedFileById(objId);
			this.files = new ArrayList<UploadedFile>();
			List<UploadedFile> uploadedFile = claimService.getUploadedFile(claim);
			if (uploadedFile != null) {
				for (UploadedFile uFile : uploadedFile) {
					files.add(uFile);
				}
			}
			claimSetupForm.setOtherPolicy(claimService.getOtherPolicy(claim.getPolicy(), true));
			claimSetupForm.setFilesList(claimService.getUploadedFile(claim));
			claimSetupForm.setClaim(claim);
			claimSetupForm.getClaim().setRemarks(claimService.getClaimRemarks(claim));
			claimSetupForm.getClaim().setRelatedPolicy(claimService.getRelatedPolicy(claim));
			if (claimSetupForm.getClaim().getRelatedPolicy() != null
					&& claimSetupForm.getClaim().getRelatedPolicy().size() > 0) {
				if (claimSetupForm.getClaim().getRelatedPolicy().size() == 2) {
					claimSetupForm.setRelatedPolicyRef2Flag("RP2");
					claimSetupForm.setRelatedPolicyRef3Flag("");
					claimSetupForm.setRelatedPolicyRef4Flag("");
				} else if (claimSetupForm.getClaim().getRelatedPolicy().size() == 3) {
					claimSetupForm.setRelatedPolicyRef2Flag("RP2");
					claimSetupForm.setRelatedPolicyRef3Flag("RP3");
					claimSetupForm.setRelatedPolicyRef4Flag("");
				} else if (claimSetupForm.getClaim().getRelatedPolicy().size() == 4) {
					claimSetupForm.setRelatedPolicyRef2Flag("RP2");
					claimSetupForm.setRelatedPolicyRef3Flag("RP3");
					claimSetupForm.setRelatedPolicyRef4Flag("RP4");
				}
			}
		}
		model.addAttribute("claimSetupForm", claimSetupForm);
		return "secured.claim.edit";
	}

	@RequestMapping(value = "/downloadfile/" + UrlPattern.PARAM_PREFIX + "/{id}", method = RequestMethod.GET)
	private void downloadfile(@PathVariable("id") Long objId, HttpServletResponse response, Model model) {
		try {
			UploadedFile uploadedFile = uploadService.getUploadedFileById(objId);
			String mimeType = URLConnection.guessContentTypeFromName(uploadedFile.getName());

			if (mimeType == null) {
				LOGGER.error("mimetype is not detectable, will take default");
				mimeType = "application/octet-stream";
			}
			LOGGER.info("mimetype : " + mimeType);

			response.setContentType(mimeType);

			// "Content-Disposition : attachment" will be directly download, may
			// provide save as popup, based on your browser setting
			response.setContentLength(uploadedFile.getFileSize().intValue());
			response.setHeader("Content-Disposition",
					String.format("attachment; filename=\"" + uploadedFile.getName()));

			// Copy bytes from source to destination(outputstream in this
			// example), closes both streams.
			FileCopyUtils.copy(uploadedFile.getContent(), response.getOutputStream());
			return;
		} catch (Exception e) {
			LOGGER.error("downloadfile", e);
		}
	}

	// Claim Deletion Request
	@GetMapping(value = "/reqDelete/" + UrlPattern.PARAM_PREFIX + "/{claimId}")
	public @ResponseBody ClaimDeleteForm requestDelete(@PathVariable("claimId") Long claimId, Locale locale,
			HttpServletRequest req, HttpServletResponse response, Model model,
			@ModelAttribute("claimDeleteForm") ClaimDeleteForm claimDeleteForm, BindingResult result) {

		Claim claim = claimService.getClaim(claimId);
		claimDeleteForm.setClaimId(claim.getId());
		claimDeleteForm
				.setNote(messageSource.getMessage("claim.delete.request", new Object[] { claim.getClaimNo() }, locale));

		return claimDeleteForm;
	}

	// Claim Revert Request
	@GetMapping(value = "/reqRevert/" + UrlPattern.PARAM_PREFIX + "/{claimId}")
	public @ResponseBody ClaimDeleteForm requestRevert(@PathVariable("claimId") Long claimId, Locale locale,
			HttpServletRequest req, HttpServletResponse response, Model model,
			@ModelAttribute("claimDeleteForm") ClaimDeleteForm claimDeleteForm, BindingResult result) {

		Claim claim = claimService.getClaim(claimId);
		claimDeleteForm.setClaimId(claim.getId());
		claimDeleteForm
				.setNote(messageSource.getMessage("claim.revert.request", new Object[] { claim.getClaimNo() }, locale));
		return claimDeleteForm;
	}

	@PostMapping(value = "/revert")
	public @ResponseBody String revertDeletion(Locale locale, HttpServletRequest req, HttpServletResponse response,
			@ModelAttribute("claimDeleteForm") ClaimDeleteForm claimDeleteForm, BindingResult result) {

		Claim claim = claimService.getClaim(claimDeleteForm.getClaimId());
		if (claimService.updateDeleteFlag(claim.getId(), false)) {
			return messageSource.getMessage("claim.revert.success", new Object[] { claim.getClaimNo() }, "", locale);
		} else {
			return messageSource.getMessage("claim.revert.error", new Object[] { claim.getClaimNo() }, "", locale);
		}
	}

	@PostMapping(value = "/delete")
	public @ResponseBody String confirmDeletion(Locale locale, HttpServletRequest req, HttpServletResponse response,
			@ModelAttribute("claimDeleteForm") ClaimDeleteForm claimDeleteForm, BindingResult result) {

		Claim claim = claimService.getClaim(claimDeleteForm.getClaimId());
		if (claimService.updateDeleteFlag(claim.getId(), true)) {
			return messageSource.getMessage("claim.delete.success", new Object[] { claim.getClaimNo() }, "", locale);
		} else {
			return messageSource.getMessage("claim.delete.error", new Object[] { claim.getClaimNo() }, "", locale);
		}
	}

	@RequestMapping(value = "/notification", method = { RequestMethod.POST, RequestMethod.GET })
	public String prepareNotification(Model model, @ModelAttribute("claimSetupForm") ClaimSetupForm claimSetupForm,
			BindingResult result) {

		ClaimNotificationForm notificationForm = new ClaimNotificationForm();
		ClaimNotificationEmail notificationEmail = new ClaimNotificationEmail();
		Claim claim = claimService.getClaim(claimSetupForm.getClaim().getId());
		notificationEmail.setClaim(claim);
		notificationForm.setNotificationEmail(notificationEmail);

		// Prepare Email Notification recipient list
		// Claim has company code, it was created by UEM user, if it was created
		// by SIB user then no point to set this
		// Company code in claim is removed, all company will be depend on the
		// policy company
		if (claim.getPolicy().getCompany() != null && StringUtils.isNotBlank(claim.getPolicy().getCompany().getCode())
				&& !claim.getPolicy().getCompany().getCode().equalsIgnoreCase(SetupConstant.COMPANY_CODE_SIB)) {
			notificationForm.setEmailRecipientUem(
					companyService.getAllEmailUnderSameCompany(claim.getPolicy().getCompany().getCode()));
		} else if (claim.getCompanyCode() != null) {
			notificationForm.setEmailRecipientUem(companyService.getAllEmailUnderSameCompany(claim.getCompanyCode()));
		} else {
			notificationForm.setEmailRecipientUem(new ArrayList<String>());
		}

		// SIB user will always tag to company code SIB
		notificationForm
				.setEmailRecipientSib(companyService.getAllEmailUnderSameCompany(SetupConstant.COMPANY_CODE_SIB));

		// Prepare subject and content
		claimNotificationService.prepareEmail(notificationEmail);

		// Prepare attachment
		notificationForm.setAttachments(claimService.getUploadedFile(claim));

		model.addAttribute("claimNotificationForm", notificationForm);

		return "secured.claim.notification";
	}

	@RequestMapping(value = "/notification/send", method = RequestMethod.POST)
	public String sendNotification(Model model,
			@ModelAttribute("claimNotificationForm") ClaimNotificationForm notificationForm, BindingResult result,
			HttpServletRequest req, RedirectAttributes redirectAttributes) {

		ClaimNotificationFormValidator validator = new ClaimNotificationFormValidator(claimService);
		validator.validate(notificationForm, result);

		ClaimNotificationEmail email = notificationForm.getNotificationEmail();
		claimNotificationService.prepareEmail(email);

		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

		if (result.hasErrors()) {
			Claim claim = claimService.getClaim(email.getClaim().getId());
			// Prepare Email Notification recipient list
			// Claim has company code, it was created by UEM user, if it was
			// created by SIB user then no point to set this
			if (claim.getPolicy().getCompany() != null
					&& StringUtils.isNotBlank(claim.getPolicy().getCompany().getCode())
					&& !claim.getPolicy().getCompany().getCode().equalsIgnoreCase(SetupConstant.COMPANY_CODE_SIB)) {
				notificationForm.setEmailRecipientUem(
						companyService.getAllEmailUnderSameCompany(claim.getPolicy().getCompany().getCode()));
			} else if (claim.getCompanyCode() != null) {
				notificationForm
						.setEmailRecipientUem(companyService.getAllEmailUnderSameCompany(claim.getCompanyCode()));
			} else {
				notificationForm.setEmailRecipientUem(new ArrayList<String>());
			}

			// SIB user will always tag to company code SIB
			notificationForm
					.setEmailRecipientSib(companyService.getAllEmailUnderSameCompany(SetupConstant.COMPANY_CODE_SIB));

			// Prepare attachment
			notificationForm.setAttachments(claimService.getUploadedFile(claim));

			return "secured.claim.notification";
		}

		JMSMessageContent messageContent = new JMSMessageContent(principal.getUsername());
		messageContent.setTransaction(NotificationTransaction.ClaimAcknowledgment);
		messageContent.setNotificationType(NotificationType.EMAIL);

		StringBuilder recipient = new StringBuilder();
		if (notificationForm.isInsurerRecipientSelected()) {
			recipient.append(notificationForm.getInsurerEmail());
			email.setRecipientInsurer(notificationForm.getInsurerEmail());
		}
		if (notificationForm.isSibRecipientSelected()) {
			StringBuilder sibRecipient = new StringBuilder();
			for (String emailAddr : notificationForm.getSibEmails()) {
				emailAddr = emailAddr.substring(emailAddr.indexOf("<") + 1, emailAddr.indexOf(">"));
				if (recipient.length() > 0) {
					recipient.append(",");
				}
				recipient.append(emailAddr);

				if (sibRecipient.length() > 0) {
					sibRecipient.append(",");
				}
				sibRecipient.append(emailAddr);
			}
			email.setRecipientSib(sibRecipient.toString());
		}
		if (notificationForm.isUemRecipientSelected()) {
			StringBuilder uemRecipient = new StringBuilder();
			for (String emailAddr : notificationForm.getUemEmails()) {
				emailAddr = emailAddr.substring(emailAddr.indexOf("<") + 1, emailAddr.indexOf(">"));
				if (recipient.length() > 0) {
					recipient.append(",");
				}
				recipient.append(emailAddr);

				if (uemRecipient.length() > 0) {
					uemRecipient.append(",");
				}
				uemRecipient.append(emailAddr);
			}
			email.setRecipientCust(uemRecipient.toString());
		}

		Map<String, Object> body = new HashMap<>();
		body.put(NotificationConstant.EMAIL_RECEPIENT, recipient.toString());
		body.put(NotificationConstant.EMAIL_MAILER, NotificationConstant.Mailer.HTML.toString());
		claimNotificationService.prepareEmailMap(notificationForm.getNotificationEmail().getClaim(), body);
		messageContent.setMessageBody(body);

		JMSMessage message = jmsMessageService.createMessage(req, messageContent);

		Long[] attachmentIds = notificationForm.getSelectedAttachments();
		if (attachmentIds != null) {
			List<UploadedFile> files = claimService.getUploadedFile(notificationForm.getNotificationEmail().getClaim());
			for (UploadedFile f : files) {
				for (Long attachmentId : attachmentIds) {
					if (attachmentId.equals(f.getId())) {
						message.addAttachment(f.getName(), f.getContent());
					}
				}
			}
		}

		jmsMessageService.send(NotificationConstant.ENDPOINT_EMAIL, message);

		// Create a email notification record
		claimNotificationService.createClaimNotificationEmail(email);

		model.asMap().remove("url_param_prefix");
		redirectAttributes.addFlashAttribute("msg", "Email have been sent.");
		return "redirect:/secured/claim/edit/" + UrlPattern.PARAM_PREFIX + "/"
				+ notificationForm.getNotificationEmail().getClaim().getId();
	}
	
	private final String getDropdownLabel(List<PolicyRelatedClaimDTO> otherPolicy, String policyNo) {
		if (otherPolicy != null && policyNo != null) {
			for (PolicyRelatedClaimDTO policy : otherPolicy) {
				if (policyNo.equals(policy.getPolicyNo())) {
					return policy.getDropDownLabel();
				}
			}
		}
		return "";
	}

	private final String getDropdownLabel(List<CompanyDepartment> departments, Long departmentId) {
		if (departments != null && departmentId != null) {
			for (CompanyDepartment department : departments) {
				if (departmentId.equals(department.getId())) {
					return department.getName();
				}
			}
		}
		return "";
	}

	private final String getLossTypeDropdownLabel(List<LossType> lossTypes, String code) {
		if (lossTypes != null && code != null) {
			for (LossType lossType : lossTypes) {
				if (code.equals(lossType.getCode())) {
					return lossType.getName();
				}
			}
		}
		return "";
	}

}
