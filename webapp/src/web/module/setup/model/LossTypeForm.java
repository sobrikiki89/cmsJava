package web.module.setup.model;

import module.setup.model.LossType;
import web.core.model.AbstractForm;

public class LossTypeForm extends AbstractForm {

	private static final long serialVersionUID = 1L;

	private String[] selected;

	private LossType lossType;

	public String[] getSelected() {
		return selected;
	}

	public void setSelected(String[] selected) {
		this.selected = selected;
	}

	public LossType getLossType() {
		return lossType;
	}

	public void setLossType(LossType lossType) {
		this.lossType = lossType;
	}
}
