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
package at.srfg.graphium.gipimport.model;

import java.util.Properties;
import java.util.Set;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.config.IImportConfig;
import gnu.trove.set.TIntSet;

/**
 * Created by shennebe on 11.12.2015.
 */
public interface IImportConfigIdf extends IImportConfig {
    void setQueueSize(int queueSize);

    void setBatchSize(int batchSize);

    void setFrcList(Integer... frcList);

    void setAccessTypes(Access... accessTypes);

    void setMinFrc(int minFrc);

    void setMaxFrc(int maxFrc);

    void setBounds(Polygon bounds);

    void setIncludedGipIds(Integer... gipIds);

    void setExcludedGipIds(Integer... gipIds);

    void enableSmallConnections();

    void setNoPixelCut();

    void setNoGipImport();

    int getQueueSize();

    int getBatchSize();

    Set<Integer> getFrcList();

    Set<Access> getAccessTypes();

    Integer getMinFrc();

    Integer getMaxFrc();

    Polygon getBounds();

    TIntSet getIncludedGipIds();

    TIntSet getExcludedGipIds();

    boolean isEnableSmallConnections();

    boolean isImportGip();

    boolean isCalculatePixelCut();

    String getInputFile();

    String getOutputDir();

    void setExtractBusLaneInfo(boolean extractBusLaneInfo);
	
	boolean isExtractBusLaneInfo();

	void enableFullConnectivity();
	
	boolean isEnableFullConnectivity();

	Properties getCsvConfig();

	void setCsvConfig(Properties csvConfig);
	
	String getCsvEncodingName();
	
	void setCsvEncodingName(String csvEncodingName);
}
