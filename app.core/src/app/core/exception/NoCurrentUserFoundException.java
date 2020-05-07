package app.core.exception;

public class NoCurrentUserFoundException extends BaseRuntimeException {

	private static final long serialVersionUID = 1L;

	public NoCurrentUserFoundException() {
		super("No current user found.");
	}

	public NoCurrentUserFoundException(Throwable e) {
		super("No current user found", e);
	}
}
