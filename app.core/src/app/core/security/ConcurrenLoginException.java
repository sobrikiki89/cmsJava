package app.core.security;

import org.springframework.security.authentication.AccountStatusException;

public class ConcurrenLoginException extends AccountStatusException {

	private static final long serialVersionUID = 1L;

	public ConcurrenLoginException(String msg) {
		super(msg);
	}
	
	public ConcurrenLoginException(String msg, Throwable t) {
		super(msg, t);
	}
}
