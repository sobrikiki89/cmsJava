package web.module.policy.controller;

import java.math.BigDecimal;
import java.net.URLConnection;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.multipart.support.StringMultipartFileEditor;

import app.core.security.UserPrincipal;
import app.core.service.FunctionService;
import app.core.spring.UrlPattern;
import app.core.utils.AppConstant;
import module.policy.PolicyModule;
import module.policy.dto.PolicySearchCriteria;
import module.policy.dto.PolicySearchDTO;
import module.policy.model.Policy;
import module.policy.model.PolicyEndorsement;
import module.policy.model.PolicyExcessDeductible;
import module.policy.model.PolicyInterestInsured;
import module.policy.service.PolicySearchService;
import module.policy.service.PolicyService;
import module.setup.model.Company;
import module.setup.model.InsuranceClass;
import module.setup.model.InsuranceClassCategory;
import module.setup.service.CompanyService;
import module.setup.service.InsuranceClassService;
import module.setup.service.InsurerService;
import module.upload.model.UploadedFile;
import module.upload.model.UploadedFileCategory;
import module.upload.service.UploadedFileService;
import web.core.controller.BaseController;
import web.core.helper.WebConstant;
import web.module.policy.model.PolicySearchForm;
import web.module.policy.model.PolicySetupForm;
import web.module.policy.validator.PolicySetupNewFormValidator;
import web.module.setup.model.CompanyAjaxDTO;

@Controller
@RequestMapping("/secured/policymgmt/setup")
@SessionAttributes({ "policySearchForm" })
public class PolicySetupController extends BaseController {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(PolicySetupController.class);

	@Autowired private PolicyService policyService;
	@Autowired private InsurerService insurerService;
	@Autowired private InsuranceClassService insuranceClassService;
	@Autowired private CompanyService companyService;
	@Autowired private PolicySearchService policySearchService;
	@Autowired private UploadedFileService uploadService;
	@Autowired private FunctionService functionService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat sdf = new SimpleDateFormat(AppConstant.DATE_FORMAT);
		binder.registerCustomEditor(Date.class, "policy.startDate", new CustomDateEditor(sdf, true));
		binder.registerCustomEditor(Date.class, "policy.endDate", new CustomDateEditor(sdf, true));
		binder.registerCustomEditor(Date.class, "criteria.effectiveYear", new CustomDateEditor(sdf, true));

