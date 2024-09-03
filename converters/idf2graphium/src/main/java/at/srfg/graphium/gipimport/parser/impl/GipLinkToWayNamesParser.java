package at.srfg.graphium.gipimport.parser.impl;

//import at.srfg.graphium.gipimport.model.IGipLink;
import at.srfg.graphium.gipimport.helper.ParserHelper;
import at.srfg.graphium.gipimport.model.IGipLinkToWayNames;
import at.srfg.graphium.gipimport.model.IImportConfigIdf;
import at.srfg.graphium.gipimport.model.impl.GipLinkToWayNamesImpl;
import at.srfg.graphium.gipimport.parser.IGipParser;
import at.srfg.graphium.gipimport.parser.IGipSectionParser;
//import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
//import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.THashMap;
//import gnu.trove.map.hash.TLongObjectHashMap;

import java.io.BufferedReader;
import java.io.IOException;

//public class GipLinkToWayNamesParser extends AbstractSectionParser<TLongObjectMap<IGipLinkToWayNames>> {
public class GipLinkToWayNamesParser extends AbstractSectionParser< THashMap<String, IGipLinkToWayNames>> {

    private final IImportConfigIdf config;
    //private final IGipSectionParser<TLongObjectMap<IGipLink>> linkParser;
    //private TLongObjectMap<IGipLinkToWayNames> map;
    private THashMap<String, IGipLinkToWayNames> map;

    public GipLinkToWayNamesParser(IGipParser parserReference,
                                   IImportConfigIdf config//,
                                   //IGipSectionParser<TLongObjectMap<IGipLink>> linkParser
    ) {
        super(parserReference);
        this.config = config;
        //this.linkParser = linkParser;
        //map = new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>());
        map = new THashMap<>();
    }

    @Override
    public String getPhase() {
        return IGipSectionParser.LINK_TO_WAY_NAMES;
    }

    @Override
    public String parseSectionInternally(BufferedReader file) {
        String line = null;
        TObjectIntMap<String> atrPos = null;

        try {
            line = file.readLine();
            while (line != null) {
                if (line.startsWith("tbl;")) {
                    break;
                }

                //atr;OBJECT_ID;LINK_OBJECT_ID;WAY_OBJECT_ID;LINK_SHORT_ID;WAY_SHORT_ID;WAY_PART_ID;WAY_PART_TYPE;WAY_PART_ONR;WAY_PART_ENAME;WAY_PART_COURSE;WAY_PART_FEATURE_NAME
                if (line.startsWith("atr")) {
                    atrPos = ParserHelper.splitAtrLine(line);
                }

                if (line.startsWith("rec")) {
                    String[] values = line.split(";");
                    IGipLinkToWayNames object = new GipLinkToWayNamesImpl();

                    object.setLinkShortId(Long.parseLong(values[atrPos.get("LINK_SHORT_ID")]));
                    object.setWayShortId(Long.parseLong(values[atrPos.get("WAY_SHORT_ID")]));

                    //uuids
                    object.setWayObjectId(values[atrPos.get("WAY_OBJECT_ID")].replaceAll("\"", ""));
                    object.setLinkObjectId(values[atrPos.get("LINK_OBJECT_ID")].replaceAll("\"", ""));

                    //Joining only possible via objectId due to errors
                    map.put(object.getLinkObjectId(), object);
                }

                line = file.readLine();
            }

        } catch (IOException e) {
            log.error(e.toString());
        }

        postImport();

        return line;
    }

    @Override
    public void postImport() {

    }

    @Override
    //public TLongObjectMap<IGipLinkToWayNames> getResult() {
    public  THashMap<String, IGipLinkToWayNames> getResult() {
        return this.map;
    }
}
