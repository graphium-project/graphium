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
package at.srfg.graphium.pixelcuts.adapter.impl;

import at.srfg.graphium.io.adapter.impl.AbstractXInfoDTOAdapter;
import at.srfg.graphium.pixelcuts.dto.impl.PixelCutDTO;
import at.srfg.graphium.pixelcuts.model.impl.PixelCut;

/**
 * @author mwimmer
 *
 */
public class PixelCutAdapter extends AbstractXInfoDTOAdapter<PixelCut, PixelCutDTO> {

    public PixelCutAdapter() {
        super(new PixelCut(), new PixelCutDTO());
    }

    @Override
    public PixelCut adapt(PixelCutDTO objectToAdapt) {
    	PixelCut segmentXInfo = new PixelCut();
        segmentXInfo.setDirectionTow(objectToAdapt.getDirectionTow());
        segmentXInfo.setSegmentId(objectToAdapt.getSegmentId());
        segmentXInfo.setGraphVersionId(objectToAdapt.getGraphVersionId());
        segmentXInfo.setEndCutLeft(objectToAdapt.getEndCutLeft());
        segmentXInfo.setEndCutRight(objectToAdapt.getEndCutRight());
        segmentXInfo.setStartCutLeft(objectToAdapt.getStartCutLeft());
        segmentXInfo.setStartCutRight(objectToAdapt.getStartCutRight());
        
        return segmentXInfo;
    }

    @Override
    public PixelCutDTO adaptReverse(PixelCut xInfo) {
    	PixelCutDTO dto = new PixelCutDTO();
        dto.setDirectionTow(xInfo.isDirectionTow());
        dto.setSegmentId(xInfo.getSegmentId());
        dto.setGraphVersionId(xInfo.getGraphVersionId());
        dto.setEndCutLeft(xInfo.getEndCutLeft());
        dto.setEndCutRight(xInfo.getEndCutRight());
        dto.setStartCutLeft(xInfo.getStartCutLeft());
        dto.setStartCutRight(xInfo.getStartCutRight());
        return dto;
    }

}
