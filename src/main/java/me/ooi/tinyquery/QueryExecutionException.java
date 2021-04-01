package me.ooi.tinyquery;

/**
 * @author jun.zhao
 */
public class QueryExecutionException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public QueryExecutionException() {
		super() ; 
	}
	
	public QueryExecutionException(String message) {
		super(message) ; 
	}
	
	public QueryExecutionException(Throwable cause) {
		super(cause) ; 
	}
	
	public QueryExecutionException(String message, Throwable cause) {
		super(message, cause) ; 
	}

}
