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
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.gipimport.model.IImportConfigIdf;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.config.impl.ImportConfig;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

/**
 * Created by shennebe on 11.12.2015.
 */
public class ImportConfigIdf extends ImportConfig implements IImportConfigIdf {

    private int queueSize = 100000;
    private int batchSize = 50000;
    private Set<Integer> frcList = null;
    private Set<Access> accessTypes = null;
    private Integer minFrc = null;
    private Integer maxFrc = null;
    private Polygon bounds = null;
    private TIntSet includedGipIds = null;
    private TIntSet excludedGipIds = null;
    private boolean enableSmallConnections = false;
    private boolean importGip = true;
    private boolean calculatePixelCut = true;
    private boolean extractBusLaneInfo = false;
    private boolean enableFullConnectivity = false;
    private Properties csvConfig; // CSV filename + XInfo factory
    private String csvEncodingName = null;

    public static IImportConfigIdf getConfig(String graphName, String version,
                                         String inputFile) {
        return new ImportConfigIdf(graphName, version, inputFile);
    }

    protected ImportConfigIdf(String graphName, String version, String inputFile) {
        super(graphName, version, inputFile);
    }

    @Override
    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    @Override
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public void setFrcList(Integer... frcList) {
        if (frcList != null) {
            this.frcList = new HashSet<>();
            Collections.addAll(this.frcList, frcList);
        }
    }

    @Override
    public void setAccessTypes(Access... accessTypes) {
        if (accessTypes != null) {
            this.accessTypes = new HashSet<>();
            Collections.addAll(this.accessTypes, accessTypes);
        }
    }

    @Override
    public void setMinFrc(int minFrc) {
        this.minFrc = minFrc;
    }

    @Override
    public void setMaxFrc(int maxFrc) {
        this.maxFrc = maxFrc;
    }

    @Override
    public void setBounds(Polygon bounds) {
        this.bounds = bounds;
    }

    @Override
    public void setIncludedGipIds(Integer... gipIds) {
        if (gipIds != null) {
            this.includedGipIds = new TIntHashSet();
            for (Integer gipId : gipIds) {
                this.includedGipIds.add(gipId);
            }
        }
    }

    @Override
    public void setExcludedGipIds(Integer... gipIds) {
        if (gipIds != null) {
            this.excludedGipIds = new TIntHashSet();
            for (Integer gipId : gipIds) {
                this.excludedGipIds.add(gipId);
            }
        }
    }

    @Override
    public void enableSmallConnections() {
        this.enableSmallConnections = true;
    }

    @Override
    public void setNoPixelCut() {
        this.calculatePixelCut = false;
    }

    @Override
    public void setNoGipImport() {
        this.importGip = false;
    }

    @Override
    public void setExtractBusLaneInfo(boolean extractBusLaneInfo) {
    	this.extractBusLaneInfo = true;
    }
    
    @Override
    public void setCsvConfig(Properties csvConfig) {
		this.csvConfig = csvConfig;
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
	public boolean isExtractBusLaneInfo() {
		return extractBusLaneInfo;
	}

	@Override
	public void enableFullConnectivity() {
		this.enableFullConnectivity = true;
	}

	@Override
	public boolean isEnableFullConnectivity() {
		return enableFullConnectivity;
	}

    @Override
    public Properties getCsvConfig() {
		return csvConfig;
	}

    @Override
	public String getCsvEncodingName() {
		return csvEncodingName;
	}

    @Override
	public void setCsvEncodingName(String csvEncodingName) {
		this.csvEncodingName = csvEncodingName;
	}

	@Override
	public String toString() {
		return "ImportConfigIdf [queueSize=" + queueSize + ", batchSize=" + batchSize + ", frcList=" + frcList
				+ ", accessTypes=" + accessTypes + ", minFrc=" + minFrc + ", maxFrc=" + maxFrc + ", bounds=" + bounds
				+ ", includedGipIds=" + includedGipIds + ", excludedGipIds=" + excludedGipIds
				+ ", enableSmallConnections=" + enableSmallConnections + ", importGip=" + importGip
				+ ", calculatePixelCut=" + calculatePixelCut + ", extractBusLaneInfo=" + extractBusLaneInfo
				+ ", enableFullConnectivity=" + enableFullConnectivity + ", csvConfig=" + csvConfig
				+ ", csvEncodingName=" + csvEncodingName + ", graphName=" + graphName + ", version=" + version
				+ ", originalGraphName=" + originalGraphName + ", originalVersion=" + originalVersion + ", validFrom="
				+ validFrom + ", validTo=" + validTo + ", inputFile=" + inputFile + ", outputDir=" + outputDir
				+ ", downloadDir=" + downloadDir + ", keepDownloadFile=" + keepDownloadFile + ", keepConvertedFile="
				+ keepConvertedFile + ", forceDownload=" + forceDownload + ", importUrl=" + importUrl + "]";
	}

}