package module.claim.model;

public enum ClaimStatusEnum {
	OPN("Open"), PDOC("Pending Document"), 
	PADJ("Pending Adjuster"), PACC("Pending Acceptance"),
	POFR("Pending Offer"), PPYMT("Pending Payment"),
	LTGN("Litigation"), CPAID("Closed - Paid"), 
	CUEX("Closed - Within Policy Excess"), CDCL("Closed - Declined"), 
	CWDTH("Closed - Withdrawn"), CWPL("Closed - Wrong Policy");

	private String label;

	private ClaimStatusEnum(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public static ClaimStatusEnum getStatus(String label) {
		for (ClaimStatusEnum status : values()) {
			if (status.label.equals(label)) {
				return status;
			}
		}

		return null;
	}
}
