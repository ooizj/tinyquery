package me.ooi.tinyquery;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class QueryExecutionContextBuildException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public QueryExecutionContextBuildException() {
		super() ; 
	}
	
	public QueryExecutionContextBuildException(String message) {
		super(message) ; 
	}
	
	public QueryExecutionContextBuildException(Throwable cause) {
		super(cause) ; 
	}
	
	public QueryExecutionContextBuildException(String message, Throwable cause) {
		super(message, cause) ; 
	}

}
