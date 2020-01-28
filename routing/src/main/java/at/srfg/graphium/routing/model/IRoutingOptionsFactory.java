package at.srfg.graphium.routing.model;

import org.springframework.util.MultiValueMap;

import at.srfg.graphium.routing.exception.RoutingParameterException;


public interface IRoutingOptionsFactory<O extends IRoutingOptions> {
	
	public final static String PARAM_TIMESTAMP = "timestamp";
	public final static String PARAM_ALGO = "algo";
	public final static String PARAM_MODE = "mode";
	public final static String PARAM_CRITERIA = "criteria";
	public final static String PARAM_SEARCHDISTANCE = "searchDistance";
	public final static String PARAM_TIMEOUT = "timeout";
	
	public O newRoutingOptions(String graphName, String graphVersion, 
			String coordsString,
			MultiValueMap<String, String> allParams) throws RoutingParameterException;
}
