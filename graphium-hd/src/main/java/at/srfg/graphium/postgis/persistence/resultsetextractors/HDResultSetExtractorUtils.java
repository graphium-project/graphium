package at.srfg.graphium.postgis.persistence.resultsetextractors;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IWaySegmentConnection;
import at.srfg.graphium.model.impl.WaySegmentConnection;

public class HDResultSetExtractorUtils {

	protected static final String ARRAYVALUESEP = ",";
	protected static final String TAGKVPSEP = "=>";
	
	public static IWaySegmentConnection parseSerializedCon(String serializedCon) { // (100000833,960301,101021339,"{15,4,22,2,9,19,11,13,12,1,10,3}",24)
		if (serializedCon == null) {
			return null;
		}
				
		String stripedSerializedCon = StringUtils.removeStart(serializedCon, "(");
		stripedSerializedCon = StringUtils.removeEnd(stripedSerializedCon, ")");
		
		String[] conAndTagSeperation = stripedSerializedCon.split("\"\"\"");

//		String accessStringArray = StringUtils.substringBetween(conAndTagSeperation[0], "{", "}");
		String[] tokens = conAndTagSeperation[0].split(ARRAYVALUESEP);
//		String accessString = tokens[3];
		String accessString = StringUtils.substringBetween(conAndTagSeperation[0], "{", "}");
		accessString = accessString.replaceAll("\\{", "");
		accessString = accessString.replaceAll("\\}", "");
		
		String[] accessTypeIdsArray = accessString.split(ARRAYVALUESEP);
	
		int[] accessTypeIds = new int[accessTypeIdsArray.length];
		int i = 0;
		for (String accessTypeId : accessTypeIdsArray) {
			accessTypeIds[i++] = Integer.parseInt(accessTypeId);
		}
		Set<Access> accessTypesTow = Access.getAccessTypes(accessTypeIds);

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
	
//	public static IWaySegmentConnection parseSerializedCon(String serializedCon) { // (100000833,960301,101021339,"{15,4,22,2,9,19,11,13,12,1,10,3}",24)
//		if (serializedCon == null) {
//			return null;
//		}
//		
//		String stripedSerializedCon = StringUtils.removeStart(serializedCon, "(");
//		stripedSerializedCon = StringUtils.removeEnd(stripedSerializedCon, ")");
//		stripedSerializedCon = stripedSerializedCon.substring(0, stripedSerializedCon.indexOf("}")); // ignore everything after access types
//		stripedSerializedCon = stripedSerializedCon.replace("\"", ""); //100000833,960301,101021339,{15,4,22,2,9,19,11,13,12,1,10,3,24
//		String[] splitCons = stripedSerializedCon.split("\\{"); //[100000833,960301,101021339,, 15,4,22,2,9,19,11,13,12,1,10,3,24]
//		String[] tokens = splitCons[0].split(ARRAYVALUESEP);
//		String[] accessTypeIdsArray = new String[]{};
//		if (splitCons.length > 1) {
//			accessTypeIdsArray = splitCons[1].split(ARRAYVALUESEP); //[15, 4, 22, 2, 9, 19, 11, 13, 12, 1, 10, 3, 24]
//		}
//// TODO: add parsing of tags!
////		{"(130,142,180,{1},2,\"\"\"connectionType\"\"=>\"\"connects\"\"\")","(130,142,182,{1},2,\"\"\"direction\"\"=>\"\"reverse\"\", \"\"connectionType\"\"=>\"\"connects\"\"\")","(130,142,184,{1},2,\"\"\"connectionType\"\"=>\"\"connects\"\"\")"}
//		
////		String s1 = StringUtils.removePattern(stripedSerializedCon, "\\\"\\{[0-9,]*\\}\\\"");
//		int[] accessTypeIds = new int[accessTypeIdsArray.length];
//		int i = 0;
//		for (String accessTypeId : accessTypeIdsArray) {
//			accessTypeIds[i++] = Integer.parseInt(accessTypeId);
//		}
//		Set<Access> accessTypesTow = Access.getAccessTypes(accessTypeIds);
//
//		return new WaySegmentConnection(
//				Long.parseLong(tokens[0]), 
//				Long.parseLong(tokens[1]),
//				Long.parseLong(tokens[2]),
//				accessTypesTow);	
//	}
	
}
