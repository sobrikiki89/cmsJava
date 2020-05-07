package module.notification.object;

public enum NotificationTransaction {
	ClaimAcknowledgment("CL0001", "CLAIM", "Claim Acknowledgment");

	private String code;

	private String type;

	private String desc;

	private NotificationTransaction(String code, String type, String desc) {
		this.code = code;
		this.type = type;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public String getType() {
		return type;
	}

	public String getDesc() {
		return desc;
	}

	public static NotificationTransaction get(String code, String type) {
		for (NotificationTransaction trx : values()) {
			if (trx.getCode().equals(code) && trx.getType().equals(type)) {
				return trx;
			}
		}
		return null;
	}

	public String toString() {
		return name() + "(code=" + code + ", type=" + type + ", desc=" + desc + ")";
	}
}
