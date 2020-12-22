package me.ooi.tinyquery.util;

/**
 * @author jun.zhao
 */
public class PropertyOperationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public PropertyOperationException() {
		super() ; 
	}
	
    public PropertyOperationException(String message) {
        super(message);
    }

    public PropertyOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertyOperationException(Throwable cause) {
        super(cause);
    }

}
