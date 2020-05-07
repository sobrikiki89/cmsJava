package web.module.setup.model;

import java.util.List;

import module.setup.model.InsuranceClass;
import module.setup.model.InsuranceClassCategory;
import web.core.model.AbstractForm;

public class InsuranceClassForm extends AbstractForm {

	private static final long serialVersionUID = 1L;

	private InsuranceClass insuranceClass;
	
	private List<InsuranceClassCategory> categories; 

	private String[] selected;

	private String categoryCode;
	
	public String[] getSelected() {
		return selected;
	}

	public void setSelected(String[] selected) {
		this.selected = selected;
	}

	public InsuranceClass getInsuranceClass() {
		return insuranceClass;
	}

	public void setInsuranceClass(InsuranceClass insuranceClass) {
		this.insuranceClass = insuranceClass;
	}

	public List<InsuranceClassCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<InsuranceClassCategory> categories) {
		this.categories = categories;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}


}
