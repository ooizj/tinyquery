package me.ooi.tinyquery;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class QueryBuildException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public QueryBuildException() {
		super() ; 
	}
	
	public QueryBuildException(String message) {
		super(message) ; 
	}
	
	public QueryBuildException(Throwable cause) {
		super(cause) ; 
	}
	
	public QueryBuildException(String message, Throwable cause) {
		super(message, cause) ; 
	}

}
