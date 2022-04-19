package idea.verlif.justdata.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ResultCode;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.justdata.base.result.ext.OkResult;
import idea.verlif.justdata.route.RouterManager;
import idea.verlif.justdata.sql.SqlExecutor;
import idea.verlif.justdata.util.RequestUtils;
import idea.verlif.justdata.util.ResultSetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/18 14:15
 */
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

    @PostMapping("/login")
    public BaseResult<String> login(@RequestBody BaseUser user, HttpServletRequest request) throws SQLException, JsonProcessingException {
        if (loginConfig.isEnable()) {
            LoginConfig.Sql sql = loginConfig.getQueryUserKey();
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
            if (key.equals(user.getKey())) {
                LoginUser loginUser = new LoginUser(user.getId());
                loginUser.setLoginTime(new Date());
                String token = userService.login(loginUser);
                if (token == null) {
                    return new FailResult<>(ResultCode.FAILURE_LOGIN_FAIL);
                } else {
                    return new OkResult<>(token);
                }
            } else {
                return new FailResult<>(ResultCode.FAILURE_LOGIN_FAIL);
            }
        }
        return new FailResult<>(ResultCode.FAILURE_DISABLED_LOGIN);
    }
}
