package idea.verlif.justdata.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import idea.verlif.justdata.encrypt.rsa.RsaService;
import idea.verlif.justdata.item.Item;
import idea.verlif.justdata.util.DataSourceUtils;
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
import java.util.HashMap;
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

    @Autowired
    private RsaService rsaService;

    private final RsaReplaceHandler rsaReplaceHandler;

    private final Map<String, Connection> connectionMap;
    private final ObjectMapper objectMapper;

    public SqlExecutor() {
        this.connectionMap = new HashMap<>();
        this.objectMapper = new ObjectMapper();
        this.rsaReplaceHandler = new RsaReplaceHandler();
    }

    /**
     * 执行操作项
     *
     * @param item 操作项
     * @param map  操作项参数
     * @param body 请求内容
     * @return 执行结果
     * @throws SQLException 执行错误
     */
    public ResultSet exec(Item item, Map<String, Object> map, String body) throws SQLException, JsonProcessingException {
        // sql变量替换
        String sql = parserSql(item, map, body);
        // 切换数据源
        DataSourceUtils.switchDB(item);
        // 获取数据库连接
        Connection connection = getConnect(item);
        PreparedStatement statement = connection.prepareStatement(sql);
        return statement.executeQuery();
    }

    /**
     * 执行操作项
     *
     * @param item 操作项
     * @param map  操作项参数
     * @param body 请求内容
     * @return 执行结果
     * @throws SQLException 执行错误
     */
    public boolean update(Item item, Map<String, Object> map, String body) throws SQLException, JsonProcessingException {
        // sql变量替换
        String sql = parserSql(item, map, body);
        // 切换数据源
        DataSourceUtils.switchDB(item);
        // 获取数据库连接
        Connection connection = getConnect(item);
        return connection.createStatement().executeUpdate(sql) > 0;
    }

    /**
     *
     * @param item 操作项
     * @param map  操作项参数
     * @param body 请求内容
     * @return 解析后的sql
     * @throws JsonProcessingException 无法解析body
     */
    private String parserSql(Item item, Map<String, Object> map, String body) throws JsonProcessingException {
        // 变量替换
        VarsContext paramContext = new VarsContext(item.getSql());
        paramContext.setAreaTag("#{", "}");
        String sql = paramContext.build(new ParamReplaceHandler(map));
        if (body != null && body.length() > 2) {
            // 将请求内容转换为json
            JsonNode node = objectMapper.readTree(body);
            VarsContext bodyContext = new VarsContext(sql);
            sql = bodyContext.build(new BodyReplaceHandler(node));
        }
        // 解码
        VarsContext rsaContext = new VarsContext(sql);
        rsaContext.setAreaTag("@DECRYPT(", ")");
        sql = rsaContext.build(rsaReplaceHandler);
        LOGGER.debug(sql);
        return sql;
    }

    private Connection getConnect(Item item) throws SQLException {
        Connection connection = connectionMap.get(item.getLabel());
        if (connection == null) {
            connection = dataSource.getConnection();
            connectionMap.put(item.getLabel(), connection);
        }
        return connection;
    }

    private static final class ParamReplaceHandler implements VarsHandler {

        private static final String SPLIT = ":";

        private final Map<String, Object> map;

        public ParamReplaceHandler(Map<String, Object> map) {
            this.map = map;
        }

        @Override
        public String handle(int i, String s, String s1) {
            String[] ss = s1.split(SPLIT, 2);
            s1 = ss[0];
            if (map.containsKey(s1)) {
                Object o = map.get(s1);
                return o.toString();
            } else if (ss.length == 2) {
                return ss[1];
            }
            return s;
        }
    }

    private static final class BodyReplaceHandler implements VarsHandler {

        private static final String SPLIT = ":";
        private static final String LINK_SPLIT = "\\.";

        private final JsonNode node;

        public BodyReplaceHandler(JsonNode node) {
            this.node = node;
        }

        @Override
        public String handle(int i, String s, String s1) {
            String[] ss = s1.split(SPLIT, 2);
            s1 = ss[0];
            String[] link = s1.split(LINK_SPLIT);
            JsonNode val = node;
            for (String s2 : link) {
                if (val == null) {
                    break;
                }
                val = val.get(s2);
            }
            if (val != null) {
                return val.asText();
            } else if (ss.length == 2) {
                return ss[1];
            }
            return s;
        }
    }

    private final class RsaReplaceHandler implements VarsHandler {

        @Override
        public String handle(int i, String s, String s1) {
            String de = rsaService.decryptByPrivateKey(s1);
            return (de == null || de.length() == 0) ? s1 : de;
        }
    }
}
