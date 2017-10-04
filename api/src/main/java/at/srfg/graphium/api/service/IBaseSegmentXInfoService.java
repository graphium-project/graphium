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
package at.srfg.graphium.api.service;

import java.io.InputStream;
import java.io.OutputStream;

import at.srfg.graphium.core.exception.GraphImportException;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.exception.GraphStorageException;
import at.srfg.graphium.io.adapter.exception.XInfoNotSupportedException;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.model.IBaseSegment;

/**
 * @author anwagner
 *
 */
public interface IBaseSegmentXInfoService<T extends IBaseSegment> {


    void streamBaseSegmentXInfos(String graph, String version, OutputStream outputStream, String... types) throws XInfoNotSupportedException, GraphNotExistsException, WaySegmentSerializationException;

    void streamBaseConnectionXInfos(String graph, String version, OutputStream outputStream,  String... types) throws XInfoNotSupportedException, GraphNotExistsException, WaySegmentSerializationException;

    void streamBaseSegmentXInfos(String graph, String version, InputStream inputStream) throws XInfoNotSupportedException, GraphImportException, GraphNotExistsException, GraphStorageException;

    void streamBaseConnectionXInfos(String graph, String version,InputStream inputStream) throws XInfoNotSupportedException, GraphImportException, GraphStorageException, GraphNotExistsException;

    void deleteBaseSegmentXInfos(String graph, String version, String... types) throws XInfoNotSupportedException, GraphNotExistsException, GraphStorageException;

    void deleteConnectionXInfos(String graph, String version, String... types) throws XInfoNotSupportedException, GraphNotExistsException, GraphStorageException;
}
