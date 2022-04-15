package idea.verlif.justdata.datasource;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/15 9:13
 */
public class DataSourceContextHolder {

    /**
     * 当前的数据源名称
     */
    private static final ThreadLocal<String> DBS_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前的数据源
     */
    public static void setDataSource(String dbname) {
        DBS_HOLDER.set(dbname);
    }

    /**
     * 获得当前的数据源
     */
    public static String getDataSource() {
        return DBS_HOLDER.get();
    }

    /**
     * 清空当前的数据源
     */
    public static void clearDataSource() {
        DBS_HOLDER.remove();
    }
}
