package idea.verlif.justdata.item;

import java.util.Locale;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/14 10:28
 */
public class Item {

    private final static char DIVIDE = '/';

    /**
     * 操作项名称
     */
    private String name;

    /**
     * 操作向关联数据库
     */
    private String label;

    /**
     * 操作项API
     */
    private String api;

    /**
     * 操作项API方法
     */
    private String method;

    /**
     * 操作项SQL语句
     */
    private String sql;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        if (api.charAt(0) == DIVIDE) {
            this.api = api.substring(1);
        } else {
            this.api = api;
        }
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method.toUpperCase(Locale.ROOT);
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", dbname='" + label + '\'' +
                ", api='" + api + '\'' +
                ", method='" + method + '\'' +
                ", sql='" + sql + '\'' +
                '}';
    }
}
