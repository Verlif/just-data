package idea.verlif.justdata.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.justdata.base.result.ext.OkResult;
import idea.verlif.justdata.encrypt.code.Encoder;
import idea.verlif.justdata.encrypt.rsa.RsaService;
import idea.verlif.justdata.item.Item;
import idea.verlif.justdata.macro.GlobalMacroManager;
import idea.verlif.justdata.sql.exception.LackOfSqlParamException;
import idea.verlif.justdata.util.DataSourceUtils;
import idea.verlif.justdata.util.ResultSetUtils;
import idea.verlif.parser.vars.VarsContext;
import idea.verlif.parser.vars.VarsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    @Autowired
    private Encoder encoder;

    @Autowired
    private GlobalMacroManager macroManager;

    private final RsaReplaceHandler rsaReplaceHandler;
    private final MacroReplaceHandler macroReplaceHandler;
    private final EncoderReplaceHandler encoderReplaceHandler;

    private final Map<String, Connection> connectionMap;
    private final ObjectMapper objectMapper;

    public SqlExecutor() {
        this.connectionMap = new HashMap<>();
        this.objectMapper = new ObjectMapper();
        this.rsaReplaceHandler = new RsaReplaceHandler();
        this.macroReplaceHandler = new MacroReplaceHandler();
        this.encoderReplaceHandler = new EncoderReplaceHandler();
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
    public BaseResult<?> exec(Item item, Map<String, Object> map, String body) throws JsonProcessingException, SQLException {
        // sql变量替换
        String sql = parserSql(item.getSql(), map, body);
        // 切换数据源
        DataSourceUtils.switchDB(item);
        // 获取数据库连接
        Connection connection = getConnect(item);
        Statement statement = connection.createStatement();
        if (statement.execute(sql)) {
            return new OkResult<>(ResultSetUtils.toMapList(statement.getResultSet()));
        } else {
            if (statement.getUpdateCount() > 0) {
                return OkResult.empty();
            } else {
                return FailResult.empty();
            }
        }
    }

    /**
     * 执行查询操作项
     *
     * @param item 操作项
     * @param map  操作项参数
     * @param body 请求内容
     * @return 查询结果
     * @throws SQLException 执行错误
     */
    public ResultSet query(Item item, Map<String, Object> map, String body) throws SQLException, JsonProcessingException {
        // sql变量替换
        String sql = parserSql(item.getSql(), map, body);
        // 切换数据源
        DataSourceUtils.switchDB(item);
        // 获取数据库连接
        Connection connection = getConnect(item);
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    public ResultSet query(String label, String sql, Map<String, Object> map, String body) throws SQLException, JsonProcessingException {
        // sql变量替换
        sql = parserSql(sql, map, body);
        // 切换数据源
        DataSourceUtils.switchDB(label);
        // 获取数据库连接
        Connection connection = getConnect(label);
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    /**
     * 更新操作项
     *
     * @param item 操作项
     * @param map  操作项参数
     * @param body 请求内容
     * @return 更新结果
     * @throws SQLException 执行错误
     */
    public boolean update(Item item, Map<String, Object> map, String body) throws SQLException, JsonProcessingException {
        // sql变量替换
        String sql = parserSql(item.getSql(), map, body);
        // 切换数据源
        DataSourceUtils.switchDB(item);
        // 获取数据库连接
        Connection connection = getConnect(item);
        return connection.createStatement().executeUpdate(sql) > 0;
    }

    /**
     * @param sql  sql
     * @param map  sql参数
     * @param body 请求内容
     * @return 解析后的sql
     * @throws JsonProcessingException 无法解析body
     */
    private String parserSql(String sql, Map<String, Object> map, String body) throws JsonProcessingException {
        // 变量替换
        VarsContext paramContext = new VarsContext(sql);
        paramContext.setAreaTag("#{", "}");
        sql = paramContext.build(new ParamReplaceHandler(map));
        if (body != null && body.length() > 2) {
            // 将请求内容转换为json
            JsonNode node = objectMapper.readTree(body);
            VarsContext bodyContext = new VarsContext(sql);
            sql = bodyContext.build(new BodyReplaceHandler(node));
        }
        // 替换全局变量
        VarsContext macroContext = new VarsContext(sql);
        macroContext.setAreaTag("${", "}");
        sql = macroContext.build(macroReplaceHandler);
        // 解码
        VarsContext rsaContext = new VarsContext(sql);
        rsaContext.setAreaTag("@DECRYPT(", ")");
        sql = rsaContext.build(rsaReplaceHandler);
        // 重编码
        VarsContext encodeContext = new VarsContext(sql);
        encodeContext.setAreaTag("@ENCODE(", ")");
        sql = encodeContext.build(encoderReplaceHandler);
        LOGGER.debug(sql);
        return sql;
    }

    private Connection getConnect(Item item) throws SQLException {
        return getConnect(item.getLabel());
    }

    private Connection getConnect(String label) throws SQLException {
        Connection connection = connectionMap.get(label);
        if (connection == null) {
            connection = dataSource.getConnection();
            connectionMap.put(label, connection);
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
            throw new LackOfSqlParamException(s1);
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
            throw new LackOfSqlParamException(s1);
        }
    }

    private final class RsaReplaceHandler implements VarsHandler {

        @Override
        public String handle(int i, String s, String s1) {
            String de = rsaService.decryptByPrivateKey(s1);
            return (de == null || de.length() == 0) ? s1 : de;
        }
    }

    private final class MacroReplaceHandler implements VarsHandler {

        @Override
        public String handle(int i, String s, String s1) {
            String val = macroManager.get(s1);
            if (val == null) {
                throw new LackOfSqlParamException(s1);
            } else {
                return val;
            }
        }
    }

    private final class EncoderReplaceHandler implements VarsHandler {

        @Override
        public String handle(int i, String s, String s1) {
            String de = encoder.encode(s1);
            return de;
        }
    }
}
