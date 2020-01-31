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
package at.srfg.graphium.routing.algo.impl;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.routing.algo.IRoutedPath;
import at.srfg.graphium.routing.algo.IRoutingAlgo;
import at.srfg.graphium.routing.model.IDirectedSegment;
import at.srfg.graphium.routing.model.IDirectedSegmentSet;
import at.srfg.graphium.routing.model.IRoutingOptions;
import at.srfg.graphium.routing.model.impl.DirectedSegmentImpl;
import at.srfg.graphium.routing.model.impl.DirectedSegmentSetImpl;

/**
 * @author mwimmer
 *
 */
public class MockRoutingAlgoImpl implements IRoutingAlgo<IRoutingOptions, LineString, Double> {

	@Override
	public IRoutedPath<Double> bestRoute(IRoutingOptions routeOptions, LineString sourceNode,
			float precentageStartWeight, LineString targetNode, float percentageEndWeight) {
		
		List<IDirectedSegment> segments = new ArrayList<>();
		segments.add(new DirectedSegmentImpl(1, true));
		segments.add(new DirectedSegmentImpl(2, true));
		segments.add(new DirectedSegmentImpl(3, true));
		
		IDirectedSegmentSet segmentsSet = new DirectedSegmentSetImpl();
		segmentsSet.setId(0);
		segmentsSet.setSegments(segments);
		segmentsSet.setStartCoord(sourceNode.getStartPoint().getCoordinate());
		segmentsSet.setEndCoord(sourceNode.getEndPoint().getCoordinate());
		
		IRoutedPath<Double> routedPath = new DefaultRoutingAlgoResultImpl(segmentsSet, 3d);
		
		return routedPath;
	}

	@Override
	public List<IRoutedPath<Double>> bestRoutes(IRoutingOptions routeOptions, LineString sourceNode,
			float precentageStartWeight, LineString targetNode, float percentageEndWeight, short amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

}
