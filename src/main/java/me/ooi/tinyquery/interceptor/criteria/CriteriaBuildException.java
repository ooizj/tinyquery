package me.ooi.tinyquery.interceptor.criteria;

/**
 * @author jun.zhao
 */
public class CriteriaBuildException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public CriteriaBuildException() {
		super() ; 
	}
	
	public CriteriaBuildException(String message) {
		super(message) ; 
	}
	
	public CriteriaBuildException(Throwable cause) {
		super(cause) ; 
	}
	
	public CriteriaBuildException(String message, Throwable cause) {
		super(message, cause) ; 
	}

}
