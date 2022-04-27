package idea.verlif.justdata.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import idea.verlif.justdata.sql.Sql;
import idea.verlif.justdata.sql.SqlExecutor;
import idea.verlif.justdata.util.FileUtils;
import idea.verlif.justdata.util.XMLUtils;
import idea.verlif.spring.taskservice.TaskService;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/25 11:06
 */
@Service
public class ApiLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiLogService.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private ApiLogConfig apiLogConfig;

    @Autowired
    private SqlExecutor sqlExecutor;

    private Sql sql;

    public void logApi(String label, String api, String method, Map<String, Object> map, String body) {
        long time = System.currentTimeMillis();
        taskService.execute(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Api-[/").append(label).append("/").append(api).append("] at ").append(new Date(time)).append(" : ").append("params=[");
            if (map.size() > 0) {
                StringBuilder psb = new StringBuilder();
                for (String key : map.keySet()) {
                    psb.append(key).append("=").append(map.get(key)).append("&");
                }
                psb.delete(psb.length() - 1, psb.length());
                map.put("log.params", psb.toString());
                sb.append(psb);
            } else {
                map.put("log.params", "");
            }
            map.put("log.label", label);
            map.put("log.method", method);
            map.put("log.api", api);
            map.put("log.body", body);
            sb.append("]").append(", body=[").append(body).append("]");
            if (apiLogConfig.onConsole()) {
                LOGGER.debug(sb.toString());
            }
            if (apiLogConfig.onFile()) {
                FileUtils.append(sb.append("\n").toString(), apiLogConfig.getLogFile());
            }
            if (apiLogConfig.onXml()) {
                if (sql != null) {
                    try {
                        sqlExecutor.update(sql.getLabel(), sql.getSql(), map, body);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @PostConstruct
    public void preLoadXml() {
        if (apiLogConfig.onXml()) {
            this.sql = readSqlFromXml(apiLogConfig.getXmlFile());
            if (this.sql == null) {
                LOGGER.warn("Api log xml file can not be read!");
            }
        }
    }

    private Sql readSqlFromXml(File xml) {
        if (this.sql == null) {
            Document document = XMLUtils.load(xml);
            if (document == null) {
                return null;
            }
            Element root = document.getRootElement();
            if (root == null) {
                return null;
            }
            Sql sql = new Sql();
            String labelStr = root.elementText("label");
            if (labelStr == null) {
                return null;
            }
            sql.setLabel(labelStr);
            String sqlStr = root.elementText("sql");
            if (sqlStr == null) {
                return null;
            }
            sql.setSql(sqlStr);
            this.sql = sql;
        }
        return this.sql;
    }

}
