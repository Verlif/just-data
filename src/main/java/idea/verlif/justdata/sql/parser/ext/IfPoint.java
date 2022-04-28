package idea.verlif.justdata.sql.parser.ext;

import com.fasterxml.jackson.databind.JsonNode;
import idea.verlif.justdata.sql.parser.SqlPoint;
import idea.verlif.justdata.util.MessagesUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map;

/**
 * IF方法
 *
 * @author Verlif
 * @version 1.0
 * @date 2022/4/26 14:30
 */
public class IfPoint extends SqlPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(IfPoint.class);

    private static final String[] COMPARE_SYM = new String[]{"<=", ">=", "!=", "<", "=", ">"};

    public IfPoint() {}

    public IfPoint(String sql, Map<String, Object> params) {
        super(sql, params);
    }

    @Override
    protected String startTag() {
        return "if";
    }

    @Override
    protected String endTag() {
        return "fi";
    }

    @Override
    protected String parser(String content, Map<String, Object> params, Map<String, String> attrs) throws Exception {
        String testStr = attrs.get("test");
        if (testStr == null || testStr.length() == 0) {
            return content;
        } else {
            // 不支持带有()的嵌套
            String[] lines = testStr.split("or");
            for (String line : lines) {
                String[] tests = line.split("and");
                int count = 0;
                for (String test : tests) {
                    if (test(test, params)) {
                        count ++;
                    }
                }
                if (count == tests.length) {
                    return content;
                }
            }
        }
        return "";
    }

    private boolean test(String language, Map<String, Object> params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        for (int i = 0; i < COMPARE_SYM.length; i++) {
            String sym = COMPARE_SYM[i];
            int lo = language.indexOf(sym);
            if (lo > -1) {
                // 符号判定符
                int po = 1 << i;
                String[] keys = language.substring(0, lo).trim().split("\\.");
                Object value = params.get(keys[0]);
                for (int j = 1; j < keys.length; j++) {
                    if (value == null) {
                        break;
                    }
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
                }
                // 比较值的字符串
                String valStr = language.substring(lo + sym.length()).trim();
                // 这两个判断可能是数值，也可能是字符串
                if ((po & (4 | 16)) > 0) {
                    // 是否是空值判断
                    if ("null".equals(valStr) || "NULL".equals(valStr)) {
                        // !=
                        if ((po & 4) > 0) {
                            return value != null;
                        } else {
                            // =
                            return value == null;
                        }
                        // 字符串长度判定
                    } else if ("''".equals(valStr)) {
                        if (value == null) {
                            return (po & 16) > 0;
                        }
                        String valueStr = value.toString();
                        // !=
                        if ((po & 4) > 0) {
                            return valueStr.length() > 0;
                        } else {
                            // =
                            return valueStr.length() == 0;
                        }
                        // 数值判定
                    } else {
                        if (value == null) {
                            return false;
                        }
                        double val = Double.parseDouble(valStr);
                        String valueStr = value.toString();
                        // !=
                        if ((po & 4) > 0) {
                            return Double.parseDouble(valueStr) != val;
                        } else {
                            // =
                            return Double.parseDouble(valueStr) == val;
                        }
                    }
                } else {
                    if (value == null) {
                        return false;
                    }
                    double val = Double.parseDouble(valStr);
                    double keyVal = Double.parseDouble(value.toString());
                    switch (sym) {
                        case "<=":
                            return keyVal <= val;
                        case ">=":
                            return keyVal >= val;
                        case "<":
                            return keyVal < val;
                        case ">":
                            return keyVal > val;
                        default:
                    }
                }
            }
        }
        return false;
    }
}
