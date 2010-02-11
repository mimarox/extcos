package net.sf.extcos.exception;

public class UnsuccessfulOperationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnsuccessfulOperationException(){
	}

	public UnsuccessfulOperationException(String message) {
		super(message);
	}

	public UnsuccessfulOperationException(Throwable cause) {
		super(cause);
	}

	public UnsuccessfulOperationException(String message, Throwable cause) {
		super(message, cause);
	}
}
