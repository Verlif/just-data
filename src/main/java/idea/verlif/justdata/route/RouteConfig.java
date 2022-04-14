package idea.verlif.justdata.route;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
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
    private final BlockedList blockedList;

    public RouteConfig() {
        blockedList = new BlockedList();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public BlockedList getBlockedList() {
        return blockedList;
    }

    public boolean isAllowApi(String api) {
        return !blockedList.apiList.contains(api);
    }

    public boolean isAllowLabel(String label) {
        return !blockedList.labelList.contains(label);
    }

    public static final class BlockedList {

        private final List<String> apiList;
        private final List<String> labelList;

        public BlockedList() {
            apiList = new ArrayList<>();
            labelList = new ArrayList<>();
        }

        public List<String> getApiList() {
            return apiList;
        }

        public List<String> getLabelList() {
            return labelList;
        }

    }
}
