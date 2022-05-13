package idea.verlif.justdata.macro;

import idea.verlif.justdata.macro.handler.UserIdMacro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/19 11:02
 */
@Configuration
@ConfigurationProperties(prefix = "just-data.macro")
public class MacroManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MacroManager.class);

    private String file;

    private final Properties properties;

    private final Map<String, MarcoHandler> macroMap;

    public MacroManager() {
        macroMap = new ConcurrentHashMap<>();
        properties = new Properties();

        init();
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
        File propFile = new File(file);
        if (propFile.exists() && propFile.isFile()) {
            try (Reader reader = new FileReader(propFile)) {
                properties.clear();
                properties.load(reader);
                LOGGER.info("Loaded macros from " + file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void resetProperties() {
        if (file != null) {
            setFile(file);
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

}
