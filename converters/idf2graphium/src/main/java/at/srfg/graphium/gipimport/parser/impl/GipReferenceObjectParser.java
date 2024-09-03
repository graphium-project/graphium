package at.srfg.graphium.gipimport.parser.impl;

import at.srfg.graphium.gipimport.helper.ParserHelper;
import at.srfg.graphium.gipimport.model.*;
import at.srfg.graphium.gipimport.model.impl.GipReferenceObjectImpl;
import at.srfg.graphium.gipimport.parser.IGipParser;
import at.srfg.graphium.gipimport.parser.IGipSectionParser;
import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.io.BufferedReader;
import java.io.IOException;

public class GipReferenceObjectParser extends AbstractSectionParser<TLongObjectMap<IGipReferenceObject>> {

    private final IImportConfigIdf config;
    //private final IGipSectionParser<TLongObjectMap<IGipLink>> linkParser;
    private TLongObjectMap<IGipReferenceObject> map;

    public GipReferenceObjectParser(IGipParser parserReference,
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
        return IGipSectionParser.REFERENCE_OBJECT;
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

                //atr;OBJECT_ID;SHORT_ID;EXTERNAL_ID;ORGANISATION_CODE;FEATURE_NAME;REMARK;OWNER;REFERENCE_TYPE;REFERENCE_TYPE_LONG;NETREFERENCE_TYPE;NAME_ID;NAME_TEXT;NAME_CATEGORY;NAME_CATEGORY_LONG
                if (line.startsWith("atr")) {
                    atrPos = ParserHelper.splitAtrLine(line);
                }

                if (line.startsWith("rec")) {
                    String[] values = line.split(";");

                    int referenceType = Integer.parseInt(values[atrPos.get("REFERENCE_TYPE")]);
                    //just bridges and tunnels: bridges = 5002; tunnel = 5023
                    if (referenceType == 5002 || referenceType == 5023) {
                        IGipReferenceObject object = new GipReferenceObjectImpl();

                        object.setShortId(Long.parseLong(values[atrPos.get("SHORT_ID")]));
                        object.setReferenceType(referenceType);

                        //uuid
                        object.setObjectId(values[atrPos.get("OBJECT_ID")].replaceAll("\"", ""));

                        map.put(object.getShortId(), object);
                    }
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
    public TLongObjectMap<IGipReferenceObject> getResult() {
        return this.map;
    }
}
