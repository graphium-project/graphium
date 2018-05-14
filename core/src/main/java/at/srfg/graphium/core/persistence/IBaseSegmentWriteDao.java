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
import at.srfg.graphium.core.exception.GraphStorageException;
import at.srfg.graphium.model.IBaseSegment;

import java.util.List;

/**
 * Created by shennebe on 07.10.2016.
 */
public interface IBaseSegmentWriteDao {

    void saveConnectionXInfos(List<? extends IBaseSegment> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException;

    void saveConnectionXInfos(List<? extends IBaseSegment> segments, String graphName, String version, List<String> excludedXInfos) throws GraphStorageException, GraphNotExistsException;

    void deleteConnectionXInfos(String graphName, String version, String... types) throws GraphStorageException, GraphNotExistsException;

    void saveSegmentXInfos(List<? extends IBaseSegment> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException;

    void saveSegmentXInfos(List<? extends IBaseSegment> segments, String graphName, String version, List<String> excludedXInfos) throws GraphStorageException, GraphNotExistsException;

    void deleteSegmentXInfos(String graphName, String version, String... types) throws GraphStorageException, GraphNotExistsException;

    void updateConnectionXInfos(List<? extends IBaseSegment> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException;

    void updateSegmentXInfos(List<? extends IBaseSegment> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException;

}
