package idea.verlif.justdata.item;

import idea.verlif.justdata.sql.SqlExecutor;
import idea.verlif.justdata.sql.parser.SqlParser;

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

    /**
     * 访问权限
     */
    private String permission;

    /**
     * 预处理信息
     */
    private final PreHandleInfo preHandleInfo;

    public Item() {
        preHandleInfo = new PreHandleInfo();
    }

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
        this.preHandleInfo.setWithParam(SqlExecutor.withParamReplace(sql));
        this.preHandleInfo.setWithBody(SqlExecutor.withBodyReplace(sql));
        this.preHandleInfo.setWithMacro(SqlExecutor.withMacroReplace(sql));
        this.preHandleInfo.setWithEncrypt(SqlExecutor.withEncrypt(sql));
        this.preHandleInfo.setWithEncode(SqlExecutor.withEncode(sql));
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public PreHandleInfo getPreHandleInfo() {
        return preHandleInfo;
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

    public static final class PreHandleInfo {

        private boolean withParam;

        private boolean withBody;

        private boolean withMacro;

        private boolean withEncrypt;

        private boolean withEncode;

        public boolean isWithParam() {
            return withParam;
        }

        public void setWithParam(boolean withParam) {
            this.withParam = withParam;
        }

        public boolean isWithBody() {
            return withBody;
        }

        public void setWithBody(boolean withBody) {
            this.withBody = withBody;
        }

        public boolean isWithMacro() {
            return withMacro;
        }

        public void setWithMacro(boolean withMacro) {
            this.withMacro = withMacro;
        }

        public boolean isWithEncrypt() {
            return withEncrypt;
        }

        public void setWithEncrypt(boolean withEncrypt) {
            this.withEncrypt = withEncrypt;
        }

        public boolean isWithEncode() {
            return withEncode;
        }

        public void setWithEncode(boolean withEncode) {
            this.withEncode = withEncode;
        }

    }
}
