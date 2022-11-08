package at.srfg.graphium.postgis.presistence.resultsetextractor;

import org.junit.Assert;
import org.junit.Test;

import at.srfg.graphium.model.IWaySegmentConnection;
import at.srfg.graphium.postgis.persistence.resultsetextractors.HDResultSetExtractorUtils;

public class TestConnectionTagsExtraction {

	@Test
	public void testHdConnectionParsingWithTags() {
		String serializedConnection = "(130,142,182,{1},2,\"\"\"direction\"\"=>\"\"reverse\"\", \"\"connectionType\"\"=>\"\"connects\"\"\")";
		IWaySegmentConnection con = HDResultSetExtractorUtils.parseSerializedCon(serializedConnection);
		
		Assert.assertNotNull(con.getTags());
		Assert.assertEquals(130, con.getNodeId());
		Assert.assertEquals(142, con.getFromSegmentId());
		Assert.assertEquals(182, con.getToSegmentId());
		Assert.assertEquals(2, con.getTags().size());	
		Assert.assertEquals("reverse", con.getTags().get("direction"));
		Assert.assertEquals("connects", con.getTags().get("connectionType"));
	}
	
	@Test
	public void testHdConnectionParsingWithMultipleAccessTypesTags() {
		String serializedConnection = "(-1,2,1,\"{12,11,4,15,10,3,23,2}\",3,\"\"\"parallel\"\"=>\"\"right\"\", \"\"connectionType\"\"=>\"\"connects\"\"\")";
		IWaySegmentConnection con = HDResultSetExtractorUtils.parseSerializedCon(serializedConnection);
		
		Assert.assertNotNull(con.getTags());
		Assert.assertEquals(-1, con.getNodeId());
		Assert.assertEquals(2, con.getFromSegmentId());
		Assert.assertEquals(1, con.getToSegmentId());
		Assert.assertEquals(2, con.getTags().size());	
		Assert.assertEquals("right", con.getTags().get("parallel"));
		Assert.assertEquals("connects", con.getTags().get("connectionType"));		
	}
}
