package module.notification.model;

public enum EmailType {
	ALERT("A"), NOTIFICATION("N");

	private String code;

	EmailType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}