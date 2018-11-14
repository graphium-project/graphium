package at.srfg.graphium.api.client.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import org.junit.Assert;
import org.junit.Rule;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;

public class AbstractGraphMetadataTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(9111);
	
	protected String graphName = "osm_at";
	
	protected void setupMetadataStub() {
		stubFor(get(urlEqualTo("/graphium-central-server/api/metadata/graphs/osm_at/versions/current")).willReturn(
				aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBodyFile("mockapi/testfiles/graph_osm_at_metadata.json")));
	}
	
	protected void assertMetadata(IWayGraphVersionMetadata metadata) {
		Assert.assertNotNull(metadata);
		Assert.assertEquals("osm_at", metadata.getGraphName());
		Assert.assertEquals(1, metadata.getId());
		Assert.assertEquals(State.ACTIVE, metadata.getState());
		Assert.assertEquals("171007", metadata.getVersion());
	}
}
