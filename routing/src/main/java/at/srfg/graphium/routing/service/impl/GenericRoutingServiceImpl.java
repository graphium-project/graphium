package at.srfg.graphium.routing.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.algo.IPointToRoutingNodeResolver;
import at.srfg.graphium.routing.algo.IRoutingAlgo;
import at.srfg.graphium.routing.algo.IRoutingAlgoFactory;
import at.srfg.graphium.routing.algo.ISegmentToRoutingNodeResolver;
import at.srfg.graphium.routing.model.IRoute;
import at.srfg.graphium.routing.model.IRoutingOptions;
import at.srfg.graphium.routing.service.IDirectedSegmentSetAdapterService;
import at.srfg.graphium.routing.service.IRoutingService;

public class GenericRoutingServiceImpl<T extends IBaseWaySegment, N extends Object,
	W extends Object, O extends IRoutingOptions> implements IRoutingService<T, W, O> {

	 // default behaviour: segment cutting is enabled
	private boolean defaultCutSegments = true;

	private IPointToRoutingNodeResolver<N> pointToRoutingNodeResolver;
	private ISegmentToRoutingNodeResolver<T, N> segmentToRoutingNodeResolver;
	private IRoutingAlgoFactory<O, N, W> routingAlgoFactory;
	private IDirectedSegmentSetAdapterService<IRoute<T,W>, T, W> toRouteAdapter;
	
	@Override
	public IRoute<T, W> route(O options) {
		
		IRoutingAlgo<O, N, W> algo;
		Iterator<Coordinate> coordIt = options.getCoordinates().iterator();
		
		Coordinate current;
		Coordinate prev = coordIt.next();
		
		N startNode;
		N endNode;
		List<IRoute<T, W>> routeFragments = new ArrayList<>();
		IRoute<T,W> currentRoute;		
		while(coordIt.hasNext()) {
			current = coordIt.next();
			startNode = pointToRoutingNodeResolver.resolveSegment(point, searchDistance, graphName, graphVersion);
			// TODO: integrate start/end Weights, only on first/last route or on all?
//			algo = routingAlgoFactory.createInstance(options, startNode, percentageStartWeight, endNode, percentageEndWeight)			
			algo =  routingAlgoFactory.createInstance(options, startNode, 1.0f, endNode, 1.0f);
			currentRoute = algo.bestRoute(routeOptions, sourceNode, precentageStartWeight, targetNode, percentageEndWeight)
		}
		
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRoute<T, W> route(IRoutingOptions options, List<T> segments) {
		// TODO Auto-generated method stub
		return null;
	}

}
