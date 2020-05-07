package web.module.claim.model;

import web.core.model.AbstractForm;

public class ClaimDeleteForm extends AbstractForm {
	private static final long serialVersionUID = 1L;

	private Long claimId;

	private String note;

	public Long getClaimId() {
		return claimId;
	}

	public void setClaimId(Long claimId) {
		this.claimId = claimId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
