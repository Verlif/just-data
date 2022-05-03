package idea.verlif.justdata.sql.parser.ext;

import com.fasterxml.jackson.databind.JsonNode;
import idea.verlif.justdata.sql.parser.SqlPoint;
import idea.verlif.parser.vars.VarsContext;
import idea.verlif.parser.vars.VarsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    private static final String TAG = "&ATG&";

    private final List<Map<String, String>> attrList;
    private final ElseIfVarsHandler varsHandler;

    public IfPoint() {
        attrList = new ArrayList<>();
        varsHandler = new ElseIfVarsHandler();
    }

    public IfPoint(String sql, Map<String, Object> params) {
        super(sql, params);
        attrList = new ArrayList<>();
        varsHandler = new ElseIfVarsHandler();
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
        attrList.add(attrs);
        // 对if判定中的elseif判定进行顺序解析
        VarsContext context = new VarsContext(content);
        context.setAreaTag("{elseif", "}");
        String after = context.build(varsHandler);
        // 顺序判定
        String[] ss = after.split(TAG);
        for (int i = 0; i < attrList.size(); i++) {
            // 获取判定条件
            Map<String, String> maps = attrList.get(i);
            String testStr = maps.get("test");
            // 当判定条件为空时判定为true
            if (testStr == null || testStr.length() == 0 || testLine(testStr, params)) {
                return ss[i];
            }
        }
        return "";
    }

    private boolean testLine(String line, Map<String, Object> params) throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        // 不支持带有()的嵌套
        String[] lines = line.split("OR");
        for (String s : lines) {
            String[] tests = s.split("AND");
            int count = 0;
            for (String test : tests) {
                if (test(test, params)) {
                    count++;
                }
            }
            if (count == tests.length) {
                return true;
            }
        }
        return false;
    }

    private boolean test(String language, Map<String, Object> params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        for (int i = 0; i < COMPARE_SYM.length; i++) {
            String sym = COMPARE_SYM[i];
            int lo = language.indexOf(sym);
            if (lo > -1) {
                // 符号判定符
                int po = 1 << i;
                Object left = parserObj(language.substring(0, lo).trim(), params);
                // 比较值的字符串
                Object right = parserObj(language.substring(lo + sym.length()).trim(), params);
                // 是否是空值判断
                if (left == null || right == null) {
                    if ((po & 4) > 0) {
                        // !=
                        return left != right;
                    } else if ((po & 16) > 0) {
                        // =
                        return left == right;
                    } else {
                        // 其他的符号都无法进行空置判断
                        return false;
                    }
                }
                // 这两个判断可能是数值，也可能是字符串
                if ((po & (4 | 16)) > 0) {
                    // !=
                    if ((po & 4) > 0) {
                        return !left.equals(right);
                    } else {
                        // =
                        return left.equals(right);
                    }
                } else if (left instanceof String || right instanceof String) {
                    return false;
                } else {
                    double val = (double) left;
                    double keyVal = (double) right;
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

    private Object parserObj(String desc, Map<String, Object> objMap) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
        return value == null || value instanceof String ? value : Double.valueOf(value.toString());
    }

    private final class ElseIfVarsHandler implements VarsHandler {

        @Override
        public String handle(int i, String s, String s1) {
            Map<String, String> map = SqlPoint.getAttrMap(s1.trim());
            attrList.add(map);
            return TAG;
        }
    }
}
