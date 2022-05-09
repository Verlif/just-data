package idea.verlif.justdata.sql.parser.ext;

import idea.verlif.justdata.sql.parser.SqlPoint;

import java.util.Map;

/**
 *  WHERE方法
 *
 * @author Verlif
 */
public class TrimPoint extends SqlPoint {

    public TrimPoint() {
        super();
    }

    public TrimPoint(String sql, Map<String, Object> params) {
        super(sql, params);
    }

    @Override
    protected String startTag() {
        return "trim";
    }

    @Override
    protected String endTag() {
        return "trim";
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    protected String parser(String content, Map<String, Object> params, Map<String, String> attrs) throws Exception {
        String prefix = attrs.get("prefix");
        String line = content.trim();
        if (line.length() == 0) {
            return "";
        } else {
            // 前缀
            String prefixOverrides = attrs.get("prefixOverrides");
            if (prefixOverrides != null) {
                String[] pigs = prefixOverrides.split("\\|");
                for (String ig : pigs) {
                    if (line.startsWith(ig)) {
                        line = line.substring(ig.length());
                    }
                }
            }
            // 后缀
            String suffixOverrides = attrs.get("suffixOverrides");
            if (suffixOverrides != null) {
                String[] sigs = suffixOverrides.split("\\|");
                for (String sig : sigs) {
                    if (line.startsWith(sig)) {
                        line = line.substring(sig.length());
                    }
                }
            }
            return prefix + " " + line;
        }
    }
}
