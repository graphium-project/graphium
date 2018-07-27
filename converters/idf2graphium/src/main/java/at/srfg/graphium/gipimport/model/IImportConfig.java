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

import java.util.Date;
import java.util.Set;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.model.Access;
import gnu.trove.set.TIntSet;

/**
 * Created by shennebe on 11.12.2015.
 */
public interface IImportConfig {
    IImportConfig outPutDir(String outputDirectory);

    IImportConfig queueSize(int queueSize);

    IImportConfig batchSize(int batchSize);

    IImportConfig frcList(Integer... frcList);

    IImportConfig accessTypes(Access... accessTypes);

    IImportConfig minFrc(int minFrc);

    IImportConfig maxFrc(int maxFrc);

    IImportConfig bounds(Polygon bounds);

    IImportConfig includedGipIds(Integer... gipIds);

    IImportConfig excludedGipIds(Integer... gipIds);

    IImportConfig originalGraphName(String originalGraphName);

    IImportConfig originalGraphVersion(String originalVersion);

    IImportConfig enableSmallConnections();

    IImportConfig noPixelCut();

    IImportConfig noGipImport();

    int getQueueSize();

    int getBatchSize();

    Set<Integer> getFrcList();

    Set<Access> getAccessTypes();

    Integer getMinFrc();

    Integer getMaxFrc();

    Polygon getBounds();

    TIntSet getIncludedGipIds();

    TIntSet getExcludedGipIds();

    String getGraphName();

    String getVersion();

    String getOriginalGraphName();

    String getOriginalVersion();

    boolean isEnableSmallConnections();

    boolean isImportGip();

    boolean isCalculatePixelCut();

    String getInputFile();

    String getOutputDir();

	IImportConfig extractBusLaneInfo(boolean extractBusLaneInfo);
	
	boolean isExtractBusLaneInfo();

	IImportConfig validTo(Date validTo);

	IImportConfig validFrom(Date validFrom);

	Date getValidFrom();

	Date getValidTo();
	
	IImportConfig enableFullConnectivity();
	
	boolean isEnableFullConnectivity();
}