		DecimalFormat decimalFormat = new DecimalFormat();
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(',');
		decimalFormat.setDecimalFormatSymbols(symbols);
		decimalFormat.setMaximumFractionDigits(2);
		binder.registerCustomEditor(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, decimalFormat, true));

		binder.registerCustomEditor(String.class, new StringMultipartFileEditor());
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
		binder.registerCustomEditor(String.class, new StringMultipartFileEditor("UTF-8"));
	}

	List<UploadedFile> files = new ArrayList<>();

	// Coming in from Menu
	@RequestMapping(method = RequestMethod.GET)
	public String getSearchScreen(Locale locale, HttpServletRequest req, Model model) {
		PolicySearchForm policySearchForm = new PolicySearchForm();
		policySearchForm.setCriteria(new PolicySearchCriteria());
		policySearchForm.setInsurers(insurerService.getAllInsurers());
		policySearchForm.setInsuranceClasses(insuranceClassService.getInsuranceClasses());
		policySearchForm.setCompanies(companyService.getAllCompanies());
		model.addAttribute("policySearchForm", policySearchForm);
		return "secured.policymgmt.setup";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String searchPolicy(Model model, @ModelAttribute("policySearchForm") PolicySearchForm policySearchForm,
			BindingResult result) {
		// We will determine the action
		if (WebConstant.ACTION_NEW.equalsIgnoreCase(policySearchForm.getAction())) {
			PolicySetupForm policySetupForm = new PolicySetupForm();
			Policy policy = new Policy();
			policy.setSumInsured(BigDecimal.ZERO);
			policy.setPremiumGross(BigDecimal.ZERO);
			policy.setPremiumRebate(BigDecimal.ZERO);
			policy.setPremiumTax(BigDecimal.ZERO);
			policy.setPremiumNet(BigDecimal.ZERO);
			policy.setStampDuty(BigDecimal.ZERO);
			policySetupForm.setPolicy(policy);
			policySetupForm.setInsurers(insurerService.getInsurers());
			policySetupForm.setInsuranceClasses(insuranceClassService.getInsuranceClasses());
			policySetupForm.setCompanies(companyService.getAllCompanies());
			policySetupForm.setTotalSumInsured(BigDecimal.ZERO);
			policySetupForm.setTotalGross(BigDecimal.ZERO);
			policySetupForm.setTotalRebate(BigDecimal.ZERO);
			policySetupForm.setTotalTax(BigDecimal.ZERO);
			policySetupForm.setTotalNet(BigDecimal.ZERO);
			policySetupForm.setTotalStampDuty(BigDecimal.ZERO);
			policySetupForm.setEndorsementTotalSumInsured(BigDecimal.ZERO);
			policySetupForm.setEndorsementTotalGross(BigDecimal.ZERO);
			policySetupForm.setEndorsementTotalRebate(BigDecimal.ZERO);
			policySetupForm.setEndorsementTotalTax(BigDecimal.ZERO);
			policySetupForm.setEndorsementTotalNet(BigDecimal.ZERO);
			policySetupForm.setEndorsementTotalStampDuty(BigDecimal.ZERO);
			model.addAttribute("policySetupForm", policySetupForm);
			return "secured.policymgmt.setup.new";
		}

		else if (WebConstant.ACTION_BACK.equalsIgnoreCase(policySearchForm.getAction())) {
			return "redirect:/secured";
		}

		else if (WebConstant.ACTION_DELETE.equalsIgnoreCase(policySearchForm.getAction())) {
			// TODO :
		}

		else if ("search".equalsIgnoreCase(policySearchForm.getAction())) {
			policySearchForm.setSearched(true);
			if (LOGGER.isDebugEnabled()) {
				if (policySearchForm.getCriteria() != null) {
					LOGGER.debug("### SEARCH : Policy List");
					LOGGER.debug("### Company Id : [" + policySearchForm.getCriteria().getCompanyId() + "]");
					LOGGER.debug("### Insurer Code : [" + policySearchForm.getCriteria().getInsurerCode() + "]");
					LOGGER.debug("### Class of Insurance Code : ["
							+ policySearchForm.getCriteria().getInsuranceClassCode() + "]");
					LOGGER.debug("### Policy No : [" + policySearchForm.getCriteria().getPolicyNo() + "]");
					LOGGER.debug("### Effective Year : [" + policySearchForm.getCriteria().getEffectiveYear() + "]");
				}
			}

			List<PolicySearchDTO> policies = policySearchService.searchPolicy(policySearchForm.getCriteria());
			policySearchForm.setPolicies(policies);
		}

		return "secured.policymgmt.setup";
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String savePolicyNew(Model model, @ModelAttribute("policySetupForm") PolicySetupForm policySetupForm,
			BindingResult result) {
		PolicySetupNewFormValidator validator = new PolicySetupNewFormValidator(policyService);
		validator.validate(policySetupForm, result);

		if (LOGGER.isDebugEnabled()) {
			if (policySetupForm.getPolicy().getInterestInsuredList() != null) {
				LOGGER.debug("### NEW : Interest Insured List");
				for (PolicyInterestInsured item : policySetupForm.getPolicy().getInterestInsuredList()) {
					LOGGER.debug("### order : [" + item.getOrder() + "]");
					LOGGER.debug("### description : [" + item.getDescription() + "]");
					LOGGER.debug("### sum covered : [" + item.getSumCovered() + "]");
					if(item.getSumCovered() == null) {
						item.setSumCovered(BigDecimal.ZERO);
					}
				}
			}

			if (policySetupForm.getPolicy().getExcessDeductibleList() != null) {
				LOGGER.debug("### NEW : Excess Deductible List");
				for (PolicyExcessDeductible item : policySetupForm.getPolicy().getExcessDeductibleList()) {
					LOGGER.debug("### order : [" + item.getOrder() + "]");
					LOGGER.debug("### description : [" + item.getDescription() + "]");
					LOGGER.debug("### amount : [" + item.getAmount() + "]");
					if(item.getAmount() == null) {
						item.setAmount(BigDecimal.ZERO);
					}
				}
			}
			
			if (policySetupForm.getPolicy().getEndorsementList() != null ) {
				LOGGER.debug("### NEW : Endorsement List");
				for (PolicyEndorsement item : policySetupForm.getPolicy().getEndorsementList()) {
					LOGGER.debug("### order : [" + item.getOrder() + "]");
					LOGGER.debug("### endorsement no : [" + item.getEndorsmentNo() + "]");
					LOGGER.debug("### gross premium : [" + item.getGrossPremium() + "]");
					LOGGER.debug("### rebate premium : [" + item.getRebatePremium() + "]");
					LOGGER.debug("### net premium : [" + item.getNetPremium() + "]");
					LOGGER.debug("### stamp duty : [" + item.getStampDuty() + "]");
					
					if(item.getSumInsured() == null)
						item.setSumInsured(BigDecimal.ZERO);
					
					if(item.getGrossPremium() == null)
						item.setGrossPremium(BigDecimal.ZERO);
					
					if(item.getRebatePremium() == null)
						item.setRebatePremium(BigDecimal.ZERO);
					
					if(item.getNetPremium() == null)
						item.setNetPremium(BigDecimal.ZERO);
					
					if(item.getTaxAmount() == null)
						item.setTaxAmount(BigDecimal.ZERO);
					
					if(item.getStampDuty() == null)
						item.setStampDuty(BigDecimal.ZERO);
				}
			}
		}

		policySetupForm.setInsurers(insurerService.getInsurers());
		policySetupForm.setInsuranceClasses(insuranceClassService.getInsuranceClasses());
		policySetupForm.setCompanies(companyService.getAllCompanies());

		if (result.hasErrors()) {
			this.files = new ArrayList<UploadedFile>();
			return "secured.policymgmt.setup.new";
		} else {
			Long policyId = policyService.createPolicy(policySetupForm.getPolicy());
			if (policyId != null) {
				
				List<UploadedFile> attached = new ArrayList<UploadedFile>();
				for (UploadedFile uFile : files) {
					if (uFile.getId() == null) {
						attached.add(uFile);
					}
				}
				Policy policy = policyService.getPolicy(policyId);
				policyService.attachFile(policy, attached);
				this.checkWarning(result, policy);
				result.reject("success.create", new Object[] { "Policy", policy.getPolicyNo() }, null);
				policySetupForm.setFilesList(policyService.getUploadedFile(policy));
				policySetupForm.setPolicy(policy);
				getTotalPremium(policySetupForm, policy);
				this.files = new ArrayList<UploadedFile>();
				PolicySearchForm policySearchForm = (PolicySearchForm) model.asMap().get("policySearchForm");
				if (policySearchForm != null && !"search".equalsIgnoreCase(policySearchForm.getAction())) {
					policySearchForm.setAction("");
				}
				return "secured.policymgmt.setup.edit";
			} else {
				return "secured.policymgmt.setup";
			}
		}
	}

	@RequestMapping(value = "/edit/" + UrlPattern.PARAM_PREFIX + "/{policyId}", method = RequestMethod.GET)
	public String getPolicyEdit(@PathVariable("policyId") Long policyId, Model model) {
		Policy policy = policyService.getPolicy(policyId);
		if (policy != null) {
			PolicySetupForm policySetupForm = new PolicySetupForm();
			policySetupForm.setInsurers(insurerService.getInsurers());
			policySetupForm.setInsuranceClasses(insuranceClassService.getInsuranceClasses());
			policySetupForm.setCompanies(companyService.getAllCompanies());
			policySetupForm.setFilesList(policyService.getUploadedFile(policy));
			policy.setPremiumGross(policy.getPremiumGross() == null ? BigDecimal.ZERO : policy.getPremiumGross());
			policy.setPremiumRebate(policy.getPremiumRebate() == null ? BigDecimal.ZERO : policy.getPremiumRebate());
			policy.setPremiumTax(policy.getPremiumTax() == null ? BigDecimal.ZERO : policy.getPremiumTax());
			policy.setPremiumNet(policy.getPremiumNet() == null ? BigDecimal.ZERO : policy.getPremiumNet());
			policy.setStampDuty(policy.getStampDuty() == null ? BigDecimal.ZERO : policy.getStampDuty());
			policySetupForm.setPolicy(policy);
			this.getTotalPremium(policySetupForm, policy);
			
			// Check the current user has edit policy no permission or not
			UserPrincipal principal = UserPrincipal.class
					.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());			
			policySetupForm.setAllowedEditPolicyNo(functionService.isAuthorizedByCode(principal.getCurrentRoleId(),
					PolicyModule.FUNC_POLICY_SETUP_EDIT_POLICY_NO));
			policySetupForm.setAllowedDeleteEndorsement(functionService.isAuthorizedByCode(principal.getCurrentRoleId(),
					PolicyModule.FUNC_POLICY_SETUP_DELETE_ENDORSEMENT));
			List<UploadedFile> file = new ArrayList<UploadedFile>();
			this.files = file;
			model.addAttribute("policySetupForm", policySetupForm);
			
			return "secured.policymgmt.setup.edit";
		}
		return "secured.policymgmt.setup";
	}

	private void getTotalPremium(PolicySetupForm policySetupForm, Policy policy) {
		BigDecimal totalSumInsured = BigDecimal.ZERO;
		BigDecimal totalGross = BigDecimal.ZERO;
		BigDecimal totalRebate = BigDecimal.ZERO;
		BigDecimal totalTax = BigDecimal.ZERO;
		BigDecimal totalStampDuty = BigDecimal.ZERO;
		BigDecimal totalNet = BigDecimal.ZERO;
		List<PolicyEndorsement> endorsments = policyService.getAllPolicyEndorsements(policy);
		if (endorsments.size() > 0) {
			for (int i = 0; i < endorsments.size(); i++ ) {
				if (endorsments.get(i).getSumInsured() != null) {
					totalSumInsured = endorsments.get(i).getSumInsured().add(totalSumInsured);
				}
				if (endorsments.get(i).getGrossPremium() != null) {
					totalGross = endorsments.get(i).getGrossPremium().add(totalGross);
				}
				if (endorsments.get(i).getRebatePremium() != null) {
					totalRebate = endorsments.get(i).getRebatePremium().add(totalRebate);
				}
				if (endorsments.get(i).getTaxAmount() != null) {
					totalTax = endorsments.get(i).getTaxAmount().add(totalTax);
				}
				if (endorsments.get(i).getStampDuty() != null) {
					totalStampDuty = endorsments.get(i).getStampDuty().add(totalStampDuty);
				}
				if (endorsments.get(i).getNetPremium() != null) {
					totalNet = endorsments.get(i).getNetPremium().add(totalNet);
				}
			}
		}
		policySetupForm.setTotalSumInsured(policy.getSumInsured() == null ? BigDecimal.ZERO.add(totalSumInsured) : policy.getSumInsured().add(totalSumInsured));
		policySetupForm.setTotalGross(policy.getPremiumGross() == null ? BigDecimal.ZERO.add(totalGross) : policy.getPremiumGross().add(totalGross));
		policySetupForm.setTotalRebate(policy.getPremiumRebate() == null ? BigDecimal.ZERO.add(totalRebate) : policy.getPremiumRebate().add(totalRebate));
		policySetupForm.setTotalTax(policy.getPremiumTax() == null ? BigDecimal.ZERO.add(totalTax) : policy.getPremiumTax().add(totalTax));
		policySetupForm.setTotalStampDuty(policy.getStampDuty() == null ? BigDecimal.ZERO.add(totalStampDuty) : policy.getStampDuty().add(totalStampDuty));
		policySetupForm.setTotalNet(policy.getPremiumNet() == null ? BigDecimal.ZERO.add(totalNet) : policy.getPremiumNet().add(totalNet));

		policySetupForm.setEndorsementTotalSumInsured(totalSumInsured);
		policySetupForm.setEndorsementTotalGross(totalGross);
		policySetupForm.setEndorsementTotalRebate(totalRebate);
		policySetupForm.setEndorsementTotalTax(totalTax);
		policySetupForm.setEndorsementTotalStampDuty(totalStampDuty);
		policySetupForm.setEndorsementTotalNet(totalNet);
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String savePolicyEdit(Model model, @ModelAttribute("policySetupForm") PolicySetupForm policySetupForm,
			BindingResult result) {
		
		if (WebConstant.ACTION_BACK.equalsIgnoreCase(policySetupForm.getAction())) {
			return "secured.policymgmt.setup";
		}
		
		PolicySetupNewFormValidator validator = new PolicySetupNewFormValidator(policyService);
		validator.validateEdit(policySetupForm, result);

		if (LOGGER.isDebugEnabled()) {
			if (policySetupForm.getPolicy().getInterestInsuredList() != null) {
				LOGGER.debug("### EDIT : Interest Insured List");
				for (PolicyInterestInsured item : policySetupForm.getPolicy().getInterestInsuredList()) {
					LOGGER.debug("### order : [" + item.getOrder() + "]");
					LOGGER.debug("### description : [" + item.getDescription() + "]");
					LOGGER.debug("### sum covered : [" + item.getSumCovered() + "]");
					if(item.getSumCovered() == null) {
						item.setSumCovered(BigDecimal.ZERO);
					}
				}
			}

			if (policySetupForm.getPolicy().getExcessDeductibleList() != null) {
				LOGGER.debug("### EDIT : Excess Deductible List");
				for (PolicyExcessDeductible item : policySetupForm.getPolicy().getExcessDeductibleList()) {
					LOGGER.debug("### order : [" + item.getOrder() + "]");
					LOGGER.debug("### description : [" + item.getDescription() + "]");
					LOGGER.debug("### amount : [" + item.getAmount() + "]");
					if(item.getAmount() == null) {
						item.setAmount(BigDecimal.ZERO);
					}
				}
			}
			
			if (policySetupForm.getPolicy().getEndorsementList() != null ) {
				LOGGER.debug("### EDIT : Endorsement List");
				for (PolicyEndorsement item : policySetupForm.getPolicy().getEndorsementList()) {
					LOGGER.debug("### order : [" + item.getOrder() + "]");
					LOGGER.debug("### endorsement no : [" + item.getEndorsmentNo() + "]");
					LOGGER.debug("### gross premium : [" + item.getGrossPremium() + "]");
					LOGGER.debug("### rebate premium : [" + item.getRebatePremium() + "]");
					LOGGER.debug("### net premium : [" + item.getNetPremium() + "]");
					LOGGER.debug("### stamp duty : [" + item.getStampDuty() + "]");
					
					if(item.getSumInsured() == null)
						item.setSumInsured(BigDecimal.ZERO);
					
					if(item.getGrossPremium() == null)
						item.setGrossPremium(BigDecimal.ZERO);
					
					if(item.getRebatePremium() == null)
						item.setRebatePremium(BigDecimal.ZERO);
					
					if(item.getNetPremium() == null)
						item.setNetPremium(BigDecimal.ZERO);
					
					if(item.getTaxAmount() == null)
						item.setTaxAmount(BigDecimal.ZERO);
					
					if(item.getStampDuty() == null)
						item.setStampDuty(BigDecimal.ZERO);
				}
			}
		}

		policySetupForm.setInsurers(insurerService.getInsurers());
		policySetupForm.setInsuranceClasses(insuranceClassService.getInsuranceClasses());
		policySetupForm.setCompanies(companyService.getAllCompanies());
		List<UploadedFile> attach = new ArrayList<UploadedFile>();
		if (files.size() > 0) {
			for (UploadedFile uFile : files) {
				if (uFile.getId() == null) {
					attach.add(uFile);
				}
			}
		}
		if (!result.hasErrors()) {
			Policy policy = policySetupForm.getPolicy();
			policy = this.updateAllList(policySetupForm.getPolicy());
			if(policySetupForm.getPolicy() != null 
					&& policySetupForm.getPolicy().getInsuranceClass() != null 
					&& policySetupForm.getPolicy().getInsuranceClass().getCode() != null
					&& !policySetupForm.getPolicy().getInsuranceClass().getCode().isEmpty()) {
				String[] insuranceClassCodeSelected = policySetupForm.getPolicy().getInsuranceClass().getCode().split(",");
				
				InsuranceClass insuranceClass = insuranceClassService.getInsuranceClass(insuranceClassCodeSelected[(insuranceClassCodeSelected.length - 1)]);
				policySetupForm.getPolicy().setInsuranceClass(insuranceClass);
			}
			
			policy = policyService.updatePolicy(policySetupForm.getPolicy());
			this.checkWarning(result, policy);
			policyService.attachFile(policy, files);
			result.reject("success.update", new Object[] { "Policy", policy.getPolicyNo() }, null);
			policySetupForm.setFilesList(policyService.getUploadedFile(policy));
			policySetupForm.setPolicy(policy);
			getTotalPremium(policySetupForm, policy);
			this.files = new ArrayList<UploadedFile>();
		}
		
		// Check the current user has edit policy no permission or not
		UserPrincipal principal = UserPrincipal.class
				.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());			
		policySetupForm.setAllowedEditPolicyNo(functionService.isAuthorizedByCode(principal.getCurrentRoleId(),
				PolicyModule.FUNC_POLICY_SETUP_EDIT_POLICY_NO));
		policySetupForm.setAllowedDeleteEndorsement(functionService.isAuthorizedByCode(principal.getCurrentRoleId(),
				PolicyModule.FUNC_POLICY_SETUP_DELETE_ENDORSEMENT));
		return "secured.policymgmt.setup.edit";
	}

	private void checkWarning(BindingResult result, Policy policy) {
		if ((policy.getPremiumGross()==null || BigDecimal.ZERO.compareTo(policy.getPremiumGross()) == 0) 
				&& ((policy.getPremiumTax()!=null && !"0.00".equals(policy.getPremiumTax().toString()) ) || 
						(policy.getPremiumRebate()!=null && !"0.00".equals(policy.getPremiumRebate().toString())) )) {
			result.reject("warning.empty", new Object[] { "Policy Premium Gross", policy.getPremiumGross() }, null);
		}
		
		for (PolicyEndorsement item : policy.getEndorsementList()) {
			if ((item.getGrossPremium()==null || item.getGrossPremium().compareTo(BigDecimal.ZERO) == 0) 
					&& ( (item.getTaxAmount()!=null && !"0.00".equals(item.getTaxAmount().toString()) ) || 
							(item.getRebatePremium()!=null && !"0.00".equals(item.getRebatePremium().toString())) )) {
				result.reject("warning.empty", new Object[] { "Endorsement Premium Gross, No " + item.getOrder(), 
						item.getGrossPremium() }, null);
			}
		}
	}
	
	private Policy updateAllList(Policy policy) {
		/** 1. POLICY INTEREST INSURED **/
		List<PolicyInterestInsured> interestInsuredList = policy.getInterestInsuredList();
		List<PolicyInterestInsured> interestInsuredToRemove = new ArrayList<PolicyInterestInsured>();

		BigDecimal sumInsured = BigDecimal.ZERO;
		if (policy.getInterestInsuredList() == null) {
			policy.setInterestInsuredList(new ArrayList<PolicyInterestInsured>());
		} else {
			for (PolicyInterestInsured item : policy.getInterestInsuredList()) {
				if (item.getSumCovered() != null) {
					sumInsured = sumInsured.add(item.getSumCovered());
				} else {
					interestInsuredToRemove.add(item);
				}
				item.setPolicy(policy);
			}
			if (interestInsuredToRemove.size() > 0) {
				interestInsuredList.removeAll(interestInsuredToRemove);
			}
			policy.setInterestInsuredList(interestInsuredList);
		}
		if (BigDecimal.ZERO.compareTo(sumInsured) == 0) {
			policy.setSumInsured(null);
		} else {
			policy.setSumInsured(sumInsured);
		}
		
		/** 2. POLICY EXCESS **/
		List<PolicyExcessDeductible> excessList = policy.getExcessDeductibleList();
		List<PolicyExcessDeductible> excessToRemove = new ArrayList<PolicyExcessDeductible>();
		if (policy.getExcessDeductibleList() == null) {
			policy.setExcessDeductibleList(new ArrayList<PolicyExcessDeductible>());
		} else {
			for (PolicyExcessDeductible item : excessList) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("["+ item.getOrder() + "] Excess Amount : " + item.getAmount());
				}
				if (item.getAmount()!=null && BigDecimal.ZERO.compareTo(item.getAmount()) <= 0) {
					item.setPolicy(policy);
				} else {
					excessToRemove.add(item);
				}
			}
			if(excessToRemove.size() > 0){
				excessList.removeAll(excessToRemove);
			}
			policy.setExcessDeductibleList(excessList);
		}
		
		/** 3. POLICY ENDORSEMENT **/
		List<PolicyEndorsement> endorsementList = policy.getEndorsementList();
		List<PolicyEndorsement> endorsementToRemove = new ArrayList<PolicyEndorsement>();
		if (policy.getEndorsementList() == null) {
			policy.setEndorsementList(new ArrayList<PolicyEndorsement>());
		} else {
			for (PolicyEndorsement item : endorsementList) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("["+ item.getOrder() + "] Gross Premium : " + item.getGrossPremium());
					LOGGER.debug("["+ item.getOrder() + "] Rebate Premium : " + item.getRebatePremium());
					LOGGER.debug("["+ item.getOrder() + "] Net Premium : " + item.getNetPremium());
					LOGGER.debug("["+ item.getOrder() + "] stamp duty : " + item.getStampDuty());
				}
				if (item.getTaxAmount()!=null || item.getRebatePremium() != null 
						|| item.getGrossPremium()!=null || item.getSumInsured()!=null) {
					item.setPolicy(policy);
					item.setPolicyNo(policy.getPolicyNo());
				} else {
					endorsementToRemove.add(item);
				}
			}
			if (endorsementToRemove.size() > 0) {
				endorsementList.removeAll(endorsementToRemove);
			}
			policy.setEndorsementList(endorsementList);
		}
		return policy;
	}

	@RequestMapping(value = "/refresh/company", method = RequestMethod.POST)
	public @ResponseBody CompanyAjaxDTO refreshCompanyInfo(@RequestBody CompanyAjaxDTO company) {
		LOGGER.info("AJAX call refresh company");
		LOGGER.info("Company Id : {}", company.getCompanyId());
		if (company.getCompanyId() != null) {
			Company entity = companyService.getCompany(company.getCompanyId());
			if (entity != null) {
				company.setName(StringUtils.defaultString(entity.getName()));
			} else {
				company.setName("");
			}

			if (entity != null && entity.getContact() != null) {
				company.setContactPerson(StringUtils.defaultString(entity.getContact().getContactPerson()));
				company.setTelNo(StringUtils.defaultString(entity.getContact().getTelNo()));
				company.setFaxNo(StringUtils.defaultString(entity.getContact().getFaxNo()));
				company.setAddress1(StringUtils.defaultString(entity.getContact().getAddress1()));
				company.setAddress2(StringUtils.defaultString(entity.getContact().getAddress2()));
				company.setAddress3(StringUtils.defaultString(entity.getContact().getAddress3()));
				company.setCity(StringUtils.defaultString(entity.getContact().getCity()));
				company.setPostcode(StringUtils.defaultString(entity.getContact().getPostcode()));

			} else {
				company.setContactPerson("");
				company.setTelNo("");
				company.setFaxNo("");
				company.setAddress1("");
				company.setAddress2("");
				company.setAddress3("");
				company.setCity("");
				company.setPostcode("");
			}

			if (entity != null && entity.getContact() != null && entity.getContact().getState() != null) {
				company.setState(StringUtils.defaultString(entity.getContact().getState().getName()));
			} else {
				company.setState("");
			}
		}
		return company;
	}

	// Upload File
	@RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
	public @ResponseBody List<UploadedFile> attachmentHandler(Model model, HttpServletRequest request,
			@ModelAttribute("policySetupForm") PolicySetupForm policySetupForm, BindingResult result) {

		try {
			if (request instanceof MultipartHttpServletRequest) {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				multipartRequest.getParameter("attachment");

				Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
				if (fileMap != null) {
					for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
						MultipartFile file = entry.getValue();
						if (LOGGER.isDebugEnabled()) {
							LOGGER.info("Received Multipart request");
							LOGGER.info("filename : {}", file.getOriginalFilename());
							LOGGER.info("content-type : {}", file.getContentType());
							LOGGER.info("length : {}", file.getSize());
						}

						UploadedFile uploadedFile = new UploadedFile();
						uploadedFile.setName(file.getOriginalFilename());
						uploadedFile.setFileSize(file.getSize());
						uploadedFile.setFileCategory(UploadedFileCategory.POLICY.toString());
						uploadedFile.setMimeType(file.getContentType());
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

	@RequestMapping(value = "/deletefile/" + UrlPattern.PARAM_PREFIX + "/{id}", method = RequestMethod.GET)
	private String deletefile(@PathVariable("id") Long objId, HttpServletResponse response, Model model) {
		PolicySetupForm policySetupForm = new PolicySetupForm();
		Policy policy = policyService.getPolicyByFileId(objId);
		if (policy != null) {
			policyService.deletePolicyFilebyFileId(objId);
			policyService.deleteUploadedFileById(objId);
			this.files = new ArrayList<UploadedFile>();
			List<UploadedFile> uploadedFile = policyService.getUploadedFile(policy);
			if (uploadedFile != null) {
				for (UploadedFile uFile : uploadedFile) {
					files.add(uFile);
				}
			}
			policySetupForm.setInsurers(insurerService.getInsurers());
			policySetupForm.setInsuranceClasses(insuranceClassService.getInsuranceClasses());
			policySetupForm.setCompanies(companyService.getAllCompanies());
			policySetupForm.setFilesList(policyService.getUploadedFile(policy));
			policySetupForm.setPolicy(policy);
			policySetupForm.getPolicy().setInterestInsuredList(policyService.getInterestInsuredByPolicy(policy));
			policySetupForm.getPolicy().setExcessDeductibleList(policyService.getExcessDeductible(policy));
		}
		model.addAttribute("policySetupForm", policySetupForm);
		return "secured.policymgmt.setup.edit";
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

	@RequestMapping(value = "/deleteNewFile", method = RequestMethod.GET)
	private @ResponseBody List<UploadedFile> deleteNewFile(Model model, HttpServletRequest request,
			@ModelAttribute("policySetupForm") PolicySetupForm policySetupForm, BindingResult result) {
		List<UploadedFile> fileNewList = this.files;
		for (UploadedFile uploadedFile : files) {
			try {
				if (request instanceof MultipartHttpServletRequest) {
					MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
					multipartRequest.getParameter("attachment");

					Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
					if (fileMap != null) {
						for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
							MultipartFile file = entry.getValue();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.info("Received Multipart request");
								LOGGER.info("filename : {}", file.getOriginalFilename());
								LOGGER.info("content-type : {}", file.getContentType());
								LOGGER.info("length : {}", file.getSize());
							}
						}
					}
				} else {
					throw new IllegalArgumentException("Method argument request must not be null.");
				}
			} catch (Exception e) {
				LOGGER.error("deleteNewFile", e);
			}
		}
		return fileNewList;
	}
}
