package idea.verlif.justdata.file;

import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ResultCode;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.justdata.base.result.ext.OkResult;
import idea.verlif.justdata.user.UserService;
import idea.verlif.spring.file.FileService;
import idea.verlif.spring.file.domain.FileCart;
import idea.verlif.spring.file.domain.FilePage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/20 14:29
 */
@Tag(name = "文件上传与下载")
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileConfig fileConfig;

    @Operation(summary = "上传文件")
    @PostMapping
    public BaseResult<Object> upload(
            @Parameter(name = "文件类型") @RequestParam(defaultValue = "") String type,
            @Parameter(name = "上传的文件数组") MultipartFile[] file
    ) throws IOException {
        BaseResult<Object> br = checkPermission();
        if (!br.equals(ResultCode.SUCCESS)) {
            return br;
        }
        FileCart fileCart;
        switch (fileConfig.getUploadType()) {
            case USER_ID:
                fileCart = new FileCart(UserService.getLoginUser().getId().toString());
                break;
            case TIME_DAY: {
                Calendar calendar = Calendar.getInstance();
                fileCart = new FileCart(
                        calendar.get(Calendar.YEAR) + "-" +
                                calendar.get(Calendar.MONTH) + "-" +
                                calendar.get(Calendar.DAY_OF_MONTH));
                break;
            }
            default:
                Calendar calendar = Calendar.getInstance();
                fileCart = new FileCart(
                        calendar.get(Calendar.YEAR) + "-" +
                                calendar.get(Calendar.MONTH));
                break;
        }
        if (fileService.uploadFile(fileCart, type, file) > 0) {
            return OkResult.empty();
        } else {
            return FailResult.empty();
        }
    }

    @Operation(summary = "获取文件列表")
    @GetMapping("/infolist")
    public BaseResult<?> fileList(
            @Parameter(name = "查询条件") FileQueryExt query
    ) {
        BaseResult<Object> br = checkPermission();
        if (!br.equals(ResultCode.SUCCESS)) {
            return br;
        }
        if (query.getPath() == null) {
            query.setPath("");
        }
        boolean onUser = fileConfig.getDownloadType() == FileConfig.DownloadType.USER_ID;
        String[] ss = query.getPath().replace("\\", "/").split("/", 2);
        String cart, type;
        if (onUser) {
            cart = UserService.getLoginUser().getId().toString();
            if (ss[0].length() == 0) {
                ss[0] = cart;
            }
            if (ss[0].equals(cart)) {
                cart = ss[0];
            } else {
                return new FailResult<>(ResultCode.FAILURE_UNAVAILABLE);
            }
            if (ss.length == 2) {
                type = ss[1];
            } else {
                type = "";
            }
        } else {
            if (ss.length == 1) {
                cart = ss[0];
                type = "";
            } else {
                cart = ss[0];
                type = ss[1];
            }
        }
        FilePage page = fileService.getFileList(new FileCart(cart), type, query);
        return new OkResult<>(page);
    }

    @Operation(summary = "下载文件")
    @GetMapping
    public BaseResult<?> download(
            @Parameter(name = "文件名", description = "包括了文件域的相对文件名") @RequestParam String filename,
            HttpServletResponse response
    ) throws IOException {
        BaseResult<Object> br = checkPermission();
        if (!br.equals(ResultCode.SUCCESS)) {
            return br;
        }
        boolean onUser = fileConfig.getDownloadType() == FileConfig.DownloadType.USER_ID;
        String[] ss = filename.replace("\\", "/").split("/", 3);
        String cart, type;
        if (onUser) {
            if (ss.length == 1) {
                return new FailResult<>(ResultCode.FAILURE_PARAMETER_LACK);
            }
            if (ss[0].equals(UserService.getLoginUser().getId().toString())) {
                cart = ss[0];
            } else {
                return new FailResult<>(ResultCode.FAILURE_UNAVAILABLE);
            }
            if (ss.length == 2) {
                type = "";
                filename = ss[1];
            } else {
                type = ss[1];
                filename = ss[2];
            }
        } else {
            if (ss.length == 1) {
                cart = "";
                type = "";
                filename = ss[0];
            } else if (ss.length == 2) {
                cart = ss[0];
                type = "";
                filename = ss[1];
            } else {
                cart = ss[0];
                type = ss[1];
                filename = ss[2];
            }
        }
        if (fileService.downloadFile(response, new FileCart(cart), type, filename)) {
            return OkResult.empty();
        } else {
            return FailResult.empty();
        }
    }

    @Operation(summary = "删除文件")
    @DeleteMapping
    public BaseResult<?> delete(@Parameter(name = "文件名", description = "包括了文件域的相对文件名") String filename) {
        BaseResult<Object> br = checkPermission();
        if (!br.equals(ResultCode.SUCCESS)) {
            return br;
        }
        if (filename == null) {
            return new FailResult<>(ResultCode.FAILURE_PARAMETER_LACK).withParam("filename");
        }
        boolean onUser = fileConfig.getDownloadType() == FileConfig.DownloadType.USER_ID;
        String[] ss = filename.replace("\\", "/").split("/", 3);
        String cart, type;
        if (onUser) {
            if (ss.length == 1) {
                return new FailResult<>(ResultCode.FAILURE_PARAMETER_LACK);
            }
            if (ss[0].equals(UserService.getLoginUser().getId().toString())) {
                cart = ss[0];
            } else {
                return new FailResult<>(ResultCode.FAILURE_UNAVAILABLE);
            }
            if (ss.length == 2) {
                type = "";
                filename = ss[1];
            } else {
                type = ss[1];
                filename = ss[2];
            }
        } else {
            if (ss.length == 1) {
                cart = "";
                type = "";
                filename = ss[0];
            } else if (ss.length == 2) {
                cart = ss[0];
                type = "";
                filename = ss[1];
            } else {
                cart = ss[0];
                type = ss[1];
                filename = ss[2];
            }
        }
        if (fileService.deleteFile(new FileCart(cart), type, filename)) {
            return OkResult.empty();
        } else {
            return FailResult.empty();
        }
    }

    /**
     * 检查访问权限
     *
     * @return 检查结果
     */
    private BaseResult<Object> checkPermission() {
        if (fileConfig.isEnabled()) {
            if (fileConfig.isNeedOnline() && !UserService.isOnline()) {
                return new FailResult<>(ResultCode.FAILURE_NOT_LOGIN);
            } else {
                return OkResult.empty();
            }
        } else {
            return new FailResult<>(ResultCode.FAILURE_DISABLED_FILE);
        }
    }
}
