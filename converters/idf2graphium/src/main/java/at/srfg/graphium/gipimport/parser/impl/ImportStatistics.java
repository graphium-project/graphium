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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.impl.sync.TSynchronizedObjectIntMap;
import gnu.trove.map.TFloatIntMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TFloatIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * Created by shennebe on 18.12.2015.
 */
public class ImportStatistics {

    private long nrOfSegmentTasks = 0;
    // export file validation properties
    private int nrOfOnewayInvalid = 0;
    private TObjectIntMap<String> invalidAccessTow;
    private TObjectIntMap<String> invalidAccessBkw;
    public final static String NO_ACCESS_ENTRY = "No Access Type set!";
    private int nrOfOneways = 0;
    private TObjectIntMap<String> accessTow;
    private TObjectIntMap<String> accessBkw;
    private TFloatIntMap levels;
    private int nrOfHalfLanesTow = 0;
    private int nrOfHalfLanesBkw = 0;
    private int nrOfValidLinks = 0;
    private int nrOfNotIntersectingLinks = 0;

    private static final Logger log = LoggerFactory.getLogger(ImportStatistics.class);

    public ImportStatistics() {
        invalidAccessTow = new TSynchronizedObjectIntMap<>(new TObjectIntHashMap<>());
        invalidAccessBkw = new TSynchronizedObjectIntMap<>(new TObjectIntHashMap<>());
        accessTow = new TSynchronizedObjectIntMap<>(new TObjectIntHashMap<>());
        accessBkw = new TSynchronizedObjectIntMap<>(new TObjectIntHashMap<>());
        levels = new TFloatIntHashMap();
    }

    public void resetInstance() {
        invalidAccessTow.clear();
        invalidAccessBkw.clear();
        accessTow.clear();
        accessBkw.clear();
        levels.clear();
        nrOfSegmentTasks = 0;
        // export file validation properties
        nrOfOnewayInvalid = 0;
        nrOfOneways = 0;
        nrOfHalfLanesTow = 0;
        nrOfHalfLanesBkw = 0;
        nrOfValidLinks = 0;
        nrOfNotIntersectingLinks = 0;
    }

    public long getNrOfSegmentTasks() {
        return nrOfSegmentTasks;
    }

    public synchronized void increaseNrOfSegmentTasks() {
        this.nrOfSegmentTasks++;
    }

    public int getNrOfOnewayInvalid() {
        return nrOfOnewayInvalid;
    }

    public synchronized void increaseNrOfOnewayInvalid() {
        this.nrOfOnewayInvalid++;
    }

    public TObjectIntMap<String> getInvalidAccessTow() {
        return invalidAccessTow;
    }

    public void increaseInvalidAccessTow(String key) {
        invalidAccessTow.put(key, invalidAccessTow.get(key) + 1);
    }

    public TObjectIntMap<String> getInvalidAccessBkw() {
        return invalidAccessBkw;
    }

    public void increaseInvalidAccessBkw(String key) {
        invalidAccessBkw.put(key, invalidAccessBkw.get(key) + 1);
    }

    public int getNrOfOneways() {
        return nrOfOneways;
    }

    public synchronized void increaseNrOfOneways() {
        this.nrOfOneways++;
    }

    public TObjectIntMap<String> getAccessTow() {
        return accessTow;
    }

    public void increaseAccessTow(String key) {
        accessTow.put(key, accessTow.get(key) + 1);
    }

    public TObjectIntMap<String> getAccessBkw() {
        return accessBkw;
    }

    public void increaseAccessBkw(String key) {
        accessBkw.put(key, accessBkw.get(key) + 1);
    }

    public TFloatIntMap getLevels() {
        return levels;
    }

    public void increaseLevel(float key) {
        this.levels.put(key,levels.get(key) + 1);
    }

    public int getNrOfHalfLanesTow() {
        return nrOfHalfLanesTow;
    }

    public synchronized void increaseNrOfHalfLanesTow() {
        this.nrOfHalfLanesTow++;
    }

    public int getNrOfHalfLanesBkw() {
        return nrOfHalfLanesBkw;
    }

    public synchronized void increaseNrOfHalfLanesBkw() {
        this.nrOfHalfLanesBkw++;
    }

    public int getNrOfValidLinks() {
        return nrOfValidLinks;
    }

    public synchronized void increaseNrOfValidLinks() {
        this.nrOfValidLinks++;
    }

    public int getNrOfNotIntersectingLinks() {
        return nrOfNotIntersectingLinks;
    }

    public synchronized void increaseNrOfNotIntersectingLinks() {
        this.nrOfNotIntersectingLinks++;
    }

    protected void printValidationResults() {
        log.info("Access types towards:");
        log.info("=====================");
        for (String acc : accessTow.keySet()) {
            log.info("   " + acc + " => " + accessTow.get(acc) + " entries");
        }
        log.info("");
        log.info("Access types backwards:");
        log.info("=====================");
        for (String acc : accessBkw.keySet()) {
            log.info("   " + acc + " => " + accessBkw.get(acc) + " entries");
        }

        log.info("");
        log.info("Levels:");
        log.info("=====================");
        for (float level : levels.keys()) {
            log.info("   " + level + " => " + levels.get(level) + " entries");
        }

        log.info("");
        log.info("Number of oneway links: " + nrOfOneways);
        log.info("=====================");

        log.info("");
        log.info("Number of Oneway = -1: " + nrOfOnewayInvalid);
        log.info("Access types towards with oneway = -1:");
        log.info("=====================");
        for (String acc : invalidAccessTow.keySet()) {
            log.info("   " + acc + " => " + invalidAccessTow.get(acc) + " entries");
        }
        log.info("Access types backwards with oneway = -1:");
        log.info("=====================");
        for (String acc : invalidAccessBkw.keySet()) {
            log.info("   " + acc + " => " + invalidAccessBkw.get(acc) + " entries");
        }
        log.info("");
        log.info("Number of Lanes = 0.5 towards: " + nrOfHalfLanesTow);
        log.info("=====================");
        log.info("Number of Lanes = 0.5 backwards: " + nrOfHalfLanesBkw);
        log.info("=====================");

        log.info("");
        log.info("Number of valid links: " + nrOfValidLinks);
        log.info("=====================");
        log.info("Number of not intersecting links: " + nrOfNotIntersectingLinks);
        log.info("=====================");

        log.info("");
        log.info("Number of corrected link lines: " + AsyncGipLinkParser.getNrCorrectedLines());
        log.info("=====================");
    }

    @Override
    public String toString() {
        return "ImportStatistics{" +
                "nrOfSegmentTasks=" + nrOfSegmentTasks +
                ", nrOfOnewayInvalid=" + nrOfOnewayInvalid +
                ", invalidAccessTow=" + invalidAccessTow +
                ", invalidAccessBkw=" + invalidAccessBkw +
                ", nrOfOneways=" + nrOfOneways +
                ", accessTow=" + accessTow +
                ", accessBkw=" + accessBkw +
                ", levels=" + levels +
                ", nrOfHalfLanesTow=" + nrOfHalfLanesTow +
                ", nrOfHalfLanesBkw=" + nrOfHalfLanesBkw +
                ", nrOfValidLinks=" + nrOfValidLinks +
                ", nrOfNotIntersectingLinks=" + nrOfNotIntersectingLinks +
                '}';
    }
}
