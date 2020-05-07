package module.notification.model;

public enum EmailStatus {

	ACTIVE("A", "Active"),

	INACTIVE("I", "Inactive");

	private String code;

	private String desc;

	private EmailStatus(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public static EmailStatus get(String code) {
		for (EmailStatus status : values()) {
			if (status.getCode().equals(code)) {
				return status;
			}
		}
		return null;
	}
}