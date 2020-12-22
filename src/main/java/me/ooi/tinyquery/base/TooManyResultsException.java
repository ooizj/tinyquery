package me.ooi.tinyquery.base;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TooManyResultsException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public TooManyResultsException() {
		super() ; 
	}
	
	public TooManyResultsException(String message) {
		super(message) ; 
	}
	
	public TooManyResultsException(Throwable cause) {
		super(cause) ; 
	}
	
	public TooManyResultsException(String message, Throwable cause) {
		super(message, cause) ; 
	}

}
