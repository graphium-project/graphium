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
package at.srfg.graphium.io.inputformat.impl.jackson;

import at.srfg.graphium.io.adapter.impl.*;
import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistry;
import at.srfg.graphium.io.adapter.registry.impl.GenericXInfoAdapterRegistry;
import at.srfg.graphium.io.adapter.registry.impl.SegmentAdapterRegistryImpl;
import at.srfg.graphium.io.adapter.registry.impl.SegmentXInfoAdapterRegistry;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.dto.IWaySegmentDTO;
import at.srfg.graphium.io.dto.impl.BaseSegmentDTOImpl;
import at.srfg.graphium.io.exception.WaySegmentDeserializationException;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.impl.BaseSegment;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shennebe on 10.10.2016.
 */
public class TestGenericQueuingJacksonInputFormat {

    private GenericQueuingJacksonSegmentInputFormat inputFormat;

    private String inputConnectioXInfoText;

    private String inputSegmentXInfoTest;

    private String inputFileName;

    @Before
    public void setup() {

        DefaultConnectionXInfoAdapter adapterXInfo = new DefaultConnectionXInfoAdapter();
        DefaultSegmentXInfoAdapter defaultSegmentXInfoAdapter = new DefaultSegmentXInfoAdapter();
        GenericXInfoAdapterRegistry adapterXInfoRegistry = new GenericXInfoAdapterRegistry<>(Collections.singletonList(adapterXInfo));
        GenericXInfoAdapterRegistry adapterSegmentXInfoRegsitry = new SegmentXInfoAdapterRegistry();
        adapterSegmentXInfoRegsitry.setAdapters(Collections.singletonList(defaultSegmentXInfoAdapter));
        AbstractSegmentDTOAdapter<IBaseSegmentDTO,IBaseSegment> adapter = new BaseSegment2SegmentDTOAdapter<>(BaseSegment.class, BaseSegmentDTOImpl.class);
        adapter.setConnectionXInfoAdapterRegistry(adapterXInfoRegistry);
        adapter.setSegmentXInfoAdapterRegistry(adapterSegmentXInfoRegsitry);
        AbstractSegmentDTOAdapter<IWaySegmentDTO,IWaySegment> adapterWay = new WaySegment2SegmentDTOAdapter<>();
        adapterWay.setConnectionXInfoAdapterRegistry(adapterXInfoRegistry);
        adapterWay.setSegmentXInfoAdapterRegistry(adapterSegmentXInfoRegsitry);
        ISegmentAdapterRegistry<IBaseSegmentDTO,IBaseSegment> adapterRegistry = new SegmentAdapterRegistryImpl<>();
        List adapterList = new ArrayList<>();
        adapterList.add(adapter);
        adapterList.add(adapterWay);
        adapterRegistry.setAdapters(adapterList);
        this.inputFormat = new GenericQueuingJacksonSegmentInputFormat<>(adapterRegistry);
        this.inputConnectioXInfoText = "{\n" +
                "  \"basesegment\" : [ {\n" +
                "    \"id\" : 23045511,\n" +
                "    \"connection\" : [ {\n" +
                "      \"nodeId\" : 18015656,\n" +
                "      \"toSegmentId\" : 23111780,\n" +
                "      \"xInfo\" : {\n" +
                "        \"default\" : [{\n" +
                "          \"graphId\" : null,\n" +
                "          \"nodeChangeFactor\" : 1.0,\n" +
                "          \"probability\" : 1.0,\n" +
                "          \"turnIntoProbability\" : 1.0,\n" +
                "          \"probabilityIndefinite\" : false,\n" +
                "          \"turnIntoProbabilityIndefinite\" : false,\n" +
                "          \"changeFactorIndefinite\" : true\n" +
                "        }]\n" +
                "      }\n" +
                "    }, {\n" +
                "      \"nodeId\" : 18208604,\n" +
                "      \"toSegmentId\" : 23045510,\n" +
                "      \"xInfo\" : {\n" +
                "        \"default\" : [{\n" +
                "          \"graphId\" : null,\n" +
                "          \"nodeChangeFactor\" : 1.0,\n" +
                "          \"probability\" : 1.0,\n" +
                "          \"turnIntoProbability\" : 1.0,\n" +
                "          \"probabilityIndefinite\" : false,\n" +
                "          \"turnIntoProbabilityIndefinite\" : false,\n" +
                "          \"changeFactorIndefinite\" : true\n" +
                "        }]\n" +
                "      }\n" +
                "    } ]\n" +
                "  }]}";
        this.inputSegmentXInfoTest = "{" +
                "  \"waysegment\" : [ {\n" +
                "    \"id\" : 2335498,\n" +
                "    \"connection\" : [ {\n" +
                "      \"nodeId\" : 18143399,\n" +
                "      \"toSegmentId\" : 2220871,\n" +
                "      \"access\" : [ \"MOTORCYCLE\", \"TAXI\", \"MOTOR_COACH\", \"PRIVATE_CAR\", \"PUBLIC_BUS\", \"BIKE\", \"TRUCK\", \"CAMPER\", \"EMERGENCY_VEHICLE\", \"GARBAGE_COLLECTION_VEHICLE\", \"PEDESTRIAN\", \"HIGH_OCCUPATION_CAR\" ]\n" +
                "    }, {\n" +
                "      \"nodeId\" : 18143399,\n" +
                "      \"toSegmentId\" : 23022577,\n" +
                "      \"access\" : [ \"MOTORCYCLE\", \"TAXI\", \"MOTOR_COACH\", \"PRIVATE_CAR\", \"PUBLIC_BUS\", \"BIKE\", \"TRUCK\", \"CAMPER\", \"EMERGENCY_VEHICLE\", \"GARBAGE_COLLECTION_VEHICLE\", \"PEDESTRIAN\", \"HIGH_OCCUPATION_CAR\" ]\n" +
                "    } ],\n" +
                "    \"geometry\" : \"LINESTRING (15.2591964 48.2328322, 15.2592228 48.232945, 15.2592533 48.2331252, 15.2592571 48.2332634)\",\n" +
                "    \"name\" : \"Mampasberg, \",\n" +
                "    \"wayId\" : 2335498,\n" +
                "    \"startNodeIndex\" : 0,\n" +
                "    \"startNodeId\" : 18143399,\n" +
                "    \"endNodeIndex\" : 4,\n" +
                "    \"endNodeId\" : 1581500,\n" +
                "    \"maxSpeedTow\" : 20,\n" +
                "    \"maxSpeedBkw\" : 20,\n" +
                "    \"calcSpeedTow\" : 20,\n" +
                "    \"calcSpeedBkw\" : 20,\n" +
                "    \"lanesTow\" : 1,\n" +
                "    \"lanesBkw\" : 1,\n" +
                "    \"frc\" : 8,\n" +
                "    \"formOfWay\" : \"PART_OF_SINGLE_CARRIAGEWAY\",\n" +
                "    \"accessTow\" : [ \"MOTORCYCLE\", \"TAXI\", \"MOTOR_COACH\", \"PRIVATE_CAR\", \"PUBLIC_BUS\", \"BIKE\", \"TRUCK\", \"CAMPER\", \"EMERGENCY_VEHICLE\", \"GARBAGE_COLLECTION_VEHICLE\", \"PEDESTRIAN\", \"HIGH_OCCUPATION_CAR\" ],\n" +
                "    \"accessBkw\" : [ \"MOTORCYCLE\", \"TAXI\", \"MOTOR_COACH\", \"PRIVATE_CAR\", \"PUBLIC_BUS\", \"BIKE\", \"TRUCK\", \"CAMPER\", \"EMERGENCY_VEHICLE\", \"GARBAGE_COLLECTION_VEHICLE\", \"PEDESTRIAN\", \"HIGH_OCCUPATION_CAR\" ],\n" +
                "    \"tunnel\" : false,\n" +
                "    \"bridge\" : false,\n" +
                "    \"urban\" : true\n" +
                "  }, {\n" +
                "    \"id\" : 701299309,\n" +
                "    \"connection\" : [ {\n" +
                "      \"nodeId\" : 700233724,\n" +
                "      \"toSegmentId\" : 701214252,\n" +
                "      \"access\" : [ \"MOTORCYCLE\", \"TAXI\", \"MOTOR_COACH\", \"PRIVATE_CAR\", \"PUBLIC_BUS\", \"BIKE\", \"TRUCK\", \"CAMPER\", \"EMERGENCY_VEHICLE\", \"GARBAGE_COLLECTION_VEHICLE\", \"PEDESTRIAN\", \"HIGH_OCCUPATION_CAR\" ]\n" +
                "    }, {\n" +
                "      \"nodeId\" : 700233724,\n" +
                "      \"toSegmentId\" : 701214251,\n" +
                "      \"access\" : [ \"MOTORCYCLE\", \"TAXI\", \"MOTOR_COACH\", \"PRIVATE_CAR\", \"PUBLIC_BUS\", \"BIKE\", \"TRUCK\", \"CAMPER\", \"EMERGENCY_VEHICLE\", \"GARBAGE_COLLECTION_VEHICLE\", \"PEDESTRIAN\", \"HIGH_OCCUPATION_CAR\" ]\n" +
                "    }, {\n" +
                "      \"nodeId\" : 700172033,\n" +
                "      \"toSegmentId\" : 701270824,\n" +
                "      \"access\" : [ \"MOTORCYCLE\", \"TAXI\", \"MOTOR_COACH\", \"PRIVATE_CAR\", \"PUBLIC_BUS\", \"BIKE\", \"TRUCK\", \"CAMPER\", \"EMERGENCY_VEHICLE\", \"GARBAGE_COLLECTION_VEHICLE\", \"PEDESTRIAN\", \"HIGH_OCCUPATION_CAR\" ]\n" +
                "    }, {\n" +
                "      \"nodeId\" : 700172033,\n" +
                "      \"toSegmentId\" : 701288137,\n" +
                "      \"access\" : [ \"MOTORCYCLE\", \"TAXI\", \"MOTOR_COACH\", \"PRIVATE_CAR\", \"PUBLIC_BUS\", \"BIKE\", \"TRUCK\", \"CAMPER\", \"EMERGENCY_VEHICLE\", \"GARBAGE_COLLECTION_VEHICLE\", \"PEDESTRIAN\", \"HIGH_OCCUPATION_CAR\" ]\n" +
                "    }, {\n" +
                "      \"nodeId\" : 700172033,\n" +
                "      \"toSegmentId\" : 701349855,\n" +
                "      \"access\" : [ \"MOTORCYCLE\", \"TAXI\", \"MOTOR_COACH\", \"PRIVATE_CAR\", \"PUBLIC_BUS\", \"BIKE\", \"TRUCK\", \"CAMPER\", \"EMERGENCY_VEHICLE\", \"GARBAGE_COLLECTION_VEHICLE\", \"PEDESTRIAN\", \"HIGH_OCCUPATION_CAR\" ]\n" +
                "    } ],\n" +
                "    \"geometry\" : \"LINESTRING (12.6137386 47.5840287, 12.6137054 47.5838083, 12.6136731 47.5836382, 12.6136296 47.5834929, 12.6135299 47.5833487, 12.6135054 47.5833219, 12.6134786 47.5832927, 12.6134251 47.583165)\",\n" +
                "    \"name\" : \"Ascher, \",\n" +
                "    \"wayId\" : 701299309,\n" +
                "    \"startNodeIndex\" : 0,\n" +
                "    \"startNodeId\" : 700233724,\n" +
                "    \"endNodeIndex\" : 8,\n" +
                "    \"endNodeId\" : 700172033,\n" +
                "    \"maxSpeedTow\" : -1,\n" +
                "    \"maxSpeedBkw\" : -1,\n" +
                "    \"calcSpeedTow\" : -1,\n" +
                "    \"calcSpeedBkw\" : -1,\n" +
                "    \"lanesTow\" : -1,\n" +
                "    \"lanesBkw\" : -1,\n" +
                "    \"frc\" : 106,\n" +
                "    \"formOfWay\" : \"PART_OF_SINGLE_CARRIAGEWAY\",\n" +
                "    \"accessTow\" : [ \"PEDESTRIAN\" ],\n" +
                "    \"accessBkw\" : [ \"PEDESTRIAN\" ],\n" +
                "    \"tunnel\" : false,\n" +
                "    \"bridge\" : false,\n" +
                "    \"urban\" : false\n" +
                "  }, {\n" +
                "    \"id\" : 601427505,\n" +
                "    \"xInfo\" : {\n" +
                "      \"default\" : [ {\n" +
                "        \"segmentId\" : 0,\n" +
                "        \"startCutRight\" : -3.8785124543281804E-4,\n" +
                "        \"startCutLeft\" : -3.8785124543281804E-4,\n" +
                "        \"endCutRight\" : 1.3681663434166047E-4,\n" +
                "        \"endCutLeft\" : 1.3681663434166047E-4\n" +
                "      } ]\n" +
                "    },\n" +
                "    \"connection\" : [ {\n" +
                "      \"nodeId\" : 600695152,\n" +
                "      \"toSegmentId\" : 601427504,\n" +
                "      \"access\" : [ \"MOTORCYCLE\", \"TAXI\", \"MOTOR_COACH\", \"PRIVATE_CAR\", \"PUBLIC_BUS\", \"BIKE\", \"TRUCK\", \"CAMPER\", \"EMERGENCY_VEHICLE\", \"GARBAGE_COLLECTION_VEHICLE\", \"PEDESTRIAN\", \"HIGH_OCCUPATION_CAR\" ]\n" +
                "    }, {\n" +
                "      \"nodeId\" : 600695152,\n" +
                "      \"toSegmentId\" : 601354874,\n" +
                "      \"access\" : [ \"MOTORCYCLE\", \"TAXI\", \"MOTOR_COACH\", \"PRIVATE_CAR\", \"PUBLIC_BUS\", \"BIKE\", \"TRUCK\", \"CAMPER\", \"EMERGENCY_VEHICLE\", \"GARBAGE_COLLECTION_VEHICLE\", \"PEDESTRIAN\", \"HIGH_OCCUPATION_CAR\" ]\n" +
                "    }, {\n" +
                "      \"nodeId\" : 600636151,\n" +
                "      \"toSegmentId\" : 601427506,\n" +
                "      \"access\" : [ \"MOTORCYCLE\", \"TAXI\", \"MOTOR_COACH\", \"PRIVATE_CAR\", \"PUBLIC_BUS\", \"BIKE\", \"TRUCK\", \"CAMPER\", \"EMERGENCY_VEHICLE\", \"GARBAGE_COLLECTION_VEHICLE\", \"PEDESTRIAN\", \"HIGH_OCCUPATION_CAR\" ]\n" +
                "    }, {\n" +
                "      \"nodeId\" : 600636151,\n" +
                "      \"toSegmentId\" : 601438378,\n" +
                "      \"access\" : [ \"MOTORCYCLE\", \"TAXI\", \"MOTOR_COACH\", \"PRIVATE_CAR\", \"PUBLIC_BUS\", \"BIKE\", \"TRUCK\", \"CAMPER\", \"EMERGENCY_VEHICLE\", \"GARBAGE_COLLECTION_VEHICLE\", \"PEDESTRIAN\", \"HIGH_OCCUPATION_CAR\" ]\n" +
                "    } ],\n" +
                "    \"geometry\" : \"LINESTRING (14.12403 46.6234275, 14.1238918 46.6235001, 14.1238164 46.6235423, 14.1237393 46.623588, 14.1235818 46.6236706)\",\n" +
                "    \"name\" : \"Wörthersee-Süduferstraße, L96 - Wörthersee Straße\",\n" +
                "    \"wayId\" : 601427505,\n" +
                "    \"startNodeIndex\" : 0,\n" +
                "    \"startNodeId\" : 600695152,\n" +
                "    \"endNodeIndex\" : 5,\n" +
                "    \"endNodeId\" : 600636151,\n" +
                "    \"maxSpeedTow\" : 39,\n" +
                "    \"maxSpeedBkw\" : 39,\n" +
                "    \"calcSpeedTow\" : 39,\n" +
                "    \"calcSpeedBkw\" : 39,\n" +
                "    \"lanesTow\" : 1,\n" +
                "    \"lanesBkw\" : 1,\n" +
                "    \"frc\" : 4,\n" +
                "    \"formOfWay\" : \"PART_OF_SINGLE_CARRIAGEWAY\",\n" +
                "    \"accessTow\" : [ \"MOTORCYCLE\", \"TAXI\", \"MOTOR_COACH\", \"PRIVATE_CAR\", \"PUBLIC_BUS\", \"BIKE\", \"TRUCK\", \"CAMPER\", \"EMERGENCY_VEHICLE\", \"GARBAGE_COLLECTION_VEHICLE\", \"PEDESTRIAN\", \"HIGH_OCCUPATION_CAR\" ],\n" +
                "    \"accessBkw\" : [ \"MOTORCYCLE\", \"TAXI\", \"MOTOR_COACH\", \"PRIVATE_CAR\", \"PUBLIC_BUS\", \"BIKE\", \"TRUCK\", \"CAMPER\", \"EMERGENCY_VEHICLE\", \"GARBAGE_COLLECTION_VEHICLE\", \"PEDESTRIAN\", \"HIGH_OCCUPATION_CAR\" ],\n" +
                "    \"tunnel\" : false,\n" +
                "    \"bridge\" : false,\n" +
                "    \"urban\" : true\n" +
                "  }]}";
        this.inputFileName = "D:\\data\\graphium\\turncalcxinfos.json";
    }

    @Test
    public void testDeserialization() throws WaySegmentDeserializationException {
        InputStream inputStream = new ByteArrayInputStream(this.inputConnectioXInfoText.getBytes(StandardCharsets.UTF_8));
        this.inputFormat.deserialize(inputStream);
    }

    @Test
    public void testDeserializationFromFile() throws IOException, WaySegmentDeserializationException {
        Path path = Paths.get(this.inputFileName);
        InputStream inputStream = Files.newInputStream(path);
        this.inputFormat.deserialize(inputStream);
    }


}
