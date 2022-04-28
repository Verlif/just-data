package idea.verlif.justdata.sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.justdata.base.result.ext.OkResult;
import idea.verlif.justdata.datasource.connection.ConnectionHolder;
import idea.verlif.justdata.datasource.connection.LockableConnection;
import idea.verlif.justdata.encrypt.code.Encoder;
import idea.verlif.justdata.encrypt.rsa.RsaService;
import idea.verlif.justdata.item.Item;
import idea.verlif.justdata.macro.MacroManager;
import idea.verlif.justdata.sql.exception.LackOfSqlParamException;
import idea.verlif.justdata.sql.parser.SqlParser;
import idea.verlif.justdata.util.DataSourceUtils;
import idea.verlif.justdata.util.ResultSetUtils;
import idea.verlif.parser.vars.VarsContext;
import idea.verlif.parser.vars.VarsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

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

    private static final String PRE_START = "<*";

    private static final String PRE_END = "*>";

    @Autowired
    private SqlConfig sqlConfig;

    @Autowired
    private SqlParser sqlParser;

    @Autowired
    private ConnectionHolder connectionHolder;

    @Autowired
    private RsaService rsaService;

    @Autowired
    private Encoder encoder;

    @Autowired
    private MacroManager macroManager;

    private final RsaReplaceHandler rsaReplaceHandler;
    private final MacroReplaceHandler macroReplaceHandler;
    private final EncoderReplaceHandler encoderReplaceHandler;
    private final NoPreHandleVarHandler noPreHandleVarHandler;

    private final Map<String, Connection> connectionMap;
    private final ObjectMapper objectMapper;

    public SqlExecutor() {
        this.connectionMap = new HashMap<>();
        this.objectMapper = new ObjectMapper();
        this.rsaReplaceHandler = new RsaReplaceHandler();
        this.macroReplaceHandler = new MacroReplaceHandler();
        this.encoderReplaceHandler = new EncoderReplaceHandler();
        this.noPreHandleVarHandler = new NoPreHandleVarHandler();
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
    public BaseResult<?> exec(Item item, Map<String, Object> map, String body) throws Exception {
        // sql变量替换
        String[] sqls = parserSql(item.getSql(), map, body).split(";");
        // 切换数据源
        DataSourceUtils.switchDB(item);
        // 获取手动事务数据库连接
        LockableConnection lc = getManualConnect(item);
        Connection connection = lc.getConnection();
        try {
            if (sqls.length == 1) {
                PreparedStatement ps = sqlToPre(sqls[0], connection);
                if (ps.execute()) {
                    return new OkResult<>(ResultSetUtils.toMapList(ps.getResultSet()));
                } else {
                    if (ps.getUpdateCount() == 0) {
                        return FailResult.empty();
                    }
                }
            } else {
                for (int i = 0; i < sqls.length; i++) {
                    String sql = sqls[i];
                    PreparedStatement ps = sqlToPre(sql, connection);
                    boolean b = ps.execute();
                    // 只对最后一个SQL进行返回值判定
                    if (i == sqls.length - 1) {
                        connection.commit();
                        if (b) {
                            return new OkResult<>(ResultSetUtils.toMapList(ps.getResultSet()));
                        } else {
                            if (ps.getUpdateCount() > 0) {
                                return OkResult.empty();
                            } else {
                                return FailResult.empty();
                            }
                        }
                    }
                }
            }
            return OkResult.empty();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            lc.release();
        }
    }

    private PreparedStatement sqlToPre(String sql, Connection connection) throws SQLException {
        // 预处理
        VarsContext context = new VarsContext(sql);
        context.setAreaTag(PRE_START, PRE_END);
        PreHandleReplaceHandler handler = new PreHandleReplaceHandler();
        sql = context.build(handler);
        // 对sql进行日志输出
        if (sqlConfig.isPrint()) {
            LOGGER.debug(sql.replace("\n", ""));
        }
        PreparedStatement ps = connection.prepareStatement(sql);
        List<String> list = handler.getObjects();
        for (int j = 0; j < list.size(); j++) {
            ps.setObject(j + 1, list.get(j));
        }
        return ps;
    }

    /**
     * 执行查询操作项
     *
     * @param label 使用的数据库label
     * @param sql   sql语句
     * @param map   sql参数
     * @param body  请求内容
     * @return 查询结果
     * @throws SQLException 执行错误
     */
    public ResultSet query(String label, String sql, Map<String, Object> map, String body) throws Exception {
        // sql变量替换
        sql = parserSql(sql, map, body);
        // 切换数据源
        DataSourceUtils.switchDB(label);
        // 预处理
        VarsContext context = new VarsContext(sql);
        context.setAreaTag(PRE_START, PRE_END);
        sql = context.build(noPreHandleVarHandler);
        // 获取数据库连接
        Connection connection = getConnect(label);
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    /**
     * 更新操作项
     *
     * @param label 使用的数据库label
     * @param sql   sql语句
     * @param map   sql参数
     * @param body  请求内容
     * @return 更新结果
     * @throws SQLException 执行错误
     */
    public boolean update(String label, String sql, Map<String, Object> map, String body) throws Exception {
        // sql变量替换
        sql = parserSql(sql, map, body);
        // 切换数据源
        DataSourceUtils.switchDB(label);
        // 预处理
        VarsContext context = new VarsContext(sql);
        context.setAreaTag(PRE_START, PRE_END);
        sql = context.build(noPreHandleVarHandler);
        // 获取数据库连接
        Connection connection = getConnect(label);
        Statement statement = connection.createStatement();
        return statement.executeUpdate(sql) > 0;
    }

    /**
     * @param sql  sql
     * @param map  sql参数
     * @param body 请求内容
     * @return 解析后的sql
     * @throws JsonProcessingException 无法解析body
     */
    private String parserSql(String sql, Map<String, Object> map, String body) throws Exception {
        // 将Body转换成Json
        JsonNode node;
        if (body != null && body.length() > 2) {
            // 将请求内容转换为json
            node = objectMapper.readTree(body);
        } else {
            node = objectMapper.nullNode();
        }
        // 判断是否需要动态SQL解析
        if (sqlParser.needParser(sql)) {
            // 组合Body与Param变量
            Map<String, Object> params = new HashMap<>(map);
            Iterator<Map.Entry<String, JsonNode>> iterable = node.fields();
            while (iterable.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterable.next();
                params.put(entry.getKey(), entry.getValue());
            }
            // SQL动态语法解析
            sql = sqlParser.parser(sql, params);
        }
        // Param变量替换
        if (sql.contains("#{")) {
            VarsContext paramContext = new VarsContext(sql);
            paramContext.setAreaTag("#{", "}");
            sql = paramContext.build(new ParamReplaceHandler(map));
        }
        // Body变量替换
        if (sql.contains("@{")) {
            VarsContext bodyContext = new VarsContext(sql);
            sql = bodyContext.build(new BodyReplaceHandler(node));
        }
        // 全局变量替换
        if (sql.contains("${")) {
            VarsContext macroContext = new VarsContext(sql);
            macroContext.setAreaTag("${", "}");
            sql = macroContext.build(macroReplaceHandler);
        }
        // 解码
        if (sql.contains("@DECRYPT(")) {
            VarsContext rsaContext = new VarsContext(sql);
            rsaContext.setAreaTag("@DECRYPT(", ")");
            sql = rsaContext.build(rsaReplaceHandler);
        }
        // 重编码
        if (sql.contains("@ENCODE(")) {
            VarsContext encodeContext = new VarsContext(sql);
            encodeContext.setAreaTag("@ENCODE(", ")");
            sql = encodeContext.build(encoderReplaceHandler);
        }
        return sql;
    }

    /**
     * 获取手动提交事务的连接
     *
     * @param item 操作项
     * @return 数据库连接
     * @throws SQLException
     */
    private LockableConnection getManualConnect(Item item) throws SQLException, InterruptedException {
        return connectionHolder.getUnlockedConnection(item.getLabel());
    }

    private Connection getConnect(Item item) throws SQLException, InterruptedException {
        return getConnect(item.getLabel());
    }

    private Connection getConnect(String label) throws SQLException, InterruptedException {
        return connectionHolder.getConnection(label);
    }

    private static String aroundVar(String var) {
        return PRE_START + var + PRE_END;
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
                return aroundVar(o.toString());
            } else if (ss.length == 2) {
                return aroundVar(ss[1]);
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
                return aroundVar(val.asText());
            } else if (ss.length == 2) {
                return aroundVar(ss[1]);
            }
            throw new LackOfSqlParamException(s1);
        }
    }

    private final class RsaReplaceHandler implements VarsHandler {

        @Override
        public String handle(int i, String s, String s1) {
            String de = rsaService.decryptByPrivateKey(s1);
            return (de == null || de.length() == 0) ? aroundVar(s1) : aroundVar(de);
        }
    }

    private final class MacroReplaceHandler implements VarsHandler {

        @Override
        public String handle(int i, String s, String s1) {
            String val = macroManager.get(s1);
            if (val == null) {
                throw new LackOfSqlParamException(s1);
            } else {
                return aroundVar(val);
            }
        }
    }

    private final class EncoderReplaceHandler implements VarsHandler {

        @Override
        public String handle(int i, String s, String s1) {
            return encoder.encode(s1);
        }
    }

    private static final class PreHandleReplaceHandler implements VarsHandler {

        private final List<String> objects;

        public PreHandleReplaceHandler() {
            objects = new ArrayList<>();
        }

        @Override
        public String handle(int i, String s, String s1) {
            objects.add(s1);
            return "?";
        }

        public List<String> getObjects() {
            return objects;
        }
    }

    private static final class NoPreHandleVarHandler implements VarsHandler {

        @Override
        public String handle(int i, String s, String s1) {
            return s1;
        }
    }
}
