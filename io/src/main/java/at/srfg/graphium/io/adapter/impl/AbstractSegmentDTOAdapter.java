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
package at.srfg.graphium.io.adapter.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.srfg.graphium.io.adapter.ISegmentAdapter;
import at.srfg.graphium.io.adapter.exception.XInfoNotSupportedException;
import at.srfg.graphium.io.adapter.registry.IXinfoAdapterRegistry;
import at.srfg.graphium.io.dto.IBaseSegmentConnectionDTO;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.dto.IConnectionXInfoDTO;
import at.srfg.graphium.io.dto.ISegmentXInfoDTO;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.IConnectionXInfo;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.IWaySegmentConnection;

/**
 * Created by shennebe on 27.09.2016.
 */
public abstract class AbstractSegmentDTOAdapter <O extends IBaseSegmentDTO,I extends IBaseSegment>
        implements ISegmentAdapter<O, I> {

    private Class<? extends IBaseSegment> modelClass;
    private Class<? extends IBaseSegmentDTO> dtoClass;

    private IXinfoAdapterRegistry<ISegmentXInfo, ISegmentXInfoDTO> segmentXInfoAdapterRegistry;
    private IXinfoAdapterRegistry<IConnectionXInfo, IConnectionXInfoDTO> connectionXInfoAdapterRegistry;

    public AbstractSegmentDTOAdapter(Class<? extends IBaseSegment> modelClass, Class<? extends IBaseSegmentDTO> dtoClass) {
        this.modelClass = modelClass;
        this.dtoClass = dtoClass;
    }

    protected abstract void setDtoValues(O segmentDTO, I segment);

    protected abstract void setModelValues(I segment, O segmentDTO);

    protected abstract IBaseSegmentConnectionDTO adaptConnectionToDTO(IWaySegmentConnection conn);

    protected abstract IWaySegmentConnection adaptConnectionToModel(IBaseSegmentConnectionDTO conn, long fromSegmentId);

    protected Map<String, List<IConnectionXInfoDTO>> adaptConnXinfoToDTO(
            List<IConnectionXInfo> xInfos) throws XInfoNotSupportedException {
        if (xInfos != null && !xInfos.isEmpty()) {
            final Map<String, List<IConnectionXInfoDTO>> resultMap = new HashMap<>();
            xInfos.forEach((xInfo) -> {
                try {
                    IConnectionXInfoDTO dto = connectionXInfoAdapterRegistry.getObjectForType(xInfo.getXInfoType()).adaptReverse(xInfo);
                    List<IConnectionXInfoDTO> dtoList = resultMap.get(xInfo.getXInfoType());
                    if (dtoList == null) {
                        dtoList = new ArrayList<>();
                        resultMap.put(xInfo.getXInfoType(),dtoList);
                    }
                    dtoList.add(dto);
                } catch (XInfoNotSupportedException e) {
                    //TODO log error? something went wrong and the value is overwrittn.
                }
            });
            return resultMap;
        }
        return null;
    }

    protected Map<String,List<IConnectionXInfo>> adaptConnXinfoToModel(Map<String,List<IConnectionXInfoDTO>> dtos) {
        if (dtos != null && !dtos.isEmpty()) {
            final Map<String,List<IConnectionXInfo>> resultMap = new HashMap<>();
            dtos.forEach((key,valueList) -> {
                final List<IConnectionXInfo> resultList = new ArrayList<>(valueList.size());
                valueList.forEach(value -> {
                    try {
                        resultList.add(connectionXInfoAdapterRegistry.getObjectForType(key).adapt(value));
                    } catch (XInfoNotSupportedException e) {
                        //TODO log ERROR? Throw Exception=
                    }
                });
                resultMap.put(key,resultList);
            });
            return resultMap;
        }
        return null;
    }

    @Override
    public Class<? extends IBaseSegmentDTO> getDtoClass() {
        return dtoClass;
    }

    @Override
    public Class<? extends IBaseSegment> getModelClass() {
        return modelClass;
    }

    public IXinfoAdapterRegistry<ISegmentXInfo, ISegmentXInfoDTO> getSegmentXInfoAdapterRegistry() {
        return segmentXInfoAdapterRegistry;
    }

    public void setSegmentXInfoAdapterRegistry(IXinfoAdapterRegistry<ISegmentXInfo,ISegmentXInfoDTO> segmentXInfoAdapterRegistry) {
        this.segmentXInfoAdapterRegistry = segmentXInfoAdapterRegistry;
    }

    public IXinfoAdapterRegistry<IConnectionXInfo, IConnectionXInfoDTO> getConnectionXInfoAdapterRegistry() {
        return connectionXInfoAdapterRegistry;
    }

    public void setConnectionXInfoAdapterRegistry(IXinfoAdapterRegistry<IConnectionXInfo, IConnectionXInfoDTO> connectionXInfoAdapterRegistry) {
        this.connectionXInfoAdapterRegistry = connectionXInfoAdapterRegistry;
    }
}
