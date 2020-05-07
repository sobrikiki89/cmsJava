package web.module.report.model;

import com.fasterxml.jackson.annotation.JsonView;

public class ReportDefinitionAjaxDTO {
	public static class PublicView {
	}

	private Long id;

	private String name;

	@JsonView(PublicView.class)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonView(PublicView.class)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
