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
package at.srfg.graphium.gipimport.model.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.gipimport.model.IImportConfig;
import at.srfg.graphium.model.Access;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

/**
 * Created by shennebe on 11.12.2015.
 */
public class ImportConfig implements IImportConfig {

    private int queueSize = 100000;
    private int batchSize = 50000;
    private Set<Integer> frcList = null;
    private Set<Access> accessTypes = null;
    private Integer minFrc = null;
    private Integer maxFrc = 5;
    private Polygon bounds = null;
    private TIntSet includedGipIds = null;
    private TIntSet excludedGipIds = null;
    private String graphName;
    private String version;
    private String originalGraphName;
    private String originalVersion;
    private Date validFrom;
    private Date validTo;
    private String inputFile;
    private String outputDir;
    private boolean enableSmallConnections = false;
    private boolean importGip = true;
    private boolean calculatePixelCut = true;
    private boolean extractBusLaneInfo = false;

    public static IImportConfig getConfig(String graphName, String version,
                                         String inputIDFFile) {
        return new ImportConfig(graphName,version,inputIDFFile);
    }

    private ImportConfig(String graphName, String version, String inputFile) {
        this.graphName = graphName;
        this.version = version;
        this.inputFile = inputFile;
    }

    @Override
    public IImportConfig outPutDir(String outputDirectory) {
        this.outputDir = outputDirectory;
        return this;
    }

    @Override
    public IImportConfig queueSize(int queueSize) {
        this.queueSize = queueSize;
        return this;
    }

    @Override
    public IImportConfig batchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    @Override
    public IImportConfig frcList(Integer... frcList) {
        if (frcList != null) {
            this.frcList = new HashSet<>();
            Collections.addAll(this.frcList, frcList);
        }
        return this;
    }

    @Override
    public IImportConfig accessTypes(Access... accessTypes) {
        if (accessTypes != null) {
            this.accessTypes = new HashSet<>();
            Collections.addAll(this.accessTypes, accessTypes);
        }
        return this;
    }

    @Override
    public IImportConfig minFrc(int minFrc) {
        this.minFrc = minFrc;
        return this;
    }

    @Override
    public IImportConfig maxFrc(int maxFrc) {
        this.maxFrc = maxFrc;
        return this;
    }

    @Override
    public IImportConfig bounds(Polygon bounds) {
        this.bounds = bounds;
        return this;
    }

    @Override
    public IImportConfig includedGipIds(Integer... gipIds) {
        if (gipIds != null) {
            this.includedGipIds = new TIntHashSet();
            for (Integer gipId : gipIds) {
                this.includedGipIds.add(gipId);
            }
        }
        return this;
    }

    @Override
    public IImportConfig excludedGipIds(Integer... gipIds) {
        if (gipIds != null) {
            this.excludedGipIds = new TIntHashSet();
            for (Integer gipId : gipIds) {
                this.excludedGipIds.add(gipId);
            }
        }
        return this;
    }

    @Override
    public IImportConfig originalGraphName(String originalGraphName){
        this.originalGraphName = originalGraphName;
        return this;
    }

    @Override
    public IImportConfig originalGraphVersion(String originalVersion) {
        this.originalVersion = originalVersion;
        return this;
    }

    @Override
    public IImportConfig validFrom(Date validFrom) {
        this.validFrom = validFrom;
        return this;
    }

    @Override
    public IImportConfig validTo(Date validTo) {
        this.validTo = validTo;
        return this;
    }

    @Override
    public IImportConfig enableSmallConnections() {
        this.enableSmallConnections = true;
        return this;
    }

    @Override
    public IImportConfig noPixelCut() {
        this.calculatePixelCut = false;
        return this;
    }

    @Override
    public IImportConfig noGipImport() {
        this.importGip = false;
        return this;
    }

    @Override
    public IImportConfig extractBusLaneInfo(boolean extractBusLaneInfo) {
    	this.extractBusLaneInfo = true;
    	return this;
    }
    
    @Override
    public int getQueueSize() {
        return queueSize;
    }

    @Override
    public int getBatchSize() {
        return batchSize;
    }

    @Override
    public Set<Integer> getFrcList() {
        return frcList;
    }

    @Override
    public Set<Access> getAccessTypes() {
        return accessTypes;
    }

    @Override
    public Integer getMinFrc() {
        return minFrc;
    }

    @Override
    public Integer getMaxFrc() {
        return maxFrc;
    }

    @Override
    public Polygon getBounds() {
        return bounds;
    }

    @Override
    public TIntSet getIncludedGipIds() {
        return includedGipIds;
    }

    @Override
    public TIntSet getExcludedGipIds() {
        return excludedGipIds;
    }

    @Override
    public String getGraphName() {
        return graphName;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getOriginalGraphName() {
        return originalGraphName;
    }

    @Override
    public String getOriginalVersion() {
        return originalVersion;
    }
    
    @Override
    public Date getValidFrom() {
    	return validFrom;
    }
    
    @Override
    public Date getValidTo() {
    	return validTo;
    }

    @Override
    public boolean isEnableSmallConnections() {
        return enableSmallConnections;
    }

    @Override
    public boolean isImportGip() {
        return importGip;
    }

    @Override
    public boolean isCalculatePixelCut() {
        return calculatePixelCut;
    }

    @Override
    public String getInputFile() {
        return inputFile;
    }

    @Override
    public String getOutputDir() {
        return outputDir;
    }

    @Override
	public boolean isExtractBusLaneInfo() {
		return extractBusLaneInfo;
	}

	@Override
    public String toString() {
        return "ImportConfig{" +
                "queueSize=" + queueSize +
                ", batchSize=" + batchSize +
                ", frcList=" + frcList +
                ", accessTypes=" + accessTypes +
                ", minFrc=" + minFrc +
                ", maxFrc=" + maxFrc +
                ", bounds=" + bounds +
                ", includedGipIds=" + includedGipIds +
                ", excludedGipIds=" + excludedGipIds +
                ", graphName='" + graphName + '\'' +
                ", version='" + version + '\'' +
                ", originalGraphName='" + originalGraphName + '\'' +
                ", originalVersion='" + originalVersion + '\'' +
                ", validFrom='" + validFrom + '\'' +
                ", validTo='" + validTo + '\'' +
                ", inputFile='" + inputFile + '\'' +
                ", outputDir='" + outputDir + '\'' +
                ", enableSmallConnections=" + enableSmallConnections +
                ", importGip=" + importGip +
                ", calculatePixelCut=" + calculatePixelCut +
                ", extractBusLaneInfo=" + extractBusLaneInfo +
                '}';
    }
}
