package idea.verlif.justdata.util;

import idea.verlif.justdata.datasource.DataSourceContextHolder;
import idea.verlif.justdata.item.Item;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/15 9:33
 */
public class DataSourceUtils {

    /**
     * 切换数据源
     *
     * @param item 操作项
     */
    public static void switchDB(Item item) {
        DataSourceContextHolder.setDataSource(item.getLabel());
    }
}
