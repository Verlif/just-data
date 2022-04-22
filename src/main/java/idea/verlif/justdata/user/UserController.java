package idea.verlif.justdata.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ResultCode;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.justdata.base.result.ext.OkResult;
import idea.verlif.justdata.encrypt.code.Encoder;
import idea.verlif.justdata.encrypt.rsa.RsaService;
import idea.verlif.justdata.router.RouterManager;
import idea.verlif.justdata.sql.Sql;
import idea.verlif.justdata.sql.SqlExecutor;
import idea.verlif.justdata.user.login.BaseUser;
import idea.verlif.justdata.user.login.LoginConfig;
import idea.verlif.justdata.user.login.LoginUser;
import idea.verlif.justdata.user.permission.PermissionCheck;
import idea.verlif.justdata.user.permission.PermissionConfig;
import idea.verlif.justdata.util.RequestUtils;
import idea.verlif.justdata.util.ResultSetUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/18 14:15
 */
@Tag(name = "用户相关")
@RestController
@RequestMapping("/special")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private RouterManager routerManager;

    @Autowired
    private SqlExecutor sqlExecutor;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginConfig loginConfig;

    @Autowired
    private PermissionConfig permissionConfig;

    @Autowired
    private PermissionCheck permissionCheck;

    @Autowired
    private Encoder encoder;

    @Autowired
    private RsaService rsaService;

    @Operation(summary = "用户登录", description = "当开启了登录配置后，登录接口生效")
    @PostMapping("/login")
    public BaseResult<String> login(@RequestBody BaseUser user, HttpServletRequest request) throws SQLException, JsonProcessingException {
        if (loginConfig.isEnabled()) {
            Sql sql = loginConfig.getQueryUserKey();
            if (sql.getSql() == null || sql.getLabel() == null) {
                LOGGER.error("Lack of queryUser param!");
                return new FailResult<>(ResultCode.FAILURE_LOGIN_FAIL);
            }
            Map<String, Object> map = RequestUtils.getMapFromRequest(request);
            ResultSet set = sqlExecutor.exec(sql.getLabel(), sql.getSql(), map, user.toString());
            String key = ResultSetUtils.toString(set);
            if (key == null) {
                return new FailResult<>(ResultCode.FAILURE_LOGIN_MISSING);
            }
            // 尝试RSA解密
            String dekey = rsaService.decryptByPrivateKey(user.getKey());
            if (dekey != null && dekey.length() > 0) {
                user.setKey(dekey);
            }
            if (key.equals(user.getKey()) || encoder.matches(user.getKey(), key)) {
                LoginUser loginUser = new LoginUser(user.getId());
                loginUser.setLoginTime(new Date());
                String token = userService.login(loginUser);
                if (token == null) {
                    return new FailResult<>(ResultCode.FAILURE_LOGIN_FAIL);
                } else {
                    // 权限赋值
                    if (permissionConfig.isEnabled() && permissionConfig.getQueryPermission().isEnabled()) {
                        sql = permissionConfig.getQueryPermission();
                        ResultSet permissionSet = sqlExecutor.exec(sql.getLabel(), sql.getSql(), map, user.toString());
                        List<String> pList = ResultSetUtils.toStringList(permissionSet);
                        loginUser.addPermission(pList);
                        userService.refreshLoginUser(loginUser);
                    }
                    return new OkResult<>(token);
                }
            } else {
                return new FailResult<>(ResultCode.FAILURE_LOGIN_FAIL);
            }
        }
        return new FailResult<>(ResultCode.FAILURE_DISABLED_LOGIN);
    }

    @Operation(summary = "配置刷新", description = "刷新Login与Permission的XML配置")
    @PutMapping("/user/xml")
    public BaseResult<?> refreshConfig() {
        if (permissionCheck.hasInnerPermission()) {
            LoginConfig config = new LoginConfig();
            if (config.setFile(loginConfig.getFile())) {
                loginConfig.setEnabled(config.isEnabled());
                loginConfig.setFile(config.getFile());
                loginConfig.setQueryUserKey(config.getQueryUserKey());
            } else {
                LOGGER.warn("Login xml can not be loaded.");
                return new FailResult<>("Login xml");
            }

            PermissionConfig premConfig = new PermissionConfig();
            if (premConfig.setFile(permissionConfig.getFile())) {
                permissionConfig.setEnabled(premConfig.isEnabled());
                permissionConfig.setFile(premConfig.getFile());
                permissionConfig.setInnerPermission(permissionConfig.getInnerPermission());
                permissionConfig.setQueryPermission(premConfig.getQueryPermission());
            } else {
                LOGGER.warn("Permission xml can not be loaded.");
                return new FailResult<>("Permission xml");
            }

            return OkResult.empty();
        } else {
            return new FailResult<>(ResultCode.FAILURE_UNAVAILABLE);
        }
    }

    @Operation(summary = "用户登出")
    @PutMapping("/logout")
    public BaseResult<String> logout() {
        userService.logout();
        return OkResult.empty();
    }
}
