package at.srfg.graphium.routing.model.impl;

import at.srfg.graphium.routing.exception.RoutingParameterException;
import at.srfg.graphium.routing.model.IRoutingCriteria;

public enum RoutingCriteria implements IRoutingCriteria {

	LENGTH("length"), MIN_DURATION("min_duration"), CURRENT_DURATION("current_duration");

	private String value;

	RoutingCriteria(String value) {
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

	public static IRoutingCriteria fromValue(String value) throws RoutingParameterException {
		for (RoutingCriteria format : RoutingCriteria.values()) {
			if (format.value.equals(value.toLowerCase())) {
				return format;
			}
		}
		throw new RoutingParameterException(value + " is not a valid value of " + RoutingCriteria.class);
	}
}
