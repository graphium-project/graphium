package at.srfg.graphium.routing.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;

public interface IRoutingOptions {
	
	List<Coordinate> getCoordinates();
	
	void setCoordinates(List<Coordinate> coordinate);

	void setCriteria(IRoutingCriteria criteria);

	IRoutingCriteria getCriteria();
	
	void setAlgorithm(IRoutingAlgorithm algorithm);
	
	IRoutingAlgorithm getAlgorithm();

	void setTimeout(int timeoutMs);
	
	int getTimeout();

	void setGraphName(String graphName);

	String getGraphName();

	/**
	 * @param version Graph's version. If set routingTimestamp will be ignored. If not set and routingTimestamp not set the current
	 * Graph's version will be selected for routing.
	 */
	void setGraphVersion(String version);
	
	String getGraphVersion();
	
	/**
	 * @param timestamp Timestamp for selecting the correct graph's version (e.g. timestamp will be in past for historical analysis).
	 * Will be ignored if graphVersion is set.
	 */
	void setRoutingTimestamp(LocalDate timestamp);
	
	LocalDate getRoutingTimestamp();
	
	IRoutingMode getMode();

	void setMode(IRoutingMode mode);

	void setTagValueFilters(Map<String, Set<Object>> tagValueFilters);

	Map<String, Set<Object>> getTagValueFilters();

	/**
	 * @param searchDistance Optional max. distance for searching segments (unit depends on spatial ref system)
	 */
	void setSearchDistance(double searchDistance);
	
	double getSearchDistance();
	
	void setAdditionalOptions(Map<String, Object> additionalOptions);
	
	Map<String, Object> getAdditionalOptions();
	
}
