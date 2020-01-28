package at.srfg.graphium.routing.model.impl;

import at.srfg.graphium.routing.exception.RoutingParameterException;
import at.srfg.graphium.routing.model.IRoutingMode;

// TODO: Erweitern!

public enum RoutingMode implements IRoutingMode {
	CAR("car"), BIKE("bike"), PEDESTRIAN("pedestrian"), PEDESTRIAN_BARRIERFREE("pedestrian_barrierfree");

	private String value;

	RoutingMode(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

	public static RoutingMode fromValue(String value) throws RoutingParameterException {
		for (RoutingMode mode : RoutingMode.values()) {
			if (mode.value.equals(value.toLowerCase())) {
				return mode;
			}
		}
		throw new RoutingParameterException(value + " is not a valid value of " + RoutingMode.class);
	}
}
