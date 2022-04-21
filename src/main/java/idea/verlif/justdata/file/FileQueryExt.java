package idea.verlif.justdata.file;

import idea.verlif.spring.file.domain.FileQuery;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/21 9:35
 */
@Schema(name = "文件搜索")
public class FileQueryExt extends FileQuery {

    @Schema(name = "文件夹路径")
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
