package at.srfg.graphium.routing.algo;

import at.srfg.graphium.routing.model.IDirectedSegmentSet;

public interface IRoutedPath<W> {

	public IDirectedSegmentSet getSegments();
	public W getWeight();
	
}
