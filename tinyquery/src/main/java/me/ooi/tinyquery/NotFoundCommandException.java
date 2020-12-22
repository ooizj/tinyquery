package me.ooi.tinyquery;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class NotFoundCommandException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public NotFoundCommandException() {
		super() ; 
	}
	
	public NotFoundCommandException(String message) {
		super(message) ; 
	}
	
	public NotFoundCommandException(Throwable cause) {
		super(cause) ; 
	}
	
	public NotFoundCommandException(String message, Throwable cause) {
		super(message, cause) ; 
	}

}
