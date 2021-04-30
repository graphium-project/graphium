package at.srfg.graphium.routing.api.controller.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.srfg.graphium.routing.api.dto.impl.RestrictedSegmentDTOImpl;
import at.srfg.graphium.routing.api.dto.impl.RestrictedSegmentsContainerDTOImpl;

/**
 * @author mwimmer
 *
 */
public class TestRestrictionsDTOs2Json {

	@Test
	public void testJsonMarshalling() {
		List<RestrictedSegmentDTOImpl> segments = new ArrayList<RestrictedSegmentDTOImpl>();
		RestrictedSegmentDTOImpl segment = new RestrictedSegmentDTOImpl(901417346, false, null, null);
		segments.add(segment);
		RestrictedSegmentsContainerDTOImpl containerDTO = new RestrictedSegmentsContainerDTOImpl(segments);
		
		ObjectMapper om = new ObjectMapper();
		try {
			System.out.println(om.writeValueAsString(containerDTO));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
