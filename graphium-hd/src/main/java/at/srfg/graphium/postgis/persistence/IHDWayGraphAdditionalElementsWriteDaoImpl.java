package at.srfg.graphium.postgis.persistence;

import at.srfg.graphium.core.exception.GraphStorageException;
import at.srfg.graphium.model.hd.IHDArea;
import at.srfg.graphium.model.hd.IHDRoadInfrastructure;

import java.util.List;

public interface IHDWayGraphAdditionalElementsWriteDaoImpl {

    void saveHdAreas(List<IHDArea> areas, String graphName, String version) throws GraphStorageException;

    void saveHdInfrastructureAndSigns(List<IHDRoadInfrastructure> infrastructureAndSigns,
                                      String graphName, String version) throws GraphStorageException;
}
