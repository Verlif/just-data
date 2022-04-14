package idea.verlif.justdata.route;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/14 17:01
 */
@Configuration
@ConfigurationProperties(prefix = "route")
public class RouteConfig {

    private String path = "./";
    private List<String> blocked;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getBlocked() {
        return blocked;
    }

    public void setBlocked(List<String> blocked) {
        this.blocked = blocked;
    }
}
