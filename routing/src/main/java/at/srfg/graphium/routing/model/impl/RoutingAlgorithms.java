package at.srfg.graphium.routing.model.impl;

import at.srfg.graphium.routing.exception.RoutingParameterException;
import at.srfg.graphium.routing.model.IRoutingAlgorithm;

public enum RoutingAlgorithms implements IRoutingAlgorithm {

	DIJKSTRA("dijkstra"), BIDIRECTIONAL_DIJKSTRA("bidirectional_dijkstra"), ASTAR("astar"),
	BIDIRECTIONAL_ASTAR("bidirectional_astar"), BELLMAN_FORD("bellman_ford"), KSHORTEST("kshortest"), YEN("yen"),
	MARTIN("martin");

	private String value;

	RoutingAlgorithms(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	public static RoutingAlgorithms fromValue(String value) throws RoutingParameterException {
		for (RoutingAlgorithms mode : RoutingAlgorithms.values()) {
			if (mode.value.equals(value.toLowerCase())) {
				return mode;
			}
		}
		throw new RoutingParameterException(value + " is not a valid value of " + RoutingAlgorithms.class);
	}
}
