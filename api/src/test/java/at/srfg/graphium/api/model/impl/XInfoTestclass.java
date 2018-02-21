
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
package at.srfg.graphium.api.model.impl;

import at.srfg.graphium.api.model.IXInfoTest;
import at.srfg.graphium.model.impl.AbstractSegmentXInfo;

/**
 * Salzburg Research ForschungsgesmbH (c) 2018
 *
 * Project: graphium
 * Created by sschwarz on 08.02.2018.
 */
public class XInfoTest extends AbstractSegmentXInfo implements IXInfoTest {

    private long directedId;
    private long graphId;

    public XInfoTest() {
        super("test");
    }

    public XInfoTest(long directedId, boolean directionTow, long graphId, long segmentId){
        super("test");
        this.directedId = directedId;
        this.directionTow = directionTow;
        this.graphId = graphId;
        this.segmentId = segmentId;
    }

    public long getDirectedId() {
        return directedId;
    }

    public void setDirectedId(long directedId) {
        this.directedId = directedId;
    }

    public long getGraphId() {
        return graphId;
    }

    public void setGraphId(long graphId) {
        this.graphId = graphId;
    }

    @Override
    public int hashCode() {
        int result = (int) (directedId ^ (directedId >>> 32));
        result = 31 * result + (int) (graphId ^ (graphId >>> 32));
        return result;
    }
}
