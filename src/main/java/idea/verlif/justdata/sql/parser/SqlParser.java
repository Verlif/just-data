package idea.verlif.justdata.sql.parser;

import idea.verlif.justdata.sql.parser.ext.IfPoint;
import idea.verlif.parser.vars.VarsContext;
import idea.verlif.parser.vars.VarsHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/26 9:04
 */
@Component
public class SqlParser {

    private final Map<String, SqlPoint> pointMap;
    private final Set<String> tagSet;

    public SqlParser() {
        pointMap = new HashMap<>();
        tagSet = new HashSet<>();

        addSqlPoint(new IfPoint());
    }

    public String parser(String sql, Map<String, Object> params) throws Exception {
        Set<SqlPoint> pointSet = needParser(sql);
        for (SqlPoint point : pointSet) {
            sql = point.newInstance(sql, params).build();
        }
        return sql;
    }

    private void addSqlPoint(SqlPoint sqlPoint) {
        tagSet.add(sqlPoint.endTag());
        pointMap.put(sqlPoint.endTag(), sqlPoint);
    }

    /**
     * 是否需要动态SQL解析
     *
     * @param sql SQL语句
     * @return 是否需要解析
     */
    public Set<SqlPoint> needParser(String sql) {
        VarsContext context = new VarsContext(sql);
        context.setAreaTag("{", "}");
        ParserParamHandler handler = new ParserParamHandler();
        context.build(handler);
        return handler.needParser;
    }

    private final class ParserParamHandler implements VarsHandler {

        private final Set<SqlPoint> needParser;

        public ParserParamHandler() {
            needParser = new HashSet<>();
        }

        @Override
        public String handle(int i, String s, String s1) {
            if (tagSet.contains(s1)) {
                needParser.add(pointMap.get(s1));
            }
            return s;
        }

        public Set<SqlPoint> isNeedParser() {
            return needParser;
        }
    }
}
