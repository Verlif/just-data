package idea.verlif.justdata.sql.parser;

import idea.verlif.justdata.sql.parser.ext.ForeachPoint;
import idea.verlif.justdata.sql.parser.ext.IfPoint;
import idea.verlif.justdata.sql.parser.ext.TrimPoint;
import idea.verlif.justdata.sql.parser.ext.WherePoint;
import idea.verlif.parser.vars.VarsContext;
import idea.verlif.parser.vars.VarsHandler;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/26 9:04
 */
@Component
public class SqlParser {

    private static final PointSort POINT_SORT = new PointSort();

    private final Map<String, SqlPoint> pointMap;
    private final List<String> tagSet;

    public SqlParser() {
        pointMap = new HashMap<>();
        tagSet = new ArrayList<>();

        addSqlPoint(new IfPoint());
        addSqlPoint(new ForeachPoint());
        addSqlPoint(new TrimPoint());
        addSqlPoint(new WherePoint());
    }

    public String parser(String sql, Map<String, Object> params, ArrayList<SqlPoint> sqlPoints) throws Exception {
        if (sqlPoints == null) {
            sqlPoints = needParser(sql);
        }
        for (SqlPoint point : sqlPoints) {
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
    public ArrayList<SqlPoint> needParser(String sql) {
        VarsContext context = new SqlPointVarsContext(sql);
        ParserParamHandler handler = new ParserParamHandler();
        context.build(handler);
        ArrayList<SqlPoint> points = new ArrayList<>(handler.needParser);
        points.sort(POINT_SORT);
        return points;
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

    private static final class PointSort implements Comparator<SqlPoint> {

        @Override
        public int compare(SqlPoint o1, SqlPoint o2) {
            return o1.order() - o2.order();
        }
    }
}
