package web.module.setup.model;

import com.fasterxml.jackson.annotation.JsonView;

public class DepartmentAjaxDTO {
	public static class PublicView {
	}

	private Long companyId;

	private String code;

	private String name;

	private String status;

	@JsonView(PublicView.class)
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@JsonView(PublicView.class)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonView(PublicView.class)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@JsonView(PublicView.class)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}