package app.core.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class BaseApplicationException extends Exception {

	private static final long serialVersionUID = 1L;

	public BaseApplicationException(String message) {
		super(message);
	}

	public BaseApplicationException(String message, Throwable e) {
		super(message, e);
	}
}
