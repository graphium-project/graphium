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

package at.srfg.graphium.api.springconfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.exception.GraphViewNotExistsException;
import at.srfg.graphium.core.persistence.IWayGraphReadDao;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.persistence.IWayGraphViewDao;
import at.srfg.graphium.geomutils.GeometryUtils;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.FormOfWay;
import at.srfg.graphium.model.FuncRoadClass;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.IWaySegmentConnection;
import at.srfg.graphium.model.State;
import at.srfg.graphium.model.impl.WayGraph;
import at.srfg.graphium.model.impl.WayGraphVersionMetadata;
import at.srfg.graphium.model.impl.WaySegment;
import at.srfg.graphium.model.impl.WaySegmentConnection;
import at.srfg.graphium.model.management.impl.Source;
import at.srfg.graphium.model.view.IWayGraphView;
import at.srfg.graphium.model.view.impl.WayGraphView;

/**
 * Salzburg Research ForschungsgesmbH (c) 2018
 *
 * Project: graphium
 * Created by sschwarz on 06.02.2018.
 */

//@Configuration
public class MockBeansConfig {
    private static Source source = new Source(815,"neue Testsource");
    private static String graphName = "testgraph";
    private static String graphName2 = "OSM_D";
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private static Date now;
    private static Map<String, String> tags = new HashMap<String, String>(){{
        put("einTestTag","holladrio");
    }};
    private static int graphId = 1;

    private static Set<Access> accessTypes = new HashSet<Access>(Arrays.asList(Access.PRIVATE_CAR));

    //Getter
    public static SimpleDateFormat getDf() {
        return df;
    }

    public static Source getSource() {
        return source;
    }

    public static String getGraphName() {
        return graphName;
    }

    public static String getGraphName2() {
        return graphName2;
    }

    public static Date getNow() {
        return now;
    }

    public static Map<String, String> getTags() {
        return tags;
    }

    public static int getGraphId() {
        return graphId;
    }

    public static Set<Access> getAccessTypes() {
        return accessTypes;
    }

