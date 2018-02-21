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
package at.srfg.graphium.osmimport.model.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import at.srfg.graphium.osmimport.model.IImportConfig;

public class ImportConfig implements IImportConfig {

    private Set<String> highwayList = null;
    private Set<String> restrictions = null;
    private String boundsFile;
    private String graphName;
    private String version;
    private String originalGraphName;
    private String originalVersion;
    private Date validFrom;
    private Date validTo;
    private String inputFile;
    private String outputDir;
    private int queueSize;
    private int workerThreads;

    public static IImportConfig getConfig(String graphName, String version,
                                         String inputOSMFile) {
        return new ImportConfig(graphName, version, inputOSMFile);
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
    public IImportConfig highwayList(String... highwayList) {
        if (highwayList != null) {
            this.highwayList = new HashSet<>();
            Collections.addAll(this.highwayList, highwayList);
        }
        return this;
    }

    @Override
    public IImportConfig restrictions(String... restrictions) {
        if (restrictions != null) {
            this.restrictions = new HashSet<>();
            Collections.addAll(this.restrictions, restrictions);
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
	public IImportConfig boundsFile(String boundsFile) {
		this.boundsFile = boundsFile;
		return this;
	}
	
	@Override
	public IImportConfig queueSize(int queueSize) {
		this.queueSize = queueSize;
		return this;
	}
	
	@Override
	public IImportConfig workerThreads(int workerThreads) {
		this.workerThreads = workerThreads;
		return this;
	}
	
    @Override
    public Set<String> getHighwayList() {
        return highwayList;
    }

    @Override
    public Set<String> getRestrictions() {
        return restrictions;
    }

	@Override
	public String getBoundsFile() {
		return boundsFile;
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
    public String getInputFile() {
        return inputFile;
    }

    @Override
    public String getOutputDir() {
        return outputDir;
    }
    
    @Override
    public int getQueueSize() {
        return queueSize;
    }
  
    @Override
    public int getWorkerThreads() {
        return workerThreads;
    }

	@Override
	public String toString() {
		return "ImportConfig [highwayList=" + highwayList + ", restrictions=" + restrictions + ", boundsFile=" + boundsFile
				+ ", graphName=" + graphName + ", version=" + version + ", originalGraphName=" + originalGraphName
				+ ", originalVersion=" + originalVersion + ", validFrom='" + validFrom + '\''
                + ", validTo='" + validTo + '\'' + ", inputFile=" + inputFile + ", outputDir=" + outputDir
				+ ", queueSize=" + queueSize + "workerThreads=" + workerThreads + "]";
	}

}