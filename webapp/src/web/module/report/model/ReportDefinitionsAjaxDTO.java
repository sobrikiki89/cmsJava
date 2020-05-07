package web.module.report.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

public class ReportDefinitionsAjaxDTO {
	public static class PublicView {
	}

	private String categoryCode;

	private List<ReportDefinitionAjaxDTO> definitions;

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	@JsonView(PublicView.class)
	public List<ReportDefinitionAjaxDTO> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<ReportDefinitionAjaxDTO> definitions) {
		this.definitions = definitions;
	}
}
