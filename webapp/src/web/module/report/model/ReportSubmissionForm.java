package web.module.report.model;

import java.util.List;

import module.report.handler.ReportParamHandler;
import module.report.model.ReportCategory;
import module.report.model.ReportDefinition;
import module.report.model.ReportSubmission;
import web.core.model.AbstractForm;

public class ReportSubmissionForm extends AbstractForm {

	private static final long serialVersionUID = 1L;

	private List<ReportCategory> categoryList;
	private List<ReportDefinition> definitionList;
	private List<String> formats;
	private ReportSubmission submission;
	private ReportParamHandler handler;
	private boolean allowedFlag;

	public List<ReportCategory> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<ReportCategory> categoryList) {
		this.categoryList = categoryList;
	}

	public List<ReportDefinition> getDefinitionList() {
		return definitionList;
	}

	public void setDefinitionList(List<ReportDefinition> definitionList) {
		this.definitionList = definitionList;
	}

	public List<String> getFormats() {
		return formats;
	}

	public void setFormats(List<String> formats) {
		this.formats = formats;
	}

	public ReportSubmission getSubmission() {
		return submission;
	}

	public void setSubmission(ReportSubmission submission) {
		this.submission = submission;
	}

	public ReportParamHandler getHandler() {
		return handler;
	}

	public void setHandler(ReportParamHandler handler) {
		this.handler = handler;
	}

	public boolean isAllowedFlag() {
		return allowedFlag;
	}

	public void setAllowedFlag(boolean allowedFlag) {
		this.allowedFlag = allowedFlag;
	}

}
