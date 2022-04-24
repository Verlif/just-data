package idea.verlif.justdata.security.token;

import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ResultCode;
import idea.verlif.justdata.special.login.LoginUser;
import idea.verlif.justdata.special.login.auth.StationAuthentication;
import idea.verlif.justdata.util.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * token过滤器 验证token有效性
 *
 * @author Verlif
 */
@Component
public class TokenFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = tokenService.getTokenFromRequest(request);
        // 没有token则移交给下一个过滤器
        if (token == null || token.length() == 0) {
            chain.doFilter(request, response);
            return;
        }
        //token解析失败
        if (tokenService.parseToken(token) == null) {
            ServletUtils.sendResult(response, new BaseResult<>(ResultCode.FAILURE_TOKEN));
            return;
        }
        LoginUser user = tokenService.getUserByToken(token);
        //刷新token过期时间
        if (user != null) {
            // 填充本次的登录用户信息
            tokenService.refreshUser(user);
            StationAuthentication authentication = new StationAuthentication();
            authentication.setDetails(user);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
