package idea.verlif.justdata.sql.parser.ext;

import idea.verlif.justdata.sql.parser.SqlPoint;

import java.util.Map;

/**
 *  WHERE方法
 *
 * @author Verlif
 */
public class WherePoint extends SqlPoint {

    public WherePoint() {
        super();
    }

    public WherePoint(String sql, Map<String, Object> params) {
        super(sql, params);
    }

    @Override
    protected String startTag() {
        return "where";
    }

    @Override
    protected String endTag() {
        return "where";
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    protected String parser(String content, Map<String, Object> params, Map<String, String> attrs) throws Exception {
        String line = content.trim();
        if (line.length() == 0) {
            return "";
        } else {
            if (line.startsWith("AND") || line.startsWith("and")) {
                line = line.substring(3);
            }
            if (line.startsWith("OR") || line.startsWith("or")) {
                line = line.substring(2);
            }
            return "WHERE " + line;
        }
    }
}
