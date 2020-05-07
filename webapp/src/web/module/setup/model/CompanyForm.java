package web.module.setup.model;

import java.util.List;

import app.core.domain.setup.model.State;
import module.setup.dto.UserDTO;
import module.setup.model.Company;
import module.setup.model.CompanyDepartment;
import web.core.model.AbstractForm;

public class CompanyForm extends AbstractForm {

	private static final long serialVersionUID = 1L;

	private Company company;

	private List<State> states;

	private Long[] selected;

	private Long[] newUser;

	private List<Long> selectedUser;

	private List<UserDTO> userDTO;

	private boolean permissionFlag;

	private List<CompanyDepartment> departments;

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<State> getStates() {
		return states;
	}

	public void setStates(List<State> states) {
		this.states = states;
	}

	public Long[] getSelected() {
		return selected;
	}

	public void setSelected(Long[] selected) {
		this.selected = selected;
	}

	public Long[] getNewUser() {
		return newUser;
	}

	public void setNewUser(Long[] newUser) {
		this.newUser = newUser;
	}

	public List<Long> getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(List<Long> selectedUser) {
		this.selectedUser = selectedUser;
	}

	public boolean isPermissionFlag() {
		return permissionFlag;
	}

	public void setPermissionFlag(boolean permissionFlag) {
		this.permissionFlag = permissionFlag;
	}

	public List<UserDTO> getUserDTO() {
		return userDTO;
	}

	public void setUserDTO(List<UserDTO> userDTO) {
		this.userDTO = userDTO;
	}

	public List<CompanyDepartment> getDepartments() {
		return departments;
	}

	public void setDepartments(List<CompanyDepartment> departments) {
		this.departments = departments;
	}
}
