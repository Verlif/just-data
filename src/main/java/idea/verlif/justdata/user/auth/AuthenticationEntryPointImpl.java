package idea.verlif.justdata.user.auth;

import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ResultCode;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.justdata.user.LoginUser;
import idea.verlif.justdata.user.UserService;
import idea.verlif.justdata.util.ServletUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * 认证失败处理类 返回未授权
 *
 * @author Verlif
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        // 验证用户是否登录，这里采用判定token的方式
        LoginUser loginUser = UserService.getLoginUser();
        if (loginUser == null) {
            ServletUtils.sendResult(response, new FailResult<>(ResultCode.FAILURE_NOT_LOGIN));
        } else {
            ServletUtils.sendResult(response, new FailResult<>(ResultCode.FAILURE_UNAVAILABLE));
        }
    }
}
