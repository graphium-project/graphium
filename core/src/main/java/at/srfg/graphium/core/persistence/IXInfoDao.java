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
package at.srfg.graphium.core.persistence;

import java.util.List;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.IXInfo;
import at.srfg.graphium.model.IXInfoModelTypeAware;

/**
 * @author mwimmer
 *
 */
public interface IXInfoDao<X extends IXInfo> extends IXInfoModelTypeAware<X> {
	
	/**
	 * do setup issues like create non existing tables
	 */
	void setup();

	void streamSegments(ISegmentOutputFormat<IBaseSegment> outputFormat, String graphName, String version) throws GraphNotExistsException;
	
	IBaseSegment get(String graphName, String version, long segmentId) throws GraphNotExistsException;
	
	void save(String graphName, String version, X xInfo) throws GraphNotExistsException;
	
	void save(String graphName, String version, List<X> xInfoList) throws GraphNotExistsException;
	
	void update(String graphName, String version, X xInfo) throws GraphNotExistsException;
	
	void update(String graphName, String version, List<X> xInfoList) throws GraphNotExistsException;
	
	void delete(String graphName, String version, X xInfo) throws GraphNotExistsException;
	
	void deleteAll(String graphName, String version) throws GraphNotExistsException;
	
}