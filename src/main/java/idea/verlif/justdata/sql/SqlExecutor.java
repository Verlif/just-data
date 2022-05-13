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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private static final String PRE_START = "&lc&";

    private static final String PRE_END = "&rc&";

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

    private final Map<String, PreExecutingInfo> preExecutingInfoMap;
    private final ObjectMapper objectMapper;

    public SqlExecutor() {
        this.preExecutingInfoMap = new HashMap<>();
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
    public BaseResult<?> exec(Item item, Map<String, Object> map, String body) throws Exception {
        // sql变量替换
        String[] sqls = parserStr(turnToKey(item), item.getSql(), map, body).split(";");
        // 切换数据源
        DataSourceUtils.switchDB(item);
        // 获取手动事务数据库连接
        LockableConnection lc = getManualConnect(item);
        Connection connection = lc.getConnection();
        try {
            if (sqls.length == 1) {
                PreparedStatement ps = sqlToPre(sqls[0], connection);
                boolean b = ps.execute();
                connection.commit();
                if (b) {
                    return new OkResult<>(ResultSetUtils.toMapList(ps.getResultSet()));
                } else {
                    if (ps.getUpdateCount() == 0) {
                        return FailResult.empty();
                    }
                }
            } else {
                for (int i = 0; i < sqls.length; i++) {
                    String sql = sqls[i].trim();
                    if (sql.length() == 0) {
                        continue;
                    }
                    PreparedStatement ps = sqlToPre(sql, connection);
                    boolean b = ps.execute();
                    // 只对最后一个SQL进行返回值判定
                    if (i == sqls.length - 1) {
                        connection.commit();
                        if (b) {
                            return new OkResult<>(ResultSetUtils.toMapList(ps.getResultSet()));
                        } else {
                            if (ps.getUpdateCount() == 0) {
                                return FailResult.empty();
                            } else {
                                return OkResult.empty();
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
        PreparedStatement ps = connection.prepareStatement(sql);
        List<String> list = handler.getObjects();
        // 对sql进行日志输出
        if (sqlConfig.isPrint()) {
            LOGGER.debug("Sql    - " + sql.replace("\n", "").trim());
            if (list.size() > 0) {
                LOGGER.debug("Params - " + Arrays.toString(list.toArray()));
            }
        }
        for (int j = 0; j < list.size(); j++) {
            ps.setObject(j + 1, list.get(j));
        }
        return ps;
    }

    /**
     * 执行查询操作项
     *
     * @param item 操作项
     * @param map  sql参数
     * @param body 请求内容
     * @return 查询结果
     * @throws SQLException 执行错误
     */
    public ResultSet query(Item item, Map<String, Object> map, String body) throws Exception {
        // sql变量替换
        String sql = parserStr(turnToKey(item), item.getSql(), map, body);
        // 切换数据源
        DataSourceUtils.switchDB(item.getLabel());
        // 获取数据库连接
        Connection connection = getConnect(item.getLabel());
        PreparedStatement ps = sqlToPre(sql, connection);
        return ps.executeQuery();
    }

    /**
     * 更新操作项
     *
     * @param item 操作项
     * @param map  sql参数
     * @param body 请求内容
     * @return 更新结果
     * @throws SQLException 执行错误
     */
    public boolean update(Item item, Map<String, Object> map, String body) throws Exception {
        // sql变量替换
        String sql = parserStr(turnToKey(item), item.getSql(), map, body);
        // 切换数据源
        DataSourceUtils.switchDB(item.getLabel());
        // 获取数据库连接
        Connection connection = getConnect(item.getLabel());
        PreparedStatement ps = sqlToPre(sql, connection);
        return ps.executeUpdate() > 0;
    }

    /**
     * @param str  需处理的字符串
     * @param map  变量参数
     * @param body 请求内容
     * @return 解析后的sql
     * @throws JsonProcessingException 无法解析body
     */
    public String parserStr(String key, String str, Map<String, Object> map, String body) throws Exception {
        PreExecutingInfo info = preExecutingInfoMap.get(key);
        if (info == null) {
            return str;
        }
        // 将Body转换成Json
        JsonNode node;
        if (body != null && body.length() > 2) {
            // 将请求内容转换为json
            node = objectMapper.readTree(body);
        } else {
            node = objectMapper.nullNode();
        }
        // 组合Body与Param变量
        Map<String, Object> params = new HashMap<>(map);
        Iterator<Map.Entry<String, JsonNode>> iterable = node.fields();
        while (iterable.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterable.next();
            params.put(entry.getKey(), entry.getValue());
        }
        // SQL动态语法解析
        str = sqlParser.parser(str, params, info.getSqlPoints());
        // Param变量替换
        if (info.isWithParam()) {
            VarsContext paramContext = new VarsContext(str);
            paramContext.setAreaTag("#{", "}");
            str = paramContext.build(new ParamReplaceHandler(map));
        }
        // Body变量替换
        if (info.isWithBody()) {
            VarsContext bodyContext = new VarsContext(str);
            str = bodyContext.build(new BodyReplaceHandler(node));
        }
        // 全局变量替换
        if (info.isWithMacro()) {
            VarsContext macroContext = new VarsContext(str);
            macroContext.setAreaTag("${", "}");
            str = macroContext.build(macroReplaceHandler);
        }
        // 解码
        if (info.isWithEncrypt()) {
            VarsContext rsaContext = new VarsContext(str);
            rsaContext.setAreaTag("@DECRYPT(", ")");
            str = rsaContext.build(rsaReplaceHandler);
        }
        // 重编码
        if (info.isWithEncode()) {
            VarsContext encodeContext = new VarsContext(str);
            encodeContext.setAreaTag("@ENCODE(", ")");
            str = encodeContext.build(encoderReplaceHandler);
        }
        return str;
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

    /**
     * 格式化变量，只要为SQL预处理提供转换值
     *
     * @param var 变量值
     * @return 格式化后的变量
     */
    public static String aroundVar(String var) {
        return PRE_START + var + PRE_END;
    }

    /**
     * 恢复变量值
     *
     * @param var 变量值
     * @return 恢复后的变量值。当变量未被格式化时，返回原值。
     */
    public static String recoveryVar(String var) {
        if (var.startsWith(PRE_START)) {
            return var.substring(PRE_START.length(), var.length() - PRE_END.length());
        }
        return var;
    }

    public static boolean withParamReplace(String sql) {
        return sql.contains("#{");
    }

    public static boolean withBodyReplace(String sql) {
        return sql.contains("@{");
    }

    public static boolean withMacroReplace(String sql) {
        return sql.contains("${");
    }

    public static boolean withEncrypt(String sql) {
        return sql.contains("@DECRYPT(");
    }

    public static boolean withEncode(String sql) {
        return sql.contains("@ENCODE(");
    }

    private String turnToKey(Item item) {
        return item.getLabel() + "." + item.getApi() + "." + item.getMethod();
    }

    public void preExecutingItem(Item item) {
        String sql = item.getSql();
        PreExecutingInfo info = new PreExecutingInfo();
        info.setWithParam(SqlExecutor.withParamReplace(sql));
        info.setWithBody(SqlExecutor.withBodyReplace(sql));
        info.setWithMacro(SqlExecutor.withMacroReplace(sql));
        info.setWithEncrypt(SqlExecutor.withEncrypt(sql));
        info.setWithEncode(SqlExecutor.withEncode(sql));
        info.getSqlPoints().addAll(sqlParser.needParser(sql));
        preExecutingInfoMap.put(turnToKey(item), info);
    }

    private static final class ParamReplaceHandler extends SqlVarsHandler {

        private static final String SPLIT = ":";

        private final Map<String, Object> map;

        public ParamReplaceHandler(Map<String, Object> map) {
            this.map = map;
        }

        @Override
        protected String handle(String content) {
            String[] ss = content.split(SPLIT, 2);
            content = ss[0];
            if (map.containsKey(content)) {
                Object o = map.get(content);
                return o.toString();
            } else if (ss.length == 2) {
                return ss[1];
            }
            throw new LackOfSqlParamException(content);
        }
    }

    private static final class BodyReplaceHandler extends SqlVarsHandler {

        private static final String SPLIT = ":";
        private static final String LINK_SPLIT = "\\.";

        private final JsonNode node;

        public BodyReplaceHandler(JsonNode node) {
            this.node = node;
        }

        @Override
        protected String handle(String content) {
            String[] ss = content.split(SPLIT, 2);
            content = ss[0];
            String[] link = content.split(LINK_SPLIT);
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
            throw new LackOfSqlParamException(content);
        }
    }

    private final class RsaReplaceHandler extends SqlVarsHandler {

        @Override
        protected String handle(String content) {
            String de = rsaService.decryptByPrivateKey(content);
            return (de == null || de.length() == 0) ? content : de;
        }
    }

    private final class MacroReplaceHandler extends SqlVarsHandler {

        @Override
        protected String handle(String content) {
            String val = macroManager.get(content);
            if (val == null) {
                return "";
            } else {
                return val;
            }
        }
    }

    private final class EncoderReplaceHandler extends SqlVarsHandler {

        @Override
        protected String handle(String content) {
            return encoder.encode(content);
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
