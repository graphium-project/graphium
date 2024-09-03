package at.srfg.graphium.gipimport.parser.impl;

import at.srfg.graphium.gipimport.helper.ParserHelper;
//import at.srfg.graphium.gipimport.model.IGipLink;
import at.srfg.graphium.gipimport.model.IGipLinkToReferenceObject;
import at.srfg.graphium.gipimport.model.IImportConfigIdf;
import at.srfg.graphium.gipimport.model.impl.GipLinkToReferenceObjectImpl;
import at.srfg.graphium.gipimport.parser.IGipParser;
import at.srfg.graphium.gipimport.parser.IGipSectionParser;
import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.io.BufferedReader;
import java.io.IOException;

public class GipLinkToReferenceObjectParser extends AbstractSectionParser<TLongObjectMap<IGipLinkToReferenceObject>> {

    private final IImportConfigIdf config;
    //private final IGipSectionParser<TLongObjectMap<IGipLink>> linkParser;
    private TLongObjectMap<IGipLinkToReferenceObject> map;


    public GipLinkToReferenceObjectParser(IGipParser parserReference,
                                IImportConfigIdf config//,
                                //IGipSectionParser<TLongObjectMap<IGipLink>> linkParser
    ) {
        super(parserReference);
        this.config = config;
        //this.linkParser = linkParser;
        map = new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>());
    }

    @Override
    public String getPhase() {
        return IGipSectionParser.LINK_TO_REFERENCE_OBJECT;
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

                //atr;OBJECT_ID;SORTFIELD;REFERENCE_OBJECT_ID;REFERENCE_OBJECT_SHORT_ID;LINK_ID;LINK_SHORT_ID;LINK_PERCENTAGE_FROM;LINK_PERCENTAGE_TO;LINK_PERCENTAGE_AT;EDGE_ID;EDGE_SEQUENCE_NUMBER;EDGE_DIRECTION
                if (line.startsWith("atr")) {
                    atrPos = ParserHelper.splitAtrLine(line);
                }

                if (line.startsWith("rec")) {
                    String[] values = line.split(";");
                    IGipLinkToReferenceObject object = new GipLinkToReferenceObjectImpl();

                    object.setReferenceObjectShortId(Long.parseLong(values[atrPos.get("REFERENCE_OBJECT_SHORT_ID")]));
                    object.setLinkShortId(Long.parseLong(values[atrPos.get("LINK_SHORT_ID")]));

                    //uuid
                    object.setReferenceObjectId(values[atrPos.get("REFERENCE_OBJECT_ID")].replaceAll("\"", ""));
                    object.setLinkId(values[atrPos.get("LINK_ID")].replaceAll("\"", ""));

                    map.put(object.getLinkShortId(), object);
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
    public TLongObjectMap<IGipLinkToReferenceObject> getResult() {
        return this.map;
    }
}
