package idea.verlif.justdata.user;

import idea.verlif.justdata.security.token.TokenService;
import idea.verlif.justdata.user.exception.CustomException;
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

    public LoginUser getLoginUser(String token) {
        return tokenService.getUserByToken(token);
    }

    public void refreshLoginUser(LoginUser user) {
        tokenService.refreshUser(user);
    }

    public String login(LoginUser user) {
        return tokenService.loginUser(user);
    }

    /**
     * 获取用户
     **/
    public static <T extends LoginUser> T getLoginUser() {
        try {
            Authentication authentication = getAuthentication();
            if (authentication == null) {
                return null;
            }
            return (T) authentication.getPrincipal();
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
