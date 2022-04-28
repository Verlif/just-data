package idea.verlif.justdata.sql.parser;

import idea.verlif.parser.vars.VarsContext;
import idea.verlif.parser.vars.VarsHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/26 9:09
 */
public abstract class SqlPoint extends VarsContext implements VarsHandler {

    private static final String BLANK = " ";

    private final Map<String, Object> params;

    public SqlPoint() {
        super("");
        this.params = new HashMap<>();
    }

    public SqlPoint(String sql, Map<String, Object> params) {
        super(sql);
        this.params = params;

        setAreaTag("{" + startTag(), getEndTag());
    }

    /**
     * 方法左标识名称
     * @return 方法的左标识名，例如 {code if}
     */
    protected abstract String startTag();

    /**
     * 方法右标识名称
     * @return 方法的右标识名，例如 {code if}
     */
    protected abstract String endTag();

    public String getEndTag() {
        return "{" + endTag() + "}";
    }

    public SqlPoint newInstance(String sql, Map<String, Object> params) throws Exception {
        return this.getClass().getConstructor(String.class, Map.class).newInstance(sql, params);
    }

    /**
     * 解析SQL的XML格式数据
     *
     * @param content SQL内容
     * @param params  SQL参数
     * @param attrs   方法属性
     * @return 解析后的XML格式数据
     * @throws Exception 解析时可能出现的异常
     */
    protected abstract String parser(String content, Map<String, Object> params, Map<String, String> attrs) throws Exception;

    public String build() {
        return build(this);
    }

    @Override
    public String handle(int i, String s, String s1) {
        int lo = s1.indexOf('}');
        try {
            return parser(s1.substring(lo + 1), params, getAttrMap(s1.substring(0, lo).trim()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    private Map<String, String> getAttrMap(String attrStr) {
        Map<String, String> attrMap = new HashMap<>();
        if (attrStr.length() == 0) {
            return attrMap;
        }
        // 以空格为分隔符
        String[] ss = attrStr.split(BLANK);
        String lastKsy = null;
        for (String s : ss) {
            String pro = s.trim();
            if (pro.length() > 0) {
                String[] prop = s.split(":");
                int length = prop.length == 2 ? prop[1].length() : prop[0].length();
                if (prop.length == 2) {
                    lastKsy = prop[0];
                    attrMap.put(lastKsy, prop[1].substring(1, length));
                } else if (lastKsy != null && length > 0) {
                    if (prop[0].charAt(length - 1) == '\"') {
                        attrMap.put(lastKsy, attrMap.get(lastKsy) + BLANK + prop[0].substring(0, length - 1));
                    } else {
                        attrMap.put(lastKsy, attrMap.get(lastKsy) + BLANK + prop[0]);
                    }
                }
            }
        }
        for (String key : attrMap.keySet()) {
            String value = attrMap.get(key);
            if (value.charAt(value.length() - 1) == '\"') {
                attrMap.put(key, value.substring(0, value.length() - 1));
            }
        }
        return attrMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SqlPoint point = (SqlPoint) o;
        return Objects.equals(params, point.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(params);
    }
}
