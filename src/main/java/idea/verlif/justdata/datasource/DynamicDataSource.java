package idea.verlif.justdata.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 动态数据库
 *
 * @author Verlif
 * @version 1.0
 * @date 2022/4/15 14:01
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    public DynamicDataSource(Map<Object, Object> map) {
        setDefaultTargetDataSource(this);
        setTargetDataSources(map);
        afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSource();
    }

}
