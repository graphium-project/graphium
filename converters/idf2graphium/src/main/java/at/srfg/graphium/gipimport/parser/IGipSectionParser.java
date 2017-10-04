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
package at.srfg.graphium.gipimport.parser;

import java.io.BufferedReader;

/**
 * Created by shennebe on 18.12.2015.
 */
public interface IGipSectionParser<T> {

    String PHASE_NODE = "node";
    String PHASE_LINK = "link";
    String PHASE_LINKCOORDINATES = "linkCoordinates";
    String PHASE_TURNEDGE = "turnedge";
    String PHASE_LINKUSE = "linkuse";

    String getPhase();

    String parseSection(BufferedReader file);

    void postImport();

    T getResult();
}
