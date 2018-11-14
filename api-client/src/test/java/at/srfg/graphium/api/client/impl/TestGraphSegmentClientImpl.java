package at.srfg.graphium.api.client.impl;

import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class TestGraphSegmentClientImpl {

	private static Logger log = LoggerFactory.getLogger(TestGraphSegmentClientImpl.class);

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(9111);

	@Test
	public void getTestSegments() throws IOException {
		log.info("test /graphium-tutorial-central-server/api/segments/graphs/osm_at/versions/171007");
		stubFor(get(urlEqualTo("/graphium-tutorial-central-server/api/segments/graphs/osm_at/versions/171007")).willReturn(
				aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBodyFile("mockapi/testfiles/graph_osm_at_5_segments.json")));
		URL url = new URL("http://localhost:9111/graphium-tutorial-central-server/api/segments/graphs/osm_at/versions/171007");
		// open the url stream, wrap it an a few "readers"
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

		// write the output to stdout
		String line;
		while ((line = reader.readLine()) != null) {
			log.info(line);
		}

		// close our reader
		reader.close();

	}
}
