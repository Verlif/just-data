package idea.verlif.justdata.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import idea.verlif.justdata.datasource.driver.DriverManager;
import idea.verlif.justdata.item.ItemParser;
import idea.verlif.justdata.item.ItemParserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/15 9:13
 */
@Configuration
public class DataSourceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfig.class);

    @Autowired
    private ItemParserManager parserManager;

    @Autowired
    private DriverManager driverManager;

    private final Map<Object, Object> dsMap;

    public DataSourceConfig() {
        dsMap = new HashMap<>();
    }

    @Bean
    @Primary
    public DynamicDataSource dynamicDataSource() {
        Map<String, ItemParser> parserMap = parserManager.getParserMap();
        for (ItemParser parser : parserMap.values()) {
            List<DataSourceItem> sourceItems = parser.getDataSourceItemList();
            for (DataSourceItem sourceItem : sourceItems) {
                DruidDataSource druidDataSource = new DruidDataSource();
                druidDataSource.setUrl(sourceItem.getUrl());
                druidDataSource.setPassword(sourceItem.getPassword());
                druidDataSource.setUsername(sourceItem.getUsername());
                druidDataSource.setDriverClassName(sourceItem.getDriver());
                druidDataSource.setName(sourceItem.getLabel());
                // 设置失败重试次数
                druidDataSource.setConnectionErrorRetryAttempts(3);
                // 中止失败重试
                druidDataSource.setBreakAfterAcquireFailure(true);
                druidDataSource.setDriver(driverManager.getDriver(sourceItem.getDriver()));
                dsMap.put(sourceItem.getLabel(), druidDataSource);
            }
        }
        LOGGER.debug("Loaded datasource: " + Arrays.toString(dsMap.keySet().toArray()));
        return new DynamicDataSource(dsMap);
    }

    public DruidDataSource getDruidDataSource(String label) {
        return (DruidDataSource) dsMap.get(label);
    }
}