package idea.verlif.justdata.item;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/15 15:49
 */
@Configuration
@ConfigurationProperties(prefix = "items")
public class ItemConfig {

    private String path = "./";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
