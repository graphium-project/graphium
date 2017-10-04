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

public interface IBaseSegment extends Cloneable {

	long getId();

	void setId(long id);

	List<ISegmentXInfo> getXInfo();
	
	List<ISegmentXInfo> getXInfo(String type);

	void setXInfo(List<ISegmentXInfo> xInfo);

	void addXInfo(ISegmentXInfo xInfo);
	
	void addXInfo(List<ISegmentXInfo> xInfo);

	List<IWaySegmentConnection> getCons();

	void setCons(List<IWaySegmentConnection> cons);

	void addCons(List<IWaySegmentConnection> connections);

	Object clone() throws CloneNotSupportedException;
    
}