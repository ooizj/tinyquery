package me.ooi.tinyquery;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class ServiceNotConfiguredException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ServiceNotConfiguredException() {
		super() ; 
	}
	
	public ServiceNotConfiguredException(String message) {
		super(message) ; 
	}
	
	public ServiceNotConfiguredException(Throwable cause) {
		super(cause) ; 
	}
	
	public ServiceNotConfiguredException(String message, Throwable cause) {
		super(message, cause) ; 
	}

}
