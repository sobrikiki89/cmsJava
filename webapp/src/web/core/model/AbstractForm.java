package web.core.model;

import java.io.Serializable;

public abstract class AbstractForm implements Serializable {
	private static final long serialVersionUID = 1L;

	private String action;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
