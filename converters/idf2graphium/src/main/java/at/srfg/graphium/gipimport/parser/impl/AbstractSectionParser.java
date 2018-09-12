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
package at.srfg.graphium.gipimport.parser.impl;

import java.io.BufferedReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.gipimport.parser.IGipParser;
import at.srfg.graphium.gipimport.parser.IGipSectionParser;

/**
 * Created by shennebe on 18.12.2015.
 */
public abstract class AbstractSectionParser<T> implements IGipSectionParser<T> {

    private IGipParser parserReference;

    protected Logger log = LoggerFactory.getLogger(AbstractSectionParser.class);

    public AbstractSectionParser(IGipParser parserReference) {
        this.parserReference = parserReference;
    }

    protected IGipParser getParserReference() {
        return parserReference;
    }

    @Override
    public String parseSection(BufferedReader file) {
        try {
            log.info("phase import " + this.parserReference.getPhase() + " started");
            return parseSectionInternally(file);
        } finally {
            log.info("phase import " + this.parserReference.getPhase() + " finished");
            GipParserImpl.printUsedMemory("");
            this.parserReference.setPhase(IGipParser.PHASE_UNDEFINED);
        }
    }

    public abstract String parseSectionInternally(BufferedReader file);

    /**
     * Do something after Import
     */
    public void postImport() {

    }
}
