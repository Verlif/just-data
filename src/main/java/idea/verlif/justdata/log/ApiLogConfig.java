package idea.verlif.justdata.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/25 11:18
 */
@Configuration
@ConfigurationProperties(prefix = "just-data.api-log")
public class ApiLogConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiLogConfig.class);

    private int type = 0;

    private FileInfo file;

    public ApiLogConfig() {
        this.type = 0;
        this.file = new FileInfo();
    }

    public void setType(String type) {
        for (String s : type.split(",")) {
            addType(s.trim().toUpperCase(Locale.ROOT));
        }
    }

    public void addType(String type) {
        switch (type.trim().toUpperCase(Locale.ROOT)) {
            case "XML":
                this.type = this.type | ApiLogType.ON_XML;
                break;
            case "FILE":
                this.type = this.type | ApiLogType.ON_FILE;
                break;
            default:
                this.type = this.type | ApiLogType.ON_CONSOLE;
        }
    }

    public int getType() {
        return type;
    }

    public boolean onConsole() {
        return (type & ApiLogType.ON_CONSOLE) > 0;
    }

    public boolean onFile() {
        return (type & ApiLogType.ON_FILE) > 0;
    }

    public boolean onXml() {
        return (type & ApiLogType.ON_XML) > 0;
    }

    public FileInfo getFile() {
        return file;
    }

    public File getXmlFile() {
        return file.xml;
    }

    public File getLogFile() {
        return file.log;
    }

    public void setFile(FileInfo file) {
        this.file = file;
    }

    private interface ApiLogType {
        int ON_CONSOLE = 1;
        int ON_FILE = 1 << 1;
        int ON_XML = 1 << 2;
    }

    private static final class FileInfo {

        private File xml;

        private File log;

        public File getLog() {
            if (log == null) {
                log = new File("api-log.log");
            }
            return log;
        }

        public void setLog(String log) throws IOException {
            this.log = new File(log);
            if (!this.log.exists()) {
                File parent = this.log.getParentFile();
                if (!parent.exists()) {
                    if (!parent.mkdirs()) {
                        throw new FileNotFoundException(parent.getPath() + " can not be created.");
                    }
                }
                if (!this.log.createNewFile()) {
                    throw new FileNotFoundException(this.log.getPath() + " can not be created.");
                } else {
                    LOGGER.debug(this.log.getPath() + " is created.");
                }
            }
        }

        public File getXml() {
            return xml;
        }

        public void setXml(String xml) {
            this.xml = new File(xml);
        }
    }
}
