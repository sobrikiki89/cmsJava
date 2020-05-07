package web.module.setup.model;

import java.util.List;

import app.core.domain.setup.model.State;
import module.setup.model.Insurer;
import web.core.model.AbstractForm;

public class InsurerForm extends AbstractForm {

	private static final long serialVersionUID = 1L;

	private Insurer insurer;

	private List<State> states;

	public Insurer getInsurer() {
		return insurer;
	}

	public void setInsurer(Insurer insurer) {
		this.insurer = insurer;
	}

	public List<State> getStates() {
		return states;
	}

	public void setStates(List<State> states) {
		this.states = states;
	}
}
