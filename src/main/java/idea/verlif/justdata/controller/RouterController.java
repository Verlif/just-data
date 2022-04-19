package idea.verlif.justdata.controller;

import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ResultCode;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.justdata.base.result.ext.OkResult;
import idea.verlif.justdata.route.Router;
import idea.verlif.justdata.route.RouterManager;
import idea.verlif.justdata.user.permission.PermissionCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @author Verlif
 */
@RequestMapping("/router")
@RestController
public class RouterController {

    @Autowired
    private RouterManager routerManager;

    @Autowired
    private PermissionCheck permissionCheck;

    @GetMapping("/label/list")
    public BaseResult<Set<String>> labelList() {
        if (permissionCheck.hasInnerPermission()) {
            return new OkResult<>(routerManager.labelSet());
        } else {
            return new FailResult<>(ResultCode.FAILURE_UNAVAILABLE);
        }
    }

    @GetMapping("/label/{label}/api")
    public BaseResult<Router.RouterInfo> apiList(@PathVariable String label) {
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
}
