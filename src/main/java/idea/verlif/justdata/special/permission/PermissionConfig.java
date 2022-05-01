package idea.verlif.justdata.special.permission;

import idea.verlif.justdata.item.Item;
import idea.verlif.justdata.sql.SqlExecutor;
import idea.verlif.justdata.util.XMLUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/19 10:31
 */
@Configuration
@ConfigurationProperties(prefix = "just-data.permission")
public class PermissionConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionConfig.class);

    /**
     * xml配置路径
     */
    private String file;

    /**
     * 是否开启登录
     */
    private boolean enabled;

    /**
     * 获取用户密钥
     */
    private Item queryPermission;

    /**
     * 内置接口需要的权限
     */
    private String innerPermission;

    @Autowired
    private SqlExecutor sqlExecutor;

    public PermissionConfig() {
        this.queryPermission = new Item();
    }

    public String getFile() {
        return file;
    }

    /**
     * 当设定了path时，会立即填充属性值
     *
     * @param file 权限配置的xml文件路径
     */
    public boolean setFile(String file) {
        this.file = file;
        if (file != null && file.length() > 0 && sqlExecutor != null) {
            File xml = new File(file);
            if (xml.isFile()) {
                Document document = XMLUtils.load(xml);
                Element root = document.getRootElement();
                // 获取enabled
                Element enabledEl = root.element("enabled");
                if (enabledEl == null) {
                    enabled = false;
                    return false;
                }
                enabled = Boolean.parseBoolean(enabledEl.getText());

                // 获取innerPermission
                Element innerEl = root.element("inner-permission");
                if (innerEl != null) {
                    innerPermission = innerEl.getText();
                }

                // 获取label
                Element labelEl = root.element("label");
                if (labelEl == null) {
                    LOGGER.warn("Login xml need label to link database.");
                    return false;
                }
                queryPermission.setLabel(labelEl.getText());

                // 获取label
                Element sqlEl = root.element("sql");
                if (sqlEl == null) {
                    LOGGER.warn("Login xml need sql to verify user.");
                    return false;
                }
                queryPermission.setApi("special/permission");
                queryPermission.setSql(sqlEl.getText().replace("\n", " "));
                sqlExecutor.preExecutingItem(queryPermission);
                return true;
            }
        }
        return false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Item getQueryPermission() {
        return queryPermission;
    }

    public void setQueryPermission(Item queryPermission) {
        this.queryPermission = queryPermission;
        this.queryPermission.setApi("special/permission");
        this.sqlExecutor.preExecutingItem(queryPermission);
    }

    public String getInnerPermission() {
        return innerPermission;
    }

    public void setInnerPermission(String innerPermission) {
        this.innerPermission = innerPermission;
    }
}
