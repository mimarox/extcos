package net.sf.extcos.exception;

public class StateChangedException extends RuntimeException {
	private static final long serialVersionUID = -2362560635395599571L;

	public StateChangedException() {
	}

	public StateChangedException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public StateChangedException(String s) {
		super(s);
	}

	public StateChangedException(Throwable throwable) {
		super(throwable);
	}
}