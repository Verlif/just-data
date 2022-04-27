package idea.verlif.justdata.sql.parser;

import idea.verlif.parser.vars.VarsHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/26 9:09
 */
public abstract class SqlPoint implements VarsHandler {

    private static final String BLANK = " ";

    private final Map<String, Object> params;

    public SqlPoint(Map<String, Object> params) {
        this.params = params;
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
}
