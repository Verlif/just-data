package idea.verlif.justdata.user;

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
    private boolean enable;

    /**
     * 获取用户密钥
     */
    private Sql queryUserKey;

    public LoginConfig() {
        queryUserKey = new Sql();
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Sql getQueryUserKey() {
        return queryUserKey;
    }

    public void setQueryUserKey(Sql queryUserKey) {
        this.queryUserKey = queryUserKey;
    }

    public static final class Sql {

        private String label;
        private String sql;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }
    }
}
