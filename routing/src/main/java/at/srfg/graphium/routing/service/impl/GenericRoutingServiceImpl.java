/**
 * Copyright © 2020 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.srfg.graphium.routing.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.geomutils.GeometryUtils;
import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.routing.algo.IPointToRoutingNodeResolver;
import at.srfg.graphium.routing.algo.IRoutedPath;
import at.srfg.graphium.routing.algo.IRoutingAlgo;
import at.srfg.graphium.routing.algo.IRoutingAlgoFactory;
import at.srfg.graphium.routing.algo.ISegmentIdToRoutingNodeResolver;
import at.srfg.graphium.routing.algo.ISegmentToRoutingNodeResolver;
import at.srfg.graphium.routing.exception.RoutingException;
import at.srfg.graphium.routing.exception.UnkownRoutingAlgoException;
import at.srfg.graphium.routing.model.IDirectedSegment;
import at.srfg.graphium.routing.model.IRoute;
import at.srfg.graphium.routing.model.IRoutingOptions;
import at.srfg.graphium.routing.service.IDirectedSegmentSetAdapterService;
import at.srfg.graphium.routing.service.IRoutingService;

public abstract class GenericRoutingServiceImpl<T extends IBaseWaySegment, N extends Object,
	W extends Object, O extends IRoutingOptions> implements IRoutingService<T, W, O> {

	 // default behaviour: segment cutting is enabled
//	private boolean defaultCutSegments = true;

	protected IPointToRoutingNodeResolver<N> pointToRoutingNodeResolver;
	protected ISegmentToRoutingNodeResolver<T, N> segmentToRoutingNodeResolver;
	protected ISegmentIdToRoutingNodeResolver<N> segmentIdToRoutingNodeResolver;
	protected IRoutingAlgoFactory<O, N, W> routingAlgoFactory;
	protected IDirectedSegmentSetAdapterService<IRoute<T,W>, T, W> toRouteAdapter;
	
	@Override
	public IRoute<T, W> route(O options) throws UnkownRoutingAlgoException, RoutingException {
		Iterator<Coordinate> coordIt = options.getCoordinates().iterator();
		
		Coordinate current;
		Coordinate prev = coordIt.next();
		
		N startNode;
		N endNode;
		float percentageStartWeight = 0;
		float percentageEndWeight = 1;
		LineString startNodeGeometry = null;
		LineString endNodeGeometry = null;
		List<IRoute<T, W>> routeFragments = new ArrayList<>();
		IRoute<T,W> currentRoute;		
		while (coordIt.hasNext()) {
			current = coordIt.next();
			// resolve datasource specific start and end node
			startNode = pointToRoutingNodeResolver.resolveSegment(GeometryUtils.createPoint(prev, options.getOutputSrid()),
																  options.getSearchDistance(), 
																  options.getGraphName(), 
																  options.getGraphVersion());
			endNode = pointToRoutingNodeResolver.resolveSegment(GeometryUtils.createPoint(current, options.getOutputSrid()),
																  options.getSearchDistance(), 
																  options.getGraphName(), 
																  options.getGraphVersion());

			// calculate node's geometries
			startNodeGeometry = getNodeGeometry(startNode);
			endNodeGeometry = getNodeGeometry(endNode);
			
			if (startNodeGeometry == null || endNodeGeometry == null) {
				throw new RoutingException("Could not parse geometry for start or end node!");
			}
			
			// calculate start and end weight percentages by linear referencing points on segment's linestrings
			percentageStartWeight = calculateWeightPercentage(prev, startNodeGeometry);
			percentageEndWeight = calculateWeightPercentage(current, endNodeGeometry);
			
			routeFragments.add(routeFragment(options, startNode, percentageStartWeight, endNode, percentageEndWeight));
		}
		
		currentRoute = assembleRoute(routeFragments);
		
		return currentRoute;
	}

	@Override
	public IRoute<T, W> routePerSegments(O options, List<T> segments) throws UnkownRoutingAlgoException {
		Iterator<T> segmentIt = segments.iterator();
		
		T current = null;
		T prev = segmentIt.next();

		N startNode;
		N endNode;
		float percentageStartWeight = 0;
		float percentageEndWeight = 1;
		List<IRoute<T, W>> routeFragments = new ArrayList<>();
		IRoute<T,W> currentRoute;		
		while (segmentIt.hasNext()) {
			current = segmentIt.next();
			// resolve datasource specific start and end node
			startNode = segmentToRoutingNodeResolver.resolveSegment(prev, 
																  options.getGraphName(), 
																  options.getGraphVersion());
			endNode = segmentToRoutingNodeResolver.resolveSegment(current, 
																  options.getGraphName(), 
																  options.getGraphVersion());
			
			routeFragments.add(routeFragment(options, startNode, percentageStartWeight, endNode, percentageEndWeight));
		}
		
		currentRoute = assembleRoute(routeFragments);
		
		return currentRoute;
	}

	@Override
	public IRoute<T, W> routePerSegmentIds(O options, List<Long> segmentIds) throws UnkownRoutingAlgoException {
		Iterator<Long> segmentIt = segmentIds.iterator();
		
		Long current = null;
		Long prev = segmentIt.next();

		N startNode;
		N endNode;
		float percentageStartWeight = 0;
		float percentageEndWeight = 1;
		List<IRoute<T, W>> routeFragments = new ArrayList<>();
		IRoute<T,W> currentRoute;		
		while (segmentIt.hasNext()) {
			current = segmentIt.next();
			// resolve datasource specific start and end node
			startNode = segmentIdToRoutingNodeResolver.resolveSegment(prev, 
																  options.getGraphName(), 
																  options.getGraphVersion());
			endNode = segmentIdToRoutingNodeResolver.resolveSegment(current, 
																  options.getGraphName(), 
																  options.getGraphVersion());
			
			routeFragments.add(routeFragment(options, startNode, percentageStartWeight, endNode, percentageEndWeight));
		}
		
		currentRoute = assembleRoute(routeFragments);
		
		return currentRoute;
	}
	
	private IRoute<T, W> routeFragment(O options, N startNode, float percentageStartWeight, N endNode, float percentageEndWeight) 
			throws UnkownRoutingAlgoException {
		long timeStart = System.currentTimeMillis();
		long timeEnd = 0;
		
		// TODO: integrate start/end Weights, only on first/last route or on all?
		// mw: weights are necessary on each start and end segment
		IRoutingAlgo<O, N, W> algo = routingAlgoFactory.createInstance(options, startNode, percentageStartWeight, endNode, percentageEndWeight);			
//		algo =  routingAlgoFactory.createInstance(options, startNode, 1.0f, endNode, 1.0f);
		
		IRoute<T,W>	routeFragment = adapt(algo.bestRoute(options, startNode, percentageStartWeight, endNode, percentageEndWeight),
									  options,
									  percentageStartWeight,
									  percentageEndWeight);
		
		timeEnd = System.currentTimeMillis();
		routeFragment.setRuntimeInMs((int)(timeEnd - timeStart));

		return routeFragment;
	}

	protected IRoute<T, W> adapt(IRoutedPath<W> currentPath, O options, float percentageStartWeight,
			float percentageEndWeight) {
		if (currentPath == null) {
			return this.toRouteAdapter.createEmptyRoute();
		} else {
			return this.toRouteAdapter.enrichAndAdapt(currentPath.getSegments(), currentPath.getWeight(), options,
				percentageStartWeight, percentageEndWeight);
		}
	}

	protected IRoute<T, W> assembleRoute(List<IRoute<T, W>> routeFragments) {
		if (routeFragments == null || routeFragments.isEmpty()) {
			return null;
		}
		else if (routeFragments.size() == 1) {
			return routeFragments.get(0);
		}
		else {
			// In case of overlapping / duplicate segments at the ends of two routes we need to remove one of the second route and the referencing directedSegment; 
			// In all cases we need to add the values of length, duration and weight to the first route.
			IRoute<T, W> route = routeFragments.get(0);
			for (int i=1; i<routeFragments.size(); i++) {
				List<T> currentRouteSegments = routeFragments.get(i).getSegments();
				if (route.getSegments() != null && !route.getSegments().isEmpty() &&
					currentRouteSegments != null && !currentRouteSegments.isEmpty()) {
					if (route.getSegments().get(route.getSegments().size()-1).getId() == currentRouteSegments.get(0).getId()) {
						currentRouteSegments.remove(0);
					}
					route.getSegments().addAll(currentRouteSegments);
				}

				List<IDirectedSegment> currentRouteDirectedSegments = routeFragments.get(i).getPath();
				if (route.getPath() != null && !route.getPath().isEmpty() &&
						currentRouteDirectedSegments != null && !currentRouteDirectedSegments.isEmpty()) {
					if (route.getPath().get(route.getPath().size()-1).getId() == currentRouteDirectedSegments.get(0).getId()) {
						currentRouteDirectedSegments.remove(0);
					}
					route.getPath().addAll(currentRouteDirectedSegments);
				}
				
				route.setDuration(route.getDuration() + routeFragments.get(i).getDuration());
				route.setLength(route.getLength() + routeFragments.get(i).getLength());
				
				route.setWeight(sumWeights(route.getWeight(), routeFragments.get(i).getWeight()));
//				//sum up weight only if it is numeric
//				if (route.getWeight() instanceof Number) {
//					double weight1 = NumberUtils.convertNumberToTargetClass((Number)route.getWeight(), Double.class);
//					double weight2 = NumberUtils.convertNumberToTargetClass((Number)route.getWeight(), Double.class);
//					double weight = weight1 + weight2;
//					W w = NumberUtils.convertNumberToTargetClass(weight, W.class);
//					route.setWeight(weight);
//				}
				
				route.setRuntimeInMs(route.getRuntimeInMs() + routeFragments.get(i).getRuntimeInMs());
			}
			return route;
		}
	}

	protected float calculateWeightPercentage(Coordinate coordinate, LineString geometry) {
		return (float) GeometryUtils.offsetOnLineString(coordinate, geometry);
	}

	protected abstract LineString getNodeGeometry(N startNode);

	protected abstract W sumWeights(W weight, W weight2);

	public IPointToRoutingNodeResolver<N> getPointToRoutingNodeResolver() {
		return pointToRoutingNodeResolver;
	}

	public void setPointToRoutingNodeResolver(IPointToRoutingNodeResolver<N> pointToRoutingNodeResolver) {
		this.pointToRoutingNodeResolver = pointToRoutingNodeResolver;
	}

	public IRoutingAlgoFactory<O, N, W> getRoutingAlgoFactory() {
		return routingAlgoFactory;
	}

	public void setRoutingAlgoFactory(IRoutingAlgoFactory<O, N, W> routingAlgoFactory) {
		this.routingAlgoFactory = routingAlgoFactory;
	}

	public IDirectedSegmentSetAdapterService<IRoute<T, W>, T, W> getToRouteAdapter() {
		return toRouteAdapter;
	}

	public void setToRouteAdapter(IDirectedSegmentSetAdapterService<IRoute<T, W>, T, W> toRouteAdapter) {
		this.toRouteAdapter = toRouteAdapter;
	}

	public ISegmentToRoutingNodeResolver<T, N> getSegmentToRoutingNodeResolver() {
		return segmentToRoutingNodeResolver;
	}

	public void setSegmentToRoutingNodeResolver(ISegmentToRoutingNodeResolver<T, N> segmentToRoutingNodeResolver) {
		this.segmentToRoutingNodeResolver = segmentToRoutingNodeResolver;
	}

	public ISegmentIdToRoutingNodeResolver<N> getSegmentIdToRoutingNodeResolver() {
		return segmentIdToRoutingNodeResolver;
	}

	public void setSegmentIdToRoutingNodeResolver(ISegmentIdToRoutingNodeResolver<N> segmentIdToRoutingNodeResolver) {
		this.segmentIdToRoutingNodeResolver = segmentIdToRoutingNodeResolver;
	}

}
