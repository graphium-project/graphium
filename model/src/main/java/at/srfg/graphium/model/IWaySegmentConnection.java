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

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IWaySegmentConnection {

	long getNodeId();

	void setNodeId(long nodeId);

	long getFromSegmentId();

	void setFromSegmentId(long fromSegmentId);

	long getToSegmentId();

	void setToSegmentId(long toSegmentId);

	Set<Access> getAccess();

	void setAccess(Set<Access> access);

	List<IConnectionXInfo> getXInfo();
	
	List<IConnectionXInfo> getXInfo(String type);

	void setXInfo(List<IConnectionXInfo> xInfo);

	void addXInfo(IConnectionXInfo xInfo);
	
	void addXInfo(List<IConnectionXInfo> xInfo);

	Map<String, String> getTags();

	void setTags(Map<String, String> tags);

	void addTag(String key, String value);
	
}