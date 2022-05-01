package idea.verlif.justdata.special.user;

import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ResultCode;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.justdata.base.result.ext.OkResult;
import idea.verlif.justdata.encrypt.code.Encoder;
import idea.verlif.justdata.encrypt.rsa.RsaService;
import idea.verlif.justdata.item.Item;
import idea.verlif.justdata.router.RouterManager;
import idea.verlif.justdata.special.login.BaseUser;
import idea.verlif.justdata.special.login.LoginConfig;
import idea.verlif.justdata.special.login.LoginUser;
import idea.verlif.justdata.special.permission.PermissionCheck;
import idea.verlif.justdata.special.permission.PermissionConfig;
import idea.verlif.justdata.sql.SqlExecutor;
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
    public BaseResult<?> login(@RequestBody BaseUser user, HttpServletRequest request) throws Exception {
        if (loginConfig.isEnabled()) {
            Item userKey = loginConfig.getQueryUserKey();
            if (!userKey.isAccessible() || user.getId() == null || user.getKey() == null) {
                return new FailResult<>(ResultCode.FAILURE_LOGIN_FAIL).withParam("Lack of queryUser param!");
            }
            Map<String, Object> map = RequestUtils.getMapFromRequest(request);
            ResultSet set = sqlExecutor.query(userKey, map, user.toString());
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
                    if (permissionConfig.isEnabled() && permissionConfig.getQueryPermission().isAccessible()) {
                        Item queryPermission = permissionConfig.getQueryPermission();
                        ResultSet permissionSet = sqlExecutor.query(queryPermission, map, user.toString());
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
            if (!loginConfig.setFile(loginConfig.getFile())) {
                LOGGER.warn("Login xml can not be loaded.");
                return new FailResult<>("Login xml");
            }
            if (!permissionConfig.setFile(permissionConfig.getFile())) {
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
