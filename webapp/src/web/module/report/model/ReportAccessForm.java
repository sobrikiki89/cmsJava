package web.module.report.model;

import java.util.List;

import app.core.usermgmt.model.Role;
import module.report.model.ReportAccessControl;
import module.report.model.ReportCategory;
import module.report.model.ReportDefinition;
import web.core.model.AbstractForm;

public class ReportAccessForm extends AbstractForm {

	private static final long serialVersionUID = 1L;

	private ReportAccessControl accessControl;
	private List<ReportCategory> categoryList;
	private List<ReportDefinition> definitionList;
	private List<Role> roleList;

	public ReportAccessControl getAccessControl() {
		return accessControl;
	}

	public void setAccessControl(ReportAccessControl accessControl) {
		this.accessControl = accessControl;
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

	public List<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}
}
