/**
 * Graphium Neo4j - Module of Graphium for routing services via Neo4j
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package at.srfg.graphium.routing.model;

import java.util.List;

import at.srfg.graphium.model.IBaseWaySegment;

public interface IRoute<T extends IBaseWaySegment, W> {

	float getLength();
	void setLength(float length);
	
	public int getDuration();
	public void setDuration(int duration);
	
	public W getWeight();
	public void setWeight(W weight);
	
	public IDirectedSegmentSet getPath();
	public void setPath(IDirectedSegmentSet path);
	
	public List<T> getSegments();
	public void setSegments(List<T> segments);

	public int getRuntimeInMs();
	public void setRuntimeInMs(int runtimeInMs);
	
	public String getGraphName();
	public void setGraphName(String graphName);
	
	public String getGraphVersion();
	public void setGraphVersion(String graphVersion);
}
