package at.srfg.graphium.routing.exception;

public class RoutingParameterException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RoutingParameterException() {}
	
	public RoutingParameterException(String message) {
		super(message);
	}

	public RoutingParameterException(String message, Throwable cause) {
		super(message, cause);
	}

}
