package idea.verlif.justdata.sql;

import idea.verlif.justdata.item.Item;
import idea.verlif.justdata.route.RouteManager;
import idea.verlif.parser.vars.VarsContext;
import idea.verlif.parser.vars.VarsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Sql执行器
 *
 * @author Verlif
 * @version 1.0
 * @date 2022/4/14 17:08
 */
@Component
public class SqlExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlExecutor.class);

    @Autowired
    private DataSource dataSource;

    /**
     * 执行操作项
     *
     * @param item 操作项
     * @param map  操作项参数
     * @return 执行结果
     * @throws SQLException 执行错误
     */
    public ResultSet exec(Item item, Map<String, Object> map) throws SQLException {
        VarsContext context = new VarsContext(item.getSql());
        context.setAreaTag("#{", "}");
        String sql = context.build(new VarsReplaceHandler(map));
        LOGGER.debug("sql-" + item.getApi() + "-" + sql);
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        return statement.executeQuery();
    }

    private static final class VarsReplaceHandler implements VarsHandler {

        private final Map<String, Object> map;

        public VarsReplaceHandler(Map<String, Object> map) {
            this.map = map;
        }

        @Override
        public String handle(int i, String s, String s1) {
            if (map.containsKey(s1)) {
                Object o = map.get(s1);
                if (o instanceof Integer || o instanceof Long || o instanceof Double) {
                    return o.toString();
                } else {
                    return "'" + o.toString() + "'";
                }
            }
            return s;
        }
    }
}
