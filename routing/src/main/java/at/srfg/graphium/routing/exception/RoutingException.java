package at.srfg.graphium.routing.exception;

public class RoutingException extends Exception {

	private static final long serialVersionUID = 7262193143538860759L;

	public RoutingException(String msg) {
		super(msg);
	}
	
	public RoutingException(Throwable cause) {
		super(cause);
	}
}
