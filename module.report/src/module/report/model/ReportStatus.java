package module.report.model;

public enum ReportStatus {
	SUBMITTED("Submitted"), FINISHED("Finished"), NO_DATA_FOUND("No Data Found"), ERROR("Error");

	private String label;

	private ReportStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public static ReportStatus getStatus(String label) {
		for (ReportStatus status : values()) {
			if (status.label.equals(label)) {
				return status;
			}
		}
		return null;
	}
}
