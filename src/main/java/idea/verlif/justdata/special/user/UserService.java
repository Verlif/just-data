package idea.verlif.justdata.special.user;

import idea.verlif.justdata.security.token.TokenConfig;
import idea.verlif.justdata.security.token.TokenService;
import idea.verlif.justdata.special.login.auth.StationAuthentication;
import idea.verlif.justdata.special.login.exception.CustomException;
import idea.verlif.justdata.special.login.LoginUser;
import idea.verlif.justdata.util.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/18 15:55
 */
@Service
public class UserService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenConfig tokenConfig;

    public LoginUser getLoginUser(String token) {
        return tokenService.getUserByToken(token);
    }

    public void refreshLoginUser(LoginUser user) {
        tokenService.refreshUser(user);
    }

    public String login(LoginUser user) {
        String token = tokenService.loginUser(user);
        if (token != null) {
            StationAuthentication authentication = new StationAuthentication();
            authentication.setDetails(user);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        return token;
    }

    public void logout() {
        String token = tokenService.getTokenFromRequest(ServletUtils.getRequest());
        tokenService.logout(token);
    }

    /**
     * 获取用户
     **/
    public static <T extends LoginUser> T getLoginUser() {
        try {
            Authentication authentication = getAuthentication();
            if (authentication == null) {
                throw new CustomException();
            }
            return (T) authentication.getPrincipal();
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    /**
     * 当前是否有用户登录
     */
    public static boolean isOnline() {
        try {
            Authentication authentication = getAuthentication();
            if (authentication == null) {
                return false;
            }
            return authentication.getPrincipal() != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
