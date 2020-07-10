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
import java.util.HashSet;
import java.util.Set;

import at.srfg.graphium.model.config.impl.ImportConfig;
import at.srfg.graphium.osmimport.model.IImportConfigOsm;

public class ImportConfigOsm extends ImportConfig implements IImportConfigOsm {

    private Set<String> highwayList = null;
    private Set<String> restrictions = null;
    private String boundsFile;
    private int queueSize;
    private int workerThreads;
    private OsmTagAdaptionMode tagAdaptionMode;

    public static IImportConfigOsm getConfig(String graphName, String version,
            String inputFile) {
    	return new ImportConfigOsm(graphName, version, inputFile);
	}

    protected ImportConfigOsm(String graphName, String version, String inputFile) {
        super(graphName, version, inputFile);
    }
    
    @Override
    public void setHighwayList(String... highwayList) {
        if (highwayList != null) {
            this.highwayList = new HashSet<>();
            Collections.addAll(this.highwayList, highwayList);
        }
    }

    @Override
    public void setRestrictions(String... restrictions) {
        if (restrictions != null) {
            this.restrictions = new HashSet<>();
            Collections.addAll(this.restrictions, restrictions);
        }
    }

    @Override
	public void setBoundsFile(String boundsFile) {
		this.boundsFile = boundsFile;
	}
	
	@Override
	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}
	
	@Override
	public void setWorkerThreads(int workerThreads) {
		this.workerThreads = workerThreads;
	}
	
	@Override
	public void setTagAdaptionMode(OsmTagAdaptionMode tagAdaptionMode) {
		this.tagAdaptionMode = tagAdaptionMode;
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
    public int getQueueSize() {
        return queueSize;
    }
  
    @Override
    public int getWorkerThreads() {
        return workerThreads;
    }
    
    @Override
    public OsmTagAdaptionMode getTagAdaptionMode() {
        return tagAdaptionMode;
    }

	@Override
	public String toString() {
		return "ImportConfigOsm [highwayList=" + highwayList + ", restrictions=" + restrictions + ", boundsFile="
				+ boundsFile + ", queueSize=" + queueSize + ", workerThreads=" + workerThreads + ", tagAdaptionMode="
				+ tagAdaptionMode + ", graphName=" + graphName + ", version=" + version + ", originalGraphName="
				+ originalGraphName + ", originalVersion=" + originalVersion + ", validFrom=" + validFrom + ", validTo="
				+ validTo + ", inputFile=" + inputFile + ", outputDir=" + outputDir + ", downloadDir=" + downloadDir
				+ ", keepDownloadFile=" + keepDownloadFile + ", keepConvertedFile=" + keepConvertedFile
				+ ", forceDownload=" + forceDownload + ", importUrl=" + importUrl + "]";
	}

}