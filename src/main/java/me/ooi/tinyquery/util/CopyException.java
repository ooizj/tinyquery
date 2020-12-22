package me.ooi.tinyquery.util;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class CopyException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public CopyException() {
		super() ; 
	}
	
    public CopyException(String message) {
        super(message);
    }

    public CopyException(String message, Throwable cause) {
        super(message, cause);
    }

    public CopyException(Throwable cause) {
        super(cause);
    }

}
