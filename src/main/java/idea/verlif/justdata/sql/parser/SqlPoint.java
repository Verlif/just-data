package idea.verlif.justdata.sql.parser;

import com.fasterxml.jackson.databind.JsonNode;
import idea.verlif.parser.vars.VarsContext;
import idea.verlif.parser.vars.VarsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/26 9:09
 */
public abstract class SqlPoint extends VarsContext implements VarsHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlPoint.class);

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
     *
     * @return 方法的左标识名，例如 {code if}
     */
    protected abstract String startTag();

    /**
     * 方法右标识名称
     *
     * @return 方法的右标识名，例如 {code if}
     */
    protected abstract String endTag();

    /**
     * 排序权重。权重越大，判定越靠后。
     *
     * @return 排序权重
     */
    public abstract int order();

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

    /**
     * 从动态语法的参数字符串中提取参数
     *
     * @param attrStr 参数字符串
     * @return 参数map
     */
    public static Map<String, String> getAttrMap(String attrStr) {
        Map<String, String> attrMap = new HashMap<>();
        char[] chars = attrStr.toCharArray();
        boolean isKey = true, in = false;
        String key = null;
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (isKey) {
                if (c == '=') {
                    isKey = false;
                    key = sb.toString();
                    sb.setLength(0);
                } else if (c != ' ') {
                    sb.append(c);
                }
            } else {
                if (in) {
                    if (c == '\"') {
                        in = false;
                        attrMap.put(key, sb.toString());
                        sb.setLength(0);
                        isKey = true;
                    } else {
                        sb.append(c);
                    }
                } else {
                    if (c == '\"') {
                        in = true;
                    }
                }
            }
        }
        return attrMap;
    }

    protected Object parserObj(String desc, Map<String, Object> objMap) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 判定是否是数字
        try {
            return Double.parseDouble(desc);
        } catch (NumberFormatException ignored) {
        }
        String[] keys = desc.split("\\.");
        Object value = objMap.get(keys[0]);
        // 判定是否是设定字符串
        if (value == null) {
            return null;
        }
        for (int j = 1; j < keys.length; j++) {
            String key = keys[j];
            Class<?> cl = value.getClass();
            // 是否是方法
            if (key.endsWith("()")) {
                try {
                    Method method = cl.getDeclaredMethod(key.substring(0, key.length() - 2));
                    value = method.invoke(value);
                    break;
                } catch (NoSuchMethodException e) {
                    LOGGER.error("Can not parse text about " + key);
                    throw e;
                } catch (InvocationTargetException | IllegalAccessException e) {
                    LOGGER.error(key + " is ran with error");
                    throw e;
                }
            } else {
                if (value instanceof JsonNode) {
                    value = ((JsonNode) value).get(key);
                }
            }
            if (value == null) {
                break;
            }
        }
        if (value instanceof Integer) {
            return Integer.valueOf(value.toString());
        } else if (value instanceof Double) {
            return Double.valueOf(value.toString());
        } else {
            return value;
        }
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
