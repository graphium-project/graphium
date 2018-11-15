package at.srfg.graphium.api.client.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import at.srfg.graphium.api.client.exception.GraphNotFoundException;
import at.srfg.graphium.api.client.exception.GraphiumServerAccessException;
import at.srfg.graphium.io.adapter.impl.AbstractSegmentDTOAdapter;
import at.srfg.graphium.io.adapter.impl.DefaultConnectionXInfoAdapter;
import at.srfg.graphium.io.adapter.impl.DefaultSegmentXInfoAdapter;
import at.srfg.graphium.io.adapter.impl.WaySegment2SegmentDTOAdapter;
import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistry;
import at.srfg.graphium.io.adapter.registry.impl.GenericXInfoAdapterRegistry;
import at.srfg.graphium.io.adapter.registry.impl.SegmentAdapterRegistryImpl;
import at.srfg.graphium.io.adapter.registry.impl.SegmentXInfoAdapterRegistry;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.dto.IWaySegmentDTO;
import at.srfg.graphium.io.inputformat.ISegmentInputFormat;
import at.srfg.graphium.io.inputformat.impl.jackson.GenericQueuingJacksonSegmentInputFormat;
import at.srfg.graphium.model.IWaySegment;

public class TestGraphSegmentClientImpl {

	private static Logger log = LoggerFactory.getLogger(TestGraphSegmentClientImpl.class);

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(9111);

	@Test
	public void getTestSegments() throws IOException {
		log.info("test /graphium-tutorial-central-server/api/segments/graphs/osm_at/versions/171007");
		stubFor(get(urlPathMatching("/graphium-tutorial-central-server/api/segments/graphs/osm_at/versions/171007")).
				withQueryParam("ids",matching("3988097"))
				.willReturn(
				aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBodyFile("mockapi/testfiles/graph_osm_at_segment_3988097.json")));
		
		stubFor(get(urlPathMatching("/graphium-tutorial-central-server/api/segments/graphs/osm_at/versions/171007")).
				withQueryParam("ids",matching("275732,2870458"))
				.willReturn(
				aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBodyFile("mockapi/testfiles/graph_osm_at_segments_275732_2870458.json")));
			
		GenericGraphSegmentClientImpl<IWaySegment> client = new GenericGraphSegmentClientImpl<IWaySegment>("http://localhost:9111/graphium-tutorial-central-server/api/");
		client.setup();
		try {
			// one segment
			List<IWaySegment> dtosSingle = client.getSegments("osm_at", "171007", Collections.singleton(3988097l));
			Assert.assertNotNull("dto list should not be null!", dtosSingle);
			Assert.assertEquals(1, dtosSingle.size());
			Assert.assertEquals(3988097l, dtosSingle.get(0).getId());
			Assert.assertEquals("A23 - Suedosttangente", dtosSingle.get(0).getName());
			
			// multiple segments
			Set<Long> segmentIds = new HashSet<>();
			segmentIds.add(275732l); segmentIds.add(2870458l);
			List<IWaySegment> dtosMulti = client.getSegments("osm_at", "171007", segmentIds);
			Assert.assertNotNull("dto list should not be null!", dtosMulti);
			Assert.assertEquals(2, dtosMulti.size());
			Assert.assertEquals(275732l, dtosMulti.get(0).getId());
			Assert.assertEquals(2870458l, dtosMulti.get(1).getId());
			Assert.assertEquals("A1 - Westautobahn", dtosMulti.get(0).getName());
			Assert.assertEquals("A1 - Westautobahn", dtosMulti.get(1).getName());
			
		} catch (GraphNotFoundException e) {
			log.error("graph not found", e);
		} catch (GraphiumServerAccessException e) {
			log.error("error accessing graphium moc", e);
		}
	}
}
