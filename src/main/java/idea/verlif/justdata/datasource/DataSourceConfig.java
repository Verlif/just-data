package idea.verlif.justdata.datasource;

import com.alibaba.druid.pool.DruidDataSource;
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

    @Bean
    @Primary
    public DynamicDataSource dynamicDataSource() {
        Map<String, ItemParser> parserMap = parserManager.getParserMap();
        Map<Object, Object> dsMap = new HashMap<>(parserMap.size());
        for (ItemParser parser : parserMap.values()) {
            List<DataSourceItem> sourceItems = parser.getDataSourceItemList();
            for (DataSourceItem sourceItem : sourceItems) {
                DruidDataSource druidDataSource = new DruidDataSource();
                druidDataSource.setUrl(sourceItem.getUrl());
                druidDataSource.setPassword(sourceItem.getPassword());
                druidDataSource.setUsername(sourceItem.getUsername());
                druidDataSource.setDriverClassName(sourceItem.getDriver());
                druidDataSource.setName(sourceItem.getLabel());
                dsMap.put(sourceItem.getLabel(), druidDataSource);
            }
        }
        LOGGER.debug("Loaded datasource: " + Arrays.toString(dsMap.keySet().toArray()));
        return new DynamicDataSource(dsMap);
    }

}