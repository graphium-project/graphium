package at.srfg.graphium.osmimport.model.impl;

public enum OsmTagAdaptionMode {

	NONE("none"), ALL("all");
	
	private String value;
	
	OsmTagAdaptionMode(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static OsmTagAdaptionMode fromValue(String value) {
		for (OsmTagAdaptionMode mode : values()) {
			if (mode.getValue().equals(value)) {
				return mode;
			}
		}
		throw new IllegalArgumentException(value + " is not a valid value for this enumeration");
	}
}
