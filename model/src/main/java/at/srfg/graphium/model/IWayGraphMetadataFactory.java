/**
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.model;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.Polygon;

/**
 * factory for production of @see @link IGraphMetadata objects
 *
 * @author <a href="mailto:andreas.wagner@salzburgresearch.at">Andreas Wagner</a>
 *
 */
public interface IWayGraphMetadataFactory {
	
	/**
	 * create new empty metadata
	 * @return empty metadata object
	 */
	public IWayGraphVersionMetadata newWayGraphVersionMetadata();

	/**
	 * create new metadata with given values
	 * 
	 * @param id id of graph version
	 * @param graphId graphId of graph version
	 * @param graphName name of the graph
	 * @param version version of the graph
	 * @param originGraphName name of the original graph (could be different from graphName in case of replication)
	 * @param originVersion version of the original graph (could be different from graphName in case of replication)
	 * @param state state of the graph version
	 * @param validFrom min value for validity
	 * @param validTo max value for validity
	 * @param coveredArea geographical area covered by graph version
	 * @param segmentsCount number of segments within graph version
	 * @param connectionsCount number of connections between segments within graph version
	 * @param accessTypes all kinds of access types within graph version
	 * @param tags optional tags
	 * @param source graph's source
	 * @param type type of graph (e.g. routing...)
	 * @param description optional description
	 * @param creationTimestamp timestamp of creating the graph version (mainly timestamp of beginning the import)
	 * @param storageTimestamp timestamp of end of storing the graph version
	 * @param creator creator of the graph version
	 * @param originUrl URL of graph's base data
	 * @return
	 */
	public IWayGraphVersionMetadata newWayGraphVersionMetadata(long id, long graphId, String graphName, String version,
			String originGraphName, String originVersion, State state, Date validFrom, Date validTo, Polygon coveredArea,
			int segmentsCount, int connectionsCount, Set<Access> accessTypes,
			Map<String, String> tags, ISource source, String type,
			String description, Date creationTimestamp, Date storageTimestamp,
			String creator, String originUrl);
}
