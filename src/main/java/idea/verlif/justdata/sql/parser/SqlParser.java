package idea.verlif.justdata.sql.parser;

import idea.verlif.justdata.sql.parser.ext.IfPoint;
import idea.verlif.parser.vars.VarsContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/26 9:04
 */
@Component
public class SqlParser {

    public String parser(String sql, Map<String, Object> params) throws Exception {
        VarsContext context = new VarsContext(sql);
        context.setAreaTag("{if", "{fi}");
        sql = context.build(new IfPoint(params));

        return sql;
    }

    /**
     * 是否需要动态SQL解析
     *
     * @param sql SQL语句
     * @return 是否需要解析
     */
    public boolean needParser(String sql) {
        return sql.contains("{fi}");
    }
}
