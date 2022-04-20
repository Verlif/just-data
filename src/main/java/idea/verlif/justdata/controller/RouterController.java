package idea.verlif.justdata.controller;

import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ResultCode;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.justdata.base.result.ext.OkResult;
import idea.verlif.justdata.router.Router;
import idea.verlif.justdata.router.RouterManager;
import idea.verlif.justdata.user.permission.PermissionCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author Verlif
 */
@Tag(name = "路由接口")
@RequestMapping("/router")
@RestController
public class RouterController {

    @Autowired
    private RouterManager routerManager;

    @Autowired
    private PermissionCheck permissionCheck;

    @Operation(summary = "标签列表")
    @GetMapping("/label/list")
    public BaseResult<Set<String>> labelList() {
        if (permissionCheck.hasInnerPermission()) {
            return new OkResult<>(routerManager.labelSet());
        } else {
            return new FailResult<>(ResultCode.FAILURE_UNAVAILABLE);
        }
    }

    @Operation(summary = "标签下的API列表")
    @GetMapping("/{label}/api")
    public BaseResult<Router.RouterInfo> apiList(
            @Parameter(name = "标签") @PathVariable String label) {
        if (permissionCheck.hasInnerPermission()) {
            Router router = routerManager.getRouter(label);
            if (router == null) {
                return new FailResult<>(ResultCode.FAILURE_NO_LABEL);
            }
            return new OkResult<>(router.getInfo());
        } else {
            return new FailResult<>(ResultCode.FAILURE_UNAVAILABLE);
        }
    }

    @Operation(summary = "重载XML配置", description = "重新加载XML文件，并重新生成标签与API接口。数据源不会被重载。")
    @PostMapping("/reload")
    public BaseResult<String> reload() {
        if (permissionCheck.hasInnerPermission()) {
            routerManager.reloadRouter();
            return OkResult.empty();
        } else {
            return new FailResult<>(ResultCode.FAILURE_UNAVAILABLE);
        }
    }
}
