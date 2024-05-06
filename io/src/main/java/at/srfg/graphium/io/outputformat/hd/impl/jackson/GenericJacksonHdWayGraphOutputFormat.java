/**
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.srfg.graphium.io.outputformat.hd.impl.jackson;

import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.io.dto.IGraphVersionMetadataDTO;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.io.outputformat.hd.IHdWayGraphOutputFormat;
import at.srfg.graphium.io.outputformat.impl.jackson.GenericJacksonWayGraphOutputFormat;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.hd.IHDArea;
import at.srfg.graphium.model.hd.IHDInfraAndSigns;
import at.srfg.graphium.model.hd.IHDWaySegment;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author mwimmer
 *
 */
public class GenericJacksonHdWayGraphOutputFormat<T extends IHDWaySegment>
        extends GenericJacksonWayGraphOutputFormat<T> implements IHdWayGraphOutputFormat<T> {

    ISegmentOutputFormat<IHDArea> areaOutputFormat;
    ISegmentOutputFormat<IHDInfraAndSigns> roadInfraOutputFormat;

    boolean segmentSectionFinished = false;
    boolean areaSectionFinished = false;
    boolean roadInfraSectionFinished = false;

    public GenericJacksonHdWayGraphOutputFormat(
            ISegmentOutputFormat<T> segmentOutputFormat, ISegmentOutputFormat<IHDArea> areaOutputFormat,
            ISegmentOutputFormat<IHDInfraAndSigns> roadInfraOutputFormat,
            IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> adapter, OutputStream stream,
            JsonGenerator generator) {
        super(segmentOutputFormat, adapter, stream, generator);
        this.areaOutputFormat = areaOutputFormat;
        this.roadInfraOutputFormat = roadInfraOutputFormat;
    }

    @Override
    public void finishSegments() throws WaySegmentSerializationException {
        segmentOutputFormat.close();
        segmentSectionFinished = true;
    }

    @Override
    public void finishAreas() throws WaySegmentSerializationException {
        areaOutputFormat.close();
        areaSectionFinished = true;
    }

    @Override
    public void finishRoadInfrastructure() throws WaySegmentSerializationException {
        roadInfraOutputFormat.close();
        roadInfraSectionFinished = true;
    }

    @Override
    public void serialize(IHDArea area) throws WaySegmentSerializationException {
        areaOutputFormat.serialize(area);
    }

    @Override
    public void serialize(IHDInfraAndSigns roadInfra) throws WaySegmentSerializationException {
        roadInfraOutputFormat.serialize(roadInfra);
    }

    @Override
    public void close() throws WaySegmentSerializationException {
        try {
            if(!segmentSectionFinished) {
                segmentOutputFormat.close();
            } else if (!areaSectionFinished) {
                areaOutputFormat.close();
            } else if (!roadInfraSectionFinished) {
                roadInfraOutputFormat.close();
            }

            if(metadataToSerialize != null) {
                doSerializeMetadata(metadataToSerialize);
                metadataToSerialize = null;
            }
            generator.writeEndObject();
            generator.flush();
        } catch (IOException e) {
            throw new WaySegmentSerializationException(e.getMessage(), e);
        }
    }

}
