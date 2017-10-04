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
package at.srfg.graphium.postgis.persistence.impl;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.IBaseSegmentReadDao;
import at.srfg.graphium.core.persistence.IXInfoDao;
import at.srfg.graphium.core.persistence.IXInfoDaoRegistry;
import at.srfg.graphium.io.adapter.exception.XInfoNotSupportedException;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormatFactory;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.IConnectionXInfo;
import at.srfg.graphium.model.ISegmentXInfo;

/**
 * Created by shennebe on 19.10.2016.
 */
public class WayBaseSegmentReadDaoImpl implements IBaseSegmentReadDao {

    private IXInfoDaoRegistry<ISegmentXInfo, IConnectionXInfo> xInfoDaoRegistry;

    private ISegmentOutputFormatFactory<IBaseSegment> segmentOutputFormatFactory;

    @Override
    public void streamBaseConnectionXInfos(String graph, String version, ISegmentOutputFormat<IBaseSegment> outputFormat, String... types)
            throws GraphNotExistsException, WaySegmentSerializationException, XInfoNotSupportedException {
        for (String type : types) {
            this.getConnectionXInfoDao(type).streamSegments(outputFormat,graph,version);
        }
    }

    @Override
    public void streamBaseSegmentXInfos(String graph, String version, ISegmentOutputFormat<IBaseSegment> outputFormat, String... types)
            throws GraphNotExistsException, WaySegmentSerializationException, XInfoNotSupportedException {
        for (String type : types) {
            this.getSegmentXInfoDao(type).streamSegments(outputFormat,graph,version);
        }
    }

    private IXInfoDao<IConnectionXInfo> getConnectionXInfoDao(String type) throws XInfoNotSupportedException {
        IXInfoDao<IConnectionXInfo> xInfoDao = this.xInfoDaoRegistry.getConnectionXInfoDao(type);
        if (xInfoDao == null) {
            throw new XInfoNotSupportedException("No database supported for this XInfo");
        }
        return xInfoDao;
    }

    private IXInfoDao<ISegmentXInfo> getSegmentXInfoDao(String type) throws XInfoNotSupportedException {
        IXInfoDao<ISegmentXInfo> xInfoDao = this.xInfoDaoRegistry.getSegmentXInfoDao(type);
        if (xInfoDao == null) {
            throw new XInfoNotSupportedException("No database supported for this XInfo");
        }
        return xInfoDao;
    }

    public IXInfoDaoRegistry<ISegmentXInfo, IConnectionXInfo> getxInfoDaoRegistry() {
        return xInfoDaoRegistry;
    }

    public void setxInfoDaoRegistry(IXInfoDaoRegistry<ISegmentXInfo, IConnectionXInfo> xInfoDaoRegistry) {
        this.xInfoDaoRegistry = xInfoDaoRegistry;
    }

    public ISegmentOutputFormatFactory<IBaseSegment> getSegmentOutputFormatFactory() {
        return segmentOutputFormatFactory;
    }

    public void setSegmentOutputFormatFactory(ISegmentOutputFormatFactory<IBaseSegment> segmentOutputFormatFactory) {
        this.segmentOutputFormatFactory = segmentOutputFormatFactory;
    }
}
