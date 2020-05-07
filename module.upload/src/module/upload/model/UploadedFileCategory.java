package module.upload.model;

public enum UploadedFileCategory {
	
	CLAIM("Claim"), POLICY("Policy");
	
	private String label;

	private UploadedFileCategory(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public static UploadedFileCategory getStatus(String label) {
		for (UploadedFileCategory status : values()) {
			if (status.label.equals(label)) {
				return status;
			}
		}

		return null;
	}
}
