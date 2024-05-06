package at.srfg.graphium.postgis.persistence;

import at.srfg.graphium.model.hd.IHDArea;
import org.springframework.jdbc.core.RowMapper;

public interface IHDAreaRowMapper<T extends IHDArea> extends RowMapper<T>, ISegmentMapper {
}
