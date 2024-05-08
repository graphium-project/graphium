/**
 * Copyright © 2019 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.io.dto.impl;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import at.srfg.graphium.io.dto.IHDRegulatoryElementDTO;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HDRegulatoryElementDTO extends AbstractSegmentXInfoDTO implements IHDRegulatoryElementDTO {

    private long id;
    private Long graphVersionId;
    // TODO: kann hier der groupKey verwendet werden???
    private String type;
    private boolean dynamic = false;
    private boolean fallback = false;
    private Map<String, String> tags;
    private Set<Long> refersIds;
    private Set<Long> refLineIds;

    public HDRegulatoryElementDTO() {
        super();
        this.setDirectionTow(true);
    }

    public HDRegulatoryElementDTO(long id, Long graphVersionId, String type,
                                  Map<String, String> tags) {
        this();
        this.id = id;
        this.graphVersionId = graphVersionId;
        this.type = type;
        //this.geometry = geometry;
        this.tags = tags;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Long getGraphVersionId() {
        return graphVersionId;
    }

    @Override
    public void setGraphVersionId(Long graphVersionId) {
        this.graphVersionId = graphVersionId;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }

    @Override
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    @Override
    public boolean isFallback() {
        return fallback;
    }

    @Override
    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }

    @Override
    public Set<Long> getRefersIds() {
        return refersIds;
    }

    @Override
    public void setRefersIds(Set<Long> refersIds) {
        this.refersIds = refersIds;
    }

    @Override
    public Set<Long> getRefLineIds() {
        return refLineIds;
    }

    @Override
    public void setRefLineIds(Set<Long> refLineIds) {
        this.refLineIds = refLineIds;
    }

    @Override
//    @JsonAnyGetter
    public Map<String, String> getTags() {
        return tags;
    }

    @Override
//    @JsonAnySetter
    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

}
