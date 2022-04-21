package idea.verlif.justdata.file;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/20 15:34
 */
@Configuration
@ConfigurationProperties(prefix = "just-data.file")
public class FileConfig {

    /**
     * 是否开启文件传输接口
     */
    private boolean enabled;

    /**
     * 是否只允许登录用户操作
     */
    private boolean needOnline;

    /**
     * 文件存储模式
     */
    private UploadType uploadType = UploadType.TIME_MONTH;

    /**
     * 文件权限模式
     */
    private DownloadType downloadType = DownloadType.ALL;


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isNeedOnline() {
        return needOnline;
    }

    public void setNeedOnline(boolean needOnline) {
        this.needOnline = needOnline;
    }

    public UploadType getUploadType() {
        return uploadType;
    }

    public void setUploadType(String uploadType) {
        this.uploadType = UploadType.getType(uploadType);
    }

    public DownloadType getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(String downloadType) {
        this.downloadType = DownloadType.getType(downloadType);
    }

    public enum UploadType {

        /**
         * 以用户ID作为文件域
         */
        USER_ID,

        /**
         * 以上传时间（天）作为文件域
         */
        TIME_DAY,

        /**
         * 以上传时间（月）作为文件域
         */
        TIME_MONTH;

        public static UploadType getType(String type) {
            switch (type.toUpperCase(Locale.ROOT)) {
                case "ID":
                case "USER_ID":
                    return USER_ID;
                case "TIME_DAY":
                case "DAY":
                    return TIME_DAY;
                default:
                    return TIME_MONTH;
            }
        }
    }

    public enum DownloadType {

        /**
         * 只允许用户操作自己的文件域文件
         */
        USER_ID,

        /**
         * 无管控
         */
        ALL;

        public static DownloadType getType(String type) {
            switch (type.toUpperCase(Locale.ROOT)) {
                case "ID":
                case "USER_ID":
                    return USER_ID;
                default:
                    return ALL;
            }
        }
    }
}
