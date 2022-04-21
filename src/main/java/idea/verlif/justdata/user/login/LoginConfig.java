package idea.verlif.justdata.user.login;

import idea.verlif.justdata.sql.Sql;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/19 10:31
 */
@Configuration
@ConfigurationProperties(prefix = "just-data.login")
public class LoginConfig {

    /**
     * 是否开启登录
     */
    private boolean enabled;

    /**
     * 获取用户密钥
     */
    private Sql queryUserKey;

    public LoginConfig() {
        queryUserKey = new Sql();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Sql getQueryUserKey() {
        return queryUserKey;
    }

    public void setQueryUserKey(Sql queryUserKey) {
        this.queryUserKey = queryUserKey;
    }

}
