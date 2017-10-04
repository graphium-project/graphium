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
/**
 * (C) 2016 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.osmimport;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.openstreetmap.osmosis.pbf2.v0_6.PbfReader;
import org.openstreetmap.osmosis.tagfilter.v0_6.TagFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.osmimport.reader.pbf.DummySink;
import at.srfg.graphium.osmimport.reader.pbf.SegmentationNodesSink;

/**
 * @author mwimmer
 */
public class TestPbfReader {

	private static Logger log = LoggerFactory.getLogger(TestPbfReader.class);
	
//	private String osmFileName = "C:/development/Graphserver/osm/austria-latest.osm.pbf";
//	private String osmFileName = "C:/development/Graphserver/osm/liechtenstein-latest.osm.pbf";
//	private String osmFileName = "D:/development/project_data/osm/germany-latest.osm.pbf";
	private String osmFileName = "D:/development/project_data/osm/liechtenstein-latest.osm.pbf";
	
	//private String osmFileName = "/development/project_data/graphium/testdata/osm/20160708/liechtenstein-latest.osm.pbf";
	//private String osmFileName = "/development/project_data/graphium/testdata/osm/20160708/austria-latest.osm.pbf";
//	private String osmFileName = "/development/project_data/graphium/testdata/osm/20160708/germany-latest.osm.pbf";
	
	@Test
	public void testSimpleRead() {
        log.info("Start reading PBF");

        DummySink sink = new DummySink();
        
        PbfReader reader = new PbfReader(new File(osmFileName), 3);
        reader.setSink(sink);
//        Thread pbfReaderThread = new Thread(reader, "PBF Reader");
        long startTime = System.currentTimeMillis();
//        pbfReaderThread.run();
        reader.run();
        
        log.info("PBF read in " + (System.currentTimeMillis() - startTime));
	}
	
	@Ignore
	@Test
	public void testSimpleReadFiltered() {
        log.info("Start reading PBF");

        DummySink sink = new DummySink();
        Set<String> keys = new HashSet<String>();
       // keys.add("highway");
        Map<String, Set<String>> keyValues = new HashMap<String, Set<String>>();
        Set<String> highwayValues = new HashSet<String>();
        highwayValues.add("motorway"); highwayValues.add("motorway_link"); highwayValues.add("trunk");             
        highwayValues.add("primary"); highwayValues.add("secondary"); highwayValues.add("tertiary");             
        keyValues.put("highway", highwayValues);
        
        TagFilter filterSink = new TagFilter("accept-way", keys, keyValues);
        filterSink.setSink(sink);
        PbfReader reader = new PbfReader(new File(osmFileName), 3);
        
        reader.setSink(filterSink);
        Thread pbfReaderThread = new Thread(reader, "PBF Reader");
        long startTime = System.currentTimeMillis();
        pbfReaderThread.run();
        
        log.info("PBF read in " + (System.currentTimeMillis() - startTime));
	}

	@Test
	public void testReadWithSegmentationNodeCount() {
        log.info("Start reading PBF");

        SegmentationNodesSink sink = new SegmentationNodesSink();
        
        PbfReader reader = new PbfReader(new File(osmFileName), 3);
        reader.setSink(sink);
        Thread pbfReaderThread = new Thread(reader, "PBF Reader");
        long startTime = System.currentTimeMillis();
        pbfReaderThread.run();
        
        log.info("PBF read in " + (System.currentTimeMillis() - startTime));
	}
	
	
	@Test
	public void testReadWithSegmentationNodeCountFiltered() {
        log.info("Start reading PBF");

        SegmentationNodesSink sink = new SegmentationNodesSink();
        
        Set<String> keys = new HashSet<String>();
      //  keys.add("highway");
        Map<String, Set<String>> keyValues = new HashMap<String, Set<String>>();
        Set<String> highwayValues = new HashSet<String>();
        highwayValues.add("motorway"); 
        highwayValues.add("motorway_link"); 
        highwayValues.add("trunk");       
        highwayValues.add("primary");       
        highwayValues.add("secondary");       
        highwayValues.add("tertiary");       
        highwayValues.add("living_street");       
        keyValues.put("highway", highwayValues);
        
        TagFilter filterSink = new TagFilter("accept-way", keys, keyValues);
        filterSink.setSink(sink);
      
        PbfReader reader = new PbfReader(new File(osmFileName), 3);
        
        reader.setSink(filterSink);
        Thread pbfReaderThread = new Thread(reader, "PBF Reader");
        long startTime = System.currentTimeMillis();
        pbfReaderThread.run();
        
        log.info("PBF read in " + (System.currentTimeMillis() - startTime) + "ms");
	}

}
