package at.srfg.graphium.postgis.persistence.resultsetextractors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IWaySegmentConnection;
import at.srfg.graphium.model.impl.WaySegmentConnection;

public class HDResultSetExtractorUtils {

	private static Logger log = LoggerFactory.getLogger(HDResultSetExtractorUtils.class);
	protected static final String ARRAYVALUESEP = ",";
	protected static final String TAGKVPSEP = "=>";
	
	public static IWaySegmentConnection parseSerializedCon(String serializedCon) { // (100000833,960301,101021339,"{15,4,22,2,9,19,11,13,12,1,10,3}",24)
		if (serializedCon == null) {
			return null;
		}
				
		String stripedSerializedCon = StringUtils.removeStart(serializedCon, "(");
		stripedSerializedCon = StringUtils.removeEnd(stripedSerializedCon, ")");
		
		String[] conAndTagSeperation = stripedSerializedCon.split("\"\"\"");

		String[] tokens = conAndTagSeperation[0].split(ARRAYVALUESEP);

		String accessString = StringUtils.substringBetween(conAndTagSeperation[0], "{", "}");
		accessString = accessString.replaceAll("\\{", "");
		accessString = accessString.replaceAll("\\}", "");
		
		Set<Access> accessTypesTow;
		if(!accessString.isEmpty()) {
			String[] accessTypeIdsArray = accessString.split(ARRAYVALUESEP);
		
			int[] accessTypeIds = new int[accessTypeIdsArray.length];
			int i = 0;
//			log.debug("string: {}, accessTypeIdsArray {}", serializedCon, accessTypeIdsArray);
			for (String accessTypeId : accessTypeIdsArray) {
				accessTypeIds[i++] = Integer.parseInt(accessTypeId);
			}
			accessTypesTow = Access.getAccessTypes(accessTypeIds);
		}
		else {
			accessTypesTow = new HashSet<>();
		}
		
		IWaySegmentConnection con = new WaySegmentConnection(
				Long.parseLong(tokens[0]), 
				Long.parseLong(tokens[1]),
				Long.parseLong(tokens[2]),
				accessTypesTow);
		
		// parse tags
		if(conAndTagSeperation.length > 1) {
			String tagsString = conAndTagSeperation[1].replaceAll("\"\"","");
			String[] keyValues = tagsString.split(ARRAYVALUESEP);
					
			Map<String, String> tags = new HashMap<>();
			if(keyValues.length > 0) {
				String[] keyValueTokens;
				for(String keyValue : keyValues) {
					keyValueTokens = keyValue.split(TAGKVPSEP);
					if(keyValueTokens.length == 2) {
						tags.put(keyValueTokens[0].trim(), keyValueTokens[1].trim());
					}
				}
			}
					
			if(!tags.isEmpty()) {
				con.setTags(tags);
			}
		}
		return con;
	}
	
}
