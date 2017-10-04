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

import at.srfg.graphium.io.dto.impl.DefaultConnectionXInfoDTO;
import at.srfg.graphium.model.impl.DefaultConnectionXInfo;

/**
 * Created by shennebe on 23.09.2016.
 */
public class DefaultConnectionXInfoAdapter extends AbstractXInfoDTOAdapter<DefaultConnectionXInfo,DefaultConnectionXInfoDTO> {

    public DefaultConnectionXInfoAdapter() {
        super(new DefaultConnectionXInfo(), new DefaultConnectionXInfoDTO());
    }

    @Override
    public DefaultConnectionXInfo adapt(DefaultConnectionXInfoDTO objectToAdapt) {
        DefaultConnectionXInfo xInfo = new DefaultConnectionXInfo();
        objectToAdapt.getProperties().forEach(xInfo::setValue);
        return xInfo;
    }

    @Override
    public DefaultConnectionXInfoDTO adaptReverse(DefaultConnectionXInfo objectToAdapt) {
        DefaultConnectionXInfoDTO dto = new DefaultConnectionXInfoDTO();
        objectToAdapt.getValues().forEach(dto::setProperties);
        return dto;
    }
}
