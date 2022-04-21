package idea.verlif.justdata.user.permission;

import idea.verlif.justdata.sql.Sql;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/19 10:31
 */
@Configuration
@ConfigurationProperties(prefix = "just-data.permission")
public class PermissionConfig {

    /**
     * 是否开启登录
     */
    private boolean enabled;

    /**
     * 获取用户密钥
     */
    private Sql queryPermission;

    /**
     * 内置接口需要的权限
     */
    private String innerPermission;

    public PermissionConfig() {
        queryPermission = new Sql();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Sql getQueryPermission() {
        return queryPermission;
    }

    public void setQueryPermission(Sql queryPermission) {
        this.queryPermission = queryPermission;
    }

    public String getInnerPermission() {
        return innerPermission;
    }

    public void setInnerPermission(String innerPermission) {
        this.innerPermission = innerPermission;
    }
}
