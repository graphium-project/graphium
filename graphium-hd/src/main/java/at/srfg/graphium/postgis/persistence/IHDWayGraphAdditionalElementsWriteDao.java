package at.srfg.graphium.postgis.persistence;

import at.srfg.graphium.core.exception.GraphStorageException;
import at.srfg.graphium.model.hd.IHDArea;
import at.srfg.graphium.model.hd.IHDInfraAndSigns;

import java.util.List;

public interface IHDWayGraphAdditionalElementsWriteDao {

    void saveHdAreas(List<IHDArea> areas, String graphName, String version) throws GraphStorageException;

    void saveHdInfrastructureAndSigns(List<IHDInfraAndSigns> infrastructureAndSigns,
                                      String graphName, String version) throws GraphStorageException;
}
