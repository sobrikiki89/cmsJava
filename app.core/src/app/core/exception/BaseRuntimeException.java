package app.core.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class BaseRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BaseRuntimeException(String message) {
		super(message);
	}

	public BaseRuntimeException(String message, Throwable e) {
		super(message, e);
	}
}
