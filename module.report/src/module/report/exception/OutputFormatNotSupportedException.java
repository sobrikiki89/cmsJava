package module.report.exception;

public class OutputFormatNotSupportedException extends Exception {

	private static final long serialVersionUID = 1L;

	private String message;

	public OutputFormatNotSupportedException(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}
}
