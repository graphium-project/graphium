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

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.io.adapter.exception.XInfoNotSupportedException;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.model.IBaseSegment;

/**
 * Generic dao interface for reading and writing XInfo
 *
 * Created by shennebe on 19.10.2016.
 */
public interface IBaseSegmentReadDao {

    void streamBaseConnectionXInfos(String graph, String version, ISegmentOutputFormat<IBaseSegment> outputFormat, String... types) throws GraphNotExistsException, WaySegmentSerializationException, XInfoNotSupportedException;

    void streamBaseSegmentXInfos(String graph, String version, ISegmentOutputFormat<IBaseSegment> outputFormat, String... types) throws GraphNotExistsException, WaySegmentSerializationException, XInfoNotSupportedException;

}
