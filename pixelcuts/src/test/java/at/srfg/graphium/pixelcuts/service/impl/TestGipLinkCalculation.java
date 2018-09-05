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
package at.srfg.graphium.pixelcuts.service.impl;


import org.junit.Assert;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import at.srfg.graphium.pixelcuts.model.IPixelCut;
import at.srfg.graphium.pixelcuts.model.ISegment;
import at.srfg.graphium.pixelcuts.model.impl.SegmentImpl;
import at.srfg.graphium.pixelcuts.service.IRenderingCalculator;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * Test Class for GIP Rendering information calculation
 *
 * Created by shennebe on 30.07.2015.
 */
public class TestGipLinkCalculation {
	
	private int COORDINATE_MULTIPLIER = 10000000;

	@Test
	public void testLinkCalculation() {
        //TODO check and fix test
    }


	//@Test
    public void gipLinkCalculation() throws ParseException {
        WKTReader reader = new WKTReader();
        ISegment gipLink1 = new SegmentImpl();
//        gipLink1.setBridge(false);
//        gipLink1.setTunnel(false);
        gipLink1.setFuncRoadClassValue((short)2);
//        gipLink1.setName1("Rudolf-Biebl-Straße ,B1 - Wiener Straße");
        //"LINESTRING(13.0264126 47.8116185,13.0264047 47.8115003,13.0263937 47.8113386,13.0263772 47.8111459,13.0263706 47.8109774)";
        //gipLink1.setCoordinatesX(new int[]{130264126,130264047,130263937,130263772,130263706});
        //gipLink1.setCoordinatesY(new int[]{478116185,478115003,478113386,478111459,478109774});
        LineString lineString = (LineString) reader.read("LINESTRING(13.0264126 47.8116185,13.0264047 47.8115003,13.0263937 47.8113386,13.0263772 47.8111459,13.0263706 47.8109774)");
        int[] coordsX = new int[lineString.getCoordinates().length];
        int[] coordsY = new int[lineString.getCoordinates().length];
        int i = 0;
        for (Coordinate coordinate : lineString.getCoordinates()) {
            coordsX[i] = (int)Math.round(coordinate.x * COORDINATE_MULTIPLIER);
            coordsY[i] = (int)Math.round(coordinate.y * COORDINATE_MULTIPLIER);
            i++;
        }
        gipLink1.setCoordinatesX(coordsX);
        gipLink1.setCoordinatesY(coordsY);
        gipLink1.setId(901392175);
        gipLink1.setFromNodeId(890000423);
        gipLink1.setToNodeId(100);

        ISegment gipLink2 = new SegmentImpl();
        gipLink2.setFuncRoadClassValue((short)2);
//        gipLink2.setName1("Ignaz-Harrer-Straße ,B1 - Wiener Straße");
        //gipLink2.setGeometry((LineString) reader.read("LINESTRING(1450108.65675829 6075570.249303,1450093.61749508 6075571.89032669)"));
        LineString lineString2 = (LineString) reader.read("LINESTRING(13.0265477 47.8116086,13.0264126 47.8116185)");
        int[] coordsX2 = new int[lineString2.getCoordinates().length];
        int[] coordsY2 = new int[lineString2.getCoordinates().length];
        int i2 = 0;
        for (Coordinate coordinate : lineString2.getCoordinates()) {
            coordsX2[i2] = (int)Math.round(coordinate.x * COORDINATE_MULTIPLIER);
            coordsY2[i2] = (int)Math.round(coordinate.y * COORDINATE_MULTIPLIER);
            i2++;
        }
        gipLink2.setCoordinatesX(coordsX2);
        gipLink2.setCoordinatesY(coordsY2);
        gipLink2.setId(901393455);
        gipLink2.setFromNodeId(890242177);
        gipLink2.setToNodeId(890000423);

        ISegment gipLink3 = new SegmentImpl();
        gipLink3.setFuncRoadClassValue((short)3);
//        gipLink3.setName1("Ignaz-Harrer-Straße ,B155 - Münchener Bundesstraße");
        //gipLink3.setGeometry((LineString) reader.read("LINESTRING(1450093.61749508 6075571.89032669,1450075.11619571 6075573.41531867,1450063.23840605 6075574.3933028)"));
        LineString lineString3 = (LineString) reader.read("LINESTRING(13.0264126 47.8116185,13.0262464 47.8116277,13.0261397 47.8116336)");
        int[] coordsX3 = new int[lineString3.getCoordinates().length];
        int[] coordsY3 = new int[lineString3.getCoordinates().length];
        int i3 = 0;
        for (Coordinate coordinate : lineString3.getCoordinates()) {
            coordsX3[i3] = (int)Math.round(coordinate.x * COORDINATE_MULTIPLIER);
            coordsY3[i3] = (int)Math.round(coordinate.y * COORDINATE_MULTIPLIER);
            i3++;
        }
        gipLink3.setCoordinatesX(coordsX3);
        gipLink3.setCoordinatesY(coordsY3);
        gipLink3.setId(901394179);
        gipLink3.setFromNodeId(890000423);
        gipLink3.setToNodeId(300);

        ISegment gipLink4 = new SegmentImpl();
        gipLink4.setFuncRoadClassValue((short)2);
//        gipLink4.setName1("Ignaz-Harrer-Straße ,B1 - Wiener Straße");
        //gipLink4.setGeometry((LineString) reader.read("LINESTRING(1450271.26113849 6075555.49667994,1450223.90582711 6075560.18767981,1450189.68621564 6075563.20450794,1450170.00492967 6075565.11074603,1450142.08600137 6075566.96725659,1450114.6012191 6075569.60283922,1450108.65675829 6075570.249303)"));
        LineString lineString4 = (LineString) reader.read("LINESTRING(13.0280084 47.8115196,13.0279647 47.8115228,13.027583 47.8115479,13.0272756 47.8115661,13.0270988 47.8115776,13.026848 47.8115888,13.0266011 47.8116047,13.0265477 47.8116086)");
        int[] coordsX4 = new int[lineString4.getCoordinates().length];
        int[] coordsY4 = new int[lineString4.getCoordinates().length];
        int i4 = 0;
        for (Coordinate coordinate : lineString4.getCoordinates()) {
            coordsX4[i4] = (int)Math.round(coordinate.x * COORDINATE_MULTIPLIER);
            coordsY4[i4] = (int)Math.round(coordinate.y * COORDINATE_MULTIPLIER);
            i4++;
        }
        gipLink3.setCoordinatesX(coordsX4);
        gipLink3.setCoordinatesY(coordsY4);
        gipLink4.setId(901394267);
        gipLink4.setFromNodeId(400);
        gipLink4.setToNodeId(890242177);


        TLongObjectHashMap<ISegment> gipLinkTLongObjectHashMap = new TLongObjectHashMap<>();
        gipLinkTLongObjectHashMap.put(gipLink1.getId(),gipLink1);
        gipLinkTLongObjectHashMap.put(gipLink2.getId(), gipLink2);
        gipLinkTLongObjectHashMap.put(gipLink3.getId(), gipLink3);
        gipLinkTLongObjectHashMap.put(gipLink4.getId(), gipLink4);

        TLongObjectHashMap<TLongArrayList> nodeConnectionTable = new TLongObjectHashMap<>();
        TLongArrayList gipLinks = new TLongArrayList();
        gipLinks.add(gipLink1.getId());
        gipLinks.add(gipLink2.getId());
        gipLinks.add(gipLink3.getId());

        TLongArrayList gipLinks2 = new TLongArrayList();
        gipLinks2.add(gipLink2.getId());
        gipLinks2.add(gipLink4.getId());

        nodeConnectionTable.put(890000423, gipLinks);
        nodeConnectionTable.put(890242177, gipLinks2);


        IRenderingCalculator calc = new RenderingCalculatorImpl();

        TLongObjectHashMap<IPixelCut> renderingTLongObjectHashMap = new TLongObjectHashMap<>();

        calc.calculateAllReduceFactors(nodeConnectionTable, gipLinkTLongObjectHashMap, gipLinkTLongObjectHashMap.keySet(),renderingTLongObjectHashMap);

        for (IPixelCut rendering : renderingTLongObjectHashMap.valueCollection()) {
            if (rendering.getSegmentId() == 901393455) {
                System.out.println("Start cut right " + rendering.getStartCutRight());
                System.out.println("Start cut left " + rendering.getStartCutLeft());
                System.out.println("End cut right " + rendering.getEndCutRight());
                System.out.println("End cut left " + rendering.getEndCutLeft());
                Assert.assertEquals(rendering.getStartCutRight(), 1.1150514945647285, 0.0000000000000001);
                Assert.assertEquals(rendering.getStartCutLeft(), 1.1150514945647285, 0.0000000000000001);
                Assert.assertEquals(rendering.getEndCutRight(), -1.0659585387702482, 0.0000000000000001);
                Assert.assertEquals(rendering.getEndCutLeft(), -1.0659585387702482, 0.0000000000000001);
            }
        }
    }
}
