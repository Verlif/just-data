package idea.verlif.justdata.item;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/15 15:49
 */
@Configuration
@ConfigurationProperties(prefix = "just-data.items")
public class ItemConfig {

    /**
     * 操作项文件夹路径
     */
    private String path = "./";

    /**
     * 文件变更后是否自动重载操作项
     */
    private boolean autoReload;

    /**
     * 操作项文件重载时间间隔
     */
    private long period;

    public ItemConfig() {
        period = 2000;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAutoReload() {
        return autoReload;
    }

    public void setAutoReload(boolean autoReload) {
        this.autoReload = autoReload;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }
}
