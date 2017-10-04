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
package at.srfg.graphium.gipimport.helper;

import at.srfg.graphium.gipimport.model.IGipLink;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

/**
 * Created by shennebe on 29.07.2015.
 */
public class GipLinkFilter {

    private Integer maxFrc;
    private Integer minFrc;
    private TIntSet includedNumberRange;
    private TIntSet excludedNumberRange;
    private boolean enableSmallConnection;

    public GipLinkFilter(Integer maxFrc, Integer minFrc, TIntSet includedNumberRange, TIntSet excludedNumberRange,
                         boolean enableSmallConnection) {
        this.maxFrc = maxFrc;
        this.minFrc = minFrc;
        this.includedNumberRange = includedNumberRange;
        this.excludedNumberRange = excludedNumberRange;
        this.enableSmallConnection = enableSmallConnection;
    }

    public TLongSet filter(TLongObjectMap<IGipLink> gipLinks) {
        if (this.maxFrc == null
                && this.minFrc == null
                && this.includedNumberRange == null
                && this.excludedNumberRange == null
                && this.enableSmallConnection){
            return gipLinks.keySet();
        }
        TLongSet filterResult = new TLongHashSet();
        for (IGipLink gipLink : gipLinks.valueCollection()){
            boolean isValid = true;
            if (this.maxFrc != null) {
                isValid = gipLink.getFuncRoadClass().getValue() <= this.maxFrc;
            }
            if (isValid && this.minFrc != null) {
                isValid = gipLink.getFuncRoadClass().getValue() >= this.minFrc;
            }
            if (isValid && this.includedNumberRange != null) {
                for (int numberRange : this.includedNumberRange.toArray()) {
                    if (gipLink.getId() >= numberRange * 100000000 && gipLink.getId() < ((numberRange + 1) * 100000000)) {
                        isValid = true;
                        break;
                    }
                    isValid = false;
                }
            }
            if (isValid && this.excludedNumberRange != null) {
                for (int numberRange : this.excludedNumberRange.toArray()) {
                    if (gipLink.getId() >= numberRange * 100000000 && gipLink.getId() < ((numberRange + 1) * 100000000)) {
                        isValid = false;
                        break;
                    }
                }
            }
            if (isValid && !this.enableSmallConnection ) {
                isValid = gipLink.getFuncRoadClassValue() != 0 || gipLink.getLength() > 3.5;
            }
            if (isValid) {
                filterResult.add(gipLink.getId());
            }
        }
        return filterResult;
    }

    @Override
    public String toString() {
        return "GipLinkFilter{" +
                "maxFrc=" + maxFrc +
                ", minFrc=" + minFrc +
                ", includedNumberRange=" + includedNumberRange +
                ", excludedNumberRange=" + excludedNumberRange +
                ", enableSmallConnection=" + enableSmallConnection +
                '}';
    }
}
