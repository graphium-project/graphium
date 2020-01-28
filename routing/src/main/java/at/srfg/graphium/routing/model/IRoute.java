package at.srfg.graphium.routing.model;

import java.util.List;

import at.srfg.graphium.model.IBaseWaySegment;

public interface IRoute<T extends IBaseWaySegment, W extends Object> {

	float getLength();
	void setLength(float length);
	
	public int getDuration();
	public void setDuration(int duration);
	
	public W getWeight();
	public void setWeight(W weight);
	
	public List<IDirectedSegment> getPath();
	public void setPath(List<IDirectedSegment> path);
	
	public List<T> getSegments();
	public void setSegments(List<T> segments);

	public int getRuntimeInMs();
	public void setRuntimeInMs(int runtimeInMs);
	
	public String getGraphName();
	public void setGraphName(String graphName);
	
	public String getGraphVersion();
	public void setGraphVersion(String graphVersion);
}
