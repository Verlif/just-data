package idea.verlif.justdata.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ResultCode;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.justdata.base.result.ext.OkResult;
import idea.verlif.justdata.item.Item;
import idea.verlif.justdata.log.api.ApiDeleteLogHandler;
import idea.verlif.justdata.log.api.ApiGetLogHandler;
import idea.verlif.justdata.log.api.ApiPostLogHandler;
import idea.verlif.justdata.log.api.ApiPutLogHandler;
import idea.verlif.justdata.router.Router;
import idea.verlif.justdata.router.RouterManager;
import idea.verlif.justdata.sql.SqlExecutor;
import idea.verlif.justdata.user.permission.PermissionCheck;
import idea.verlif.justdata.util.RequestUtils;
import idea.verlif.justdata.util.ResultSetUtils;
import idea.verlif.spring.logging.api.LogIt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/8 11:33
 */
@Tag(name = "API接口访问")
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private RouterManager routerManager;

    @Autowired
    private SqlExecutor sqlExecutor;

    @Autowired
    private PermissionCheck permissionCheck;

    @Operation(summary = "Get接口")
    @LogIt(message = "", handler = ApiGetLogHandler.class)
    @GetMapping("/{label}/{api}")
    public BaseResult<List<Map<String, Object>>> get(
            @Parameter(name = "标签") @PathVariable String label,
            @Parameter(name = "接口API") @PathVariable String api,
            HttpServletRequest request) throws SQLException, JsonProcessingException {
        Router router = routerManager.getRouter(label);
        if (router == null) {
            return new FailResult<>(ResultCode.FAILURE_NO_LABEL);
        }
        Item item = router.get(api);
        if (item == null) {
            return new FailResult<>(ResultCode.FAILURE_NO_API);
        }
        if (permissionCheck.hasPermission(item.getPermission())) {
            Map<String, Object> map = RequestUtils.getMapFromRequest(request);
            String body = RequestUtils.getBodyFromRequest(request);
            ResultSet set = sqlExecutor.exec(item, map, body);
            return new OkResult<>(ResultSetUtils.toMapList(set));
        } else {
            return new FailResult<>(ResultCode.FAILURE_UNAVAILABLE);
        }
    }

    @Operation(summary = "Post接口")
    @LogIt(message = "", handler = ApiPostLogHandler.class)
    @PostMapping("/{label}/{api}")
    public BaseResult<String> post(
            @Parameter(name = "标签") @PathVariable String label,
            @Parameter(name = "接口API") @PathVariable String api,
            HttpServletRequest request) throws SQLException, JsonProcessingException {
        Router router = routerManager.getRouter(label);
        if (router == null) {
            return new FailResult<>(ResultCode.FAILURE_NO_LABEL);
        }
        Item item = router.post(api);
        if (item == null) {
            return new FailResult<>(ResultCode.FAILURE_NO_API);
        }
        if (permissionCheck.hasPermission(item.getPermission())) {
            Map<String, Object> map = RequestUtils.getMapFromRequest(request);
            String body = RequestUtils.getBodyFromRequest(request);
            if (sqlExecutor.update(item, map, body)) {
                return OkResult.empty();
            } else {
                return FailResult.empty();
            }
        } else {
            return new FailResult<>(ResultCode.FAILURE_UNAVAILABLE);
        }
    }

    @Operation(summary = "Put接口")
    @LogIt(message = "", handler = ApiPutLogHandler.class)
    @PutMapping("/{label}/{api}")
    public BaseResult<String> put(
            @Parameter(name = "标签") @PathVariable String label,
            @Parameter(name = "接口API") @PathVariable String api,
            HttpServletRequest request) throws SQLException, JsonProcessingException {
        Router router = routerManager.getRouter(label);
        if (router == null) {
            return new FailResult<>(ResultCode.FAILURE_NO_LABEL);
        }
        Item item = router.post(api);
        if (item == null) {
            return new FailResult<>(ResultCode.FAILURE_NO_API);
        }
        if (permissionCheck.hasPermission(item.getPermission())) {
            Map<String, Object> map = RequestUtils.getMapFromRequest(request);
            String body = RequestUtils.getBodyFromRequest(request);
            if (sqlExecutor.update(item, map, body)) {
                return OkResult.empty();
            } else {
                return FailResult.empty();
            }
        } else {
            return new FailResult<>(ResultCode.FAILURE_UNAVAILABLE);
        }
    }

    @Operation(summary = "Delete接口")
    @LogIt(message = "", handler = ApiDeleteLogHandler.class)
    @DeleteMapping("/{label}/{api}")
    public BaseResult<String> delete(
            @Parameter(name = "标签") @PathVariable String label,
            @Parameter(name = "接口API") @PathVariable String api,
            HttpServletRequest request) throws SQLException, JsonProcessingException {
        Router router = routerManager.getRouter(label);
        if (router == null) {
            return new FailResult<>(ResultCode.FAILURE_NO_LABEL);
        }
        Item item = router.post(api);
        if (item == null) {
            return new FailResult<>(ResultCode.FAILURE_NO_API);
        }
        if (permissionCheck.hasPermission(item.getPermission())) {
            Map<String, Object> map = RequestUtils.getMapFromRequest(request);
            String body = RequestUtils.getBodyFromRequest(request);
            if (sqlExecutor.update(item, map, body)) {
                return OkResult.empty();
            } else {
                return FailResult.empty();
            }
        } else {
            return new FailResult<>(ResultCode.FAILURE_UNAVAILABLE);
        }
    }

}
