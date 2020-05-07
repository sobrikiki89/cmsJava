package web.module.report.model;

import java.util.List;

import module.report.dto.ReportSubmissionDTO;
import module.report.dto.ReportSubmissionSearchCriteria;
import module.report.model.ReportCategory;
import module.report.model.ReportDefinition;
import module.report.model.ReportStatus;
import web.core.model.AbstractForm;

public class ReportSubmissionSearchForm extends AbstractForm {

	private static final long serialVersionUID = 1L;

	private ReportSubmissionSearchCriteria criteria;
	private boolean searched;
	private ReportStatus[] statuses;
	private List<ReportCategory> categoryList;
	private List<ReportDefinition> definitionList;
	private List<ReportSubmissionDTO> submissions;

	public ReportSubmissionSearchCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ReportSubmissionSearchCriteria criteria) {
		this.criteria = criteria;
	}

	public boolean isSearched() {
		return searched;
	}

	public void setSearched(boolean searched) {
		this.searched = searched;
	}

	public ReportStatus[] getStatuses() {
		return statuses;
	}

	public void setStatuses(ReportStatus[] statuses) {
		this.statuses = statuses;
	}

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

	public List<ReportSubmissionDTO> getSubmissions() {
		return submissions;
	}

	public void setSubmissions(List<ReportSubmissionDTO> submissions) {
		this.submissions = submissions;
	}
}
