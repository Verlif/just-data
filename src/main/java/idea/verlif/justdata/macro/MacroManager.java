package idea.verlif.justdata.macro;

import idea.verlif.justdata.macro.handler.UserIdMacro;
import idea.verlif.spring.taskservice.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/19 11:02
 */
@Configuration
@ConfigurationProperties(prefix = "just-data.macro")
public class MacroManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MacroManager.class);

    /**
     * 全局变量文件路径
     */
    private String file;

    /**
     * 是否自动刷新文件内容
     */
    private boolean autoReload;

    /**
     * 文件刷新间隔
     */
    private long period;

    private final Properties properties;

    private final Map<String, MarcoHandler> macroMap;

    @Autowired
    private TaskService taskService;

    public MacroManager() {
        macroMap = new HashMap<>();
        autoReload = true;
        period = 2000;
        properties = new Properties();

        init();
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
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

    @PostConstruct
    public void checkReloadFile() {
        if (file != null && autoReload) {
            taskService.getSchedule().scheduleAtFixedRate(new ReloadMacroFile(), period);
        } else {
            new ReloadMacroFile().run();
        }
    }

    private void init() {
        addMacro(new UserIdMacro());
    }

    public void addMacro(MarcoHandler handler) {
        macroMap.put(handler.getKey(), handler);
    }

    /**
     * 优先从变量表中取值，当没有值时，从动态变量表中取值。
     *
     * @param key 值对应的key
     * @return key对应的全局变量值
     */
    public String get(String key) {
        String s = properties.getProperty(key);
        if (s != null) {
            return s;
        }
        MarcoHandler handler = macroMap.get(key);
        if (key == null || handler == null) {
            return null;
        }
        return handler.getValue();
    }

    private final class ReloadMacroFile implements Runnable {

        private final File propFile;
        private long last;

        public ReloadMacroFile() {
            if (file != null) {
                File f = new File(file);
                if (f.exists() && f.isFile()) {
                    this.propFile = f;
                } else {
                    this.propFile = null;
                }
            } else {
                this.propFile = null;
            }
            last = 0;
        }

        @Override
        public void run() {
            if (propFile != null) {
                long nowTime = propFile.lastModified();
                if (last != nowTime) {
                    last = nowTime;
                    try (Reader reader = new FileReader(propFile)) {
                        properties.clear();
                        properties.load(reader);
                        LOGGER.info("Loaded macros from " + file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