    public static IWayGraphVersionMetadataDao getWayGraphVersionMetadataDaoMock() {
        //define the mock-object using mockito when then
        try {
            now = df.parse("2018-02-01");
            IWayGraphVersionMetadataDao metadataDao = mock(IWayGraphVersionMetadataDao.class);

            List<IWayGraphVersionMetadata> metaDataList = new ArrayList<IWayGraphVersionMetadata>();
            metaDataList.add(new WayGraphVersionMetadata(0, graphId, graphName, "1.0", graphName, "1.0_orig", State.INITIAL, df.parse("2017-03-01"), df.parse("2017-03-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph fuer Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org"));
            metaDataList.add(new WayGraphVersionMetadata(1, graphId, graphName, "2.0", graphName, "1.0_orig", State.INITIAL, df.parse("2017-04-01"), df.parse("2017-04-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph fuer Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org"));
            metaDataList.add(new WayGraphVersionMetadata(2, graphId, graphName, "3.0", graphName, "1.0_orig", State.INITIAL, df.parse("2017-05-01"), df.parse("2017-05-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph fuer Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org"));

            List<IWayGraphVersionMetadata> metaDataList2 = new ArrayList<IWayGraphVersionMetadata>();
            metaDataList2.add(new WayGraphVersionMetadata(0, graphId, graphName2, "1.0", graphName2, "1.0_orig", State.INITIAL, df.parse("2017-03-01"), df.parse("2017-03-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph fuer Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org"));
            metaDataList2.add(new WayGraphVersionMetadata(1, graphId, graphName2, "2.0", graphName2, "1.0_orig", State.INITIAL, df.parse("2017-04-01"), df.parse("2017-04-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph fuer Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org"));
            metaDataList2.add(new WayGraphVersionMetadata(2, graphId, graphName2, "3.0", graphName2, "1.0_orig", State.INITIAL, df.parse("2017-05-01"), df.parse("2017-05-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph fuer Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org"));
            when(metadataDao.getWayGraphVersionMetadataList(graphName)).thenReturn(metaDataList);
            when(metadataDao.getWayGraphVersionMetadataList(graphName2)).thenReturn(metaDataList2);

            List<String> graphList = new ArrayList<String>();
            graphList.add(graphName);
            graphList.add(graphName2);
            doAnswer((Answer<List<String>>) invocation -> {
                return graphList;
            }).when(metadataDao).getGraphs();

            WayGraphVersionMetadata wayGraphVersionMetadata_1 = new WayGraphVersionMetadata(0, graphId, graphName, "1.0", graphName, "1.0_orig", State.INITIAL, df.parse("2017-03-01"), df.parse("2017-03-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph fuer Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");
            WayGraphVersionMetadata wayGraphVersionMetadata_2 = new WayGraphVersionMetadata(0, graphId, graphName, "2.0", graphName, "2.0_orig", State.INITIAL, df.parse("2017-03-01"), df.parse("2017-03-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph fuer Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");
            WayGraphVersionMetadata wayGraphVersionMetadata_3 = new WayGraphVersionMetadata(0, graphId, graphName, "3.0", graphName, "3.0_orig", State.INITIAL, df.parse("2017-03-01"), df.parse("2017-03-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph fuer Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");
            WayGraphVersionMetadata wayGraphVersionMetadata2_1 = new WayGraphVersionMetadata(0, graphId, graphName2, "1.0", graphName2, "1.0_orig", State.INITIAL, df.parse("2017-03-01"), df.parse("2017-03-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph fuer Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");
            WayGraphVersionMetadata wayGraphVersionMetadata2_2 = new WayGraphVersionMetadata(0, graphId, graphName2, "2.0", graphName2, "2.0_orig", State.INITIAL, df.parse("2017-03-01"), df.parse("2017-03-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph fuer Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");
            WayGraphVersionMetadata wayGraphVersionMetadata2_3 = new WayGraphVersionMetadata(0, graphId, graphName2, "3.0", graphName2, "3.0_orig", State.INITIAL, df.parse("2017-03-01"), df.parse("2017-03-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
                    "Graph fuer Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");
            when(metadataDao.getCurrentWayGraphVersionMetadata(graphName)).thenReturn(wayGraphVersionMetadata_3);
            when(metadataDao.getCurrentWayGraphVersionMetadata(graphName2)).thenReturn(wayGraphVersionMetadata2_3);

            when(metadataDao.getWayGraphVersionMetadata(graphName,"1.0")).thenReturn(wayGraphVersionMetadata_1);
            when(metadataDao.getWayGraphVersionMetadata(graphName,"2.0")).thenReturn(wayGraphVersionMetadata_2);
            when(metadataDao.getWayGraphVersionMetadata(graphName,"3.0")).thenReturn(wayGraphVersionMetadata_3);
            when(metadataDao.getWayGraphVersionMetadata(graphName2,"1.0")).thenReturn(wayGraphVersionMetadata2_1);
            when(metadataDao.getWayGraphVersionMetadata(graphName2,"2.0")).thenReturn(wayGraphVersionMetadata2_2);
            when(metadataDao.getWayGraphVersionMetadata(graphName2,"3.0")).thenReturn(wayGraphVersionMetadata2_3);

            when(metadataDao.getWayGraphVersionMetadata(graphName,"1")).thenReturn(wayGraphVersionMetadata_1);
            when(metadataDao.getWayGraphVersionMetadata(graphName,"2")).thenReturn(wayGraphVersionMetadata_2);
            when(metadataDao.getWayGraphVersionMetadata(graphName,"3")).thenReturn(wayGraphVersionMetadata_3);
            when(metadataDao.getWayGraphVersionMetadata(graphName2,"1")).thenReturn(wayGraphVersionMetadata2_1);
            when(metadataDao.getWayGraphVersionMetadata(graphName2,"2")).thenReturn(wayGraphVersionMetadata2_2);
            when(metadataDao.getWayGraphVersionMetadata(graphName2,"3")).thenReturn(wayGraphVersionMetadata2_3);
            return metadataDao;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static IWayGraphReadDao getWayGraphReadDaoMock() {
        IWayGraphReadDao readDao = mock(IWayGraphReadDao.class);

        try {
            doAnswer((Answer<Void>) (InvocationOnMock invocation) -> {
                String viewName = invocation.getArgument(2) + "_view";
                Map<String, String> tags = new HashMap<String, String>() {{
                    put("einTestTag", "holladrio");
                }};
                IWayGraphView wayGraphView = new WayGraphView(viewName, new WayGraph(1, invocation.getArgument(2)), viewName, false, getBoundsAustria(), 1000, 2000, tags);

                List<WaySegment> waySegments = new ArrayList<WaySegment>();

                //create WaySegment 1
                Coordinate c1 = new Coordinate(13.1, 47.1);
                Coordinate c2 = new Coordinate(13.2, 47.2);
                LineString geom = GeometryUtils.createLineString(new Coordinate[]{c1, c2}, 4326);
                long waySegmentID = 1;
                Set<Access> access = new HashSet<Access>();
                access.add(Access.TRUCK);
                access.add(Access.CAR_FERRY);
                List<IWaySegmentConnection> segmentConnections = new ArrayList<IWaySegmentConnection>();
                segmentConnections.add(new WaySegmentConnection(1, 1, 2, access));
                segmentConnections.add(new WaySegmentConnection(1, 2, 3, access));

                //create WaySegment 2
                c1 = new Coordinate(13.3, 47.3);
                c2 = new Coordinate(13.4, 47.4);
                geom = GeometryUtils.createLineString(new Coordinate[]{c1, c2}, 4326);
                waySegmentID = 2;
                access = new HashSet<Access>();
                access.add(Access.TRUCK);
                access.add(Access.CAR_FERRY);
                access.add(Access.EMERGENCY_VEHICLE);
                segmentConnections = new ArrayList<IWaySegmentConnection>();
                segmentConnections.add(new WaySegmentConnection(2, 1, 2, access));
                segmentConnections.add(new WaySegmentConnection(2, 2, 3, access));

                //serialize waySegments-list
                ISegmentOutputFormat outputFormat = invocation.getArgument(0);
                for (WaySegment waySegment : waySegments) {
                    outputFormat.serialize(waySegment);
                }
                return null;
                }).when(readDao).streamSegments(any(ISegmentOutputFormat.class), Mockito.nullable(Polygon.class), anyString(), anyString());
        } catch (GraphNotExistsException | WaySegmentSerializationException e) {
           e.printStackTrace();
        }
        return readDao;
    }

    private static WaySegment createWaySegment(long segmentID, LineString geom, float length, String name, short maxSpeedTow, short maxSpeedBkw, short speedCalcTow, short speedCalcBkw,
                                               short lanesTow, short lanesBkw, FuncRoadClass frc, FormOfWay formOfWay, String streetType, long wayId, long startNodeId, int startNodeIndex,
                                               long endNodeId, int endNodeIndex, Set<Access> accessTow, Set<Access> accessBkw, boolean isTunnel, boolean isBridge, boolean isUrban,
                                               Date timestamp, List<IWaySegmentConnection> cons, Map<String, String> tags, Map<String, List<ISegmentXInfo>> xInfo){
        return new WaySegment(segmentID, geom, length, name, maxSpeedTow, maxSpeedBkw, speedCalcTow, speedCalcBkw,
                lanesTow, lanesBkw, frc, formOfWay, streetType, wayId, startNodeId, startNodeIndex,
                endNodeId, endNodeIndex, accessTow, accessBkw,isTunnel, isBridge, isUrban,
                timestamp, cons, tags, xInfo);
    }

    public static IWayGraphViewDao getWayGraphViewDaoMock(){
        IWayGraphViewDao viewDao = mock(IWayGraphViewDao.class);
        try {
            when(viewDao.getView(anyString())).thenReturn(new WayGraphView());
        } catch (GraphViewNotExistsException e) {
            e.printStackTrace();
        }
        return viewDao;
    }

    public static Polygon getBoundsAustria() {
        WKTReader reader = new WKTReader();
        String poly = "POLYGON((9.5282778 46.3704647,9.5282778 49.023522,17.1625438 49.023522,17.1625438 46.3704647,9.5282778 46.3704647))";
        Polygon bounds = null;
        try {
            bounds = (Polygon) reader.read(poly);
            bounds.setSRID(4326);
        } catch (com.vividsolutions.jts.io.ParseException e) {
            e.printStackTrace();
        }
        return bounds;
    }

}
