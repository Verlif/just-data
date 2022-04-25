package idea.verlif.justdata.sql;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/25 15:36
 */
@Configuration
@ConfigurationProperties(prefix = "just-data.sql")
public class SqlConfig {

    /**
     * 是否输出到控制台
     */
    private boolean print;

    public boolean isPrint() {
        return print;
    }

    public void setPrint(boolean print) {
        this.print = print;
    }
}
