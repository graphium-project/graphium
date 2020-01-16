package at.srfg.graphium.routing.exception;

import at.srfg.graphium.routing.model.impl.RoutingAlgorithms;

public class UnkownRoutingAlgoException extends Exception {

	private RoutingAlgorithms algo;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnkownRoutingAlgoException(RoutingAlgorithms algo) {
		this.algo = algo;
	}

	public UnkownRoutingAlgoException(RoutingAlgorithms algo, String message, Throwable cause) {
		super(message, cause);
		this.algo = algo;
	}

	public RoutingAlgorithms getAlgo() {
		return this.algo;
	}
}
