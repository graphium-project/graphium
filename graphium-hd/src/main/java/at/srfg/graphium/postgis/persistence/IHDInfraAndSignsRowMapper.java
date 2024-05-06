package at.srfg.graphium.postgis.persistence;

import at.srfg.graphium.model.hd.IHDInfraAndSigns;
import org.springframework.jdbc.core.RowMapper;

public interface IHDInfraAndSignsRowMapper<T extends IHDInfraAndSigns> extends RowMapper<T>, ISegmentMapper {
}
