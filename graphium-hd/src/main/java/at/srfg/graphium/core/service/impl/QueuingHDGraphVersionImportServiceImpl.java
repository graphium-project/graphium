package at.srfg.graphium.core.service.impl;

import at.srfg.graphium.core.exception.GraphStorageException;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.hd.IHDArea;
import at.srfg.graphium.model.hd.IHDInfraAndSigns;
import at.srfg.graphium.model.hd.IHDWaySegment;
import at.srfg.graphium.postgis.persistence.IHDWayGraphAdditionalElementsWriteDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class QueuingHDGraphVersionImportServiceImpl<T extends IHDWaySegment>
        extends QueuingGraphVersionImportServiceImpl<T> {

    private static Logger log = LoggerFactory.getLogger(QueuingHDGraphVersionImportServiceImpl.class);

    IHDWayGraphAdditionalElementsWriteDao additionalElementsWriteDao;

    @Override
    protected void processAdditionalElements(List<IBaseSegment> additionalElementsToSave,
                                             String graphName, String version) throws GraphStorageException {
        // sort all areas and infrastructur/sign elements and store them
        List<IHDArea> areas = new ArrayList<>();
        List<IHDInfraAndSigns> infrastructures = new ArrayList<>();
        for (IBaseSegment element : additionalElementsToSave) {
            if (element instanceof IHDArea) {
                areas.add((IHDArea) element);
            } else if (element instanceof IHDInfraAndSigns) {
                infrastructures.add((IHDInfraAndSigns) element);
            } else {
                log.warn("unrecognized additional element type: " + element.getClass().getName());
            }
        }
        if (!areas.isEmpty()) {
            log.info("storing {} areas", areas.size());
            additionalElementsWriteDao.saveHdAreas(areas, graphName, version);
        }
        if (!infrastructures.isEmpty()) {
            log.info("storing {} infrastructure", infrastructures.size());
            additionalElementsWriteDao.saveHdInfrastructureAndSigns(infrastructures, graphName, version);
        }
    }

    public IHDWayGraphAdditionalElementsWriteDao getAdditionalElementsWriteDao() {
        return additionalElementsWriteDao;
    }

    public void setAdditionalElementsWriteDao(IHDWayGraphAdditionalElementsWriteDao additionalElementsWriteDao) {
        this.additionalElementsWriteDao = additionalElementsWriteDao;
    }
}
