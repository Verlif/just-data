package idea.verlif.justdata.user.auth;

import idea.verlif.justdata.util.MessagesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * 登录验证管理
 *
 * @author Verlif
 * @version 1.0
 * @date 2022/1/7 10:24
 */
@Component
public class StationAttestation implements AuthenticationProvider {

    @Autowired(required = false)
    private AuthHandler authHandler;

    public StationAttestation() {
        authHandler = new NoAuthHandler();
    }

    public void setAuthHandler(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof StationAuthentication) {
            UserDetails user = authHandler.auth(authentication.getPrincipal().toString(), authentication.getCredentials().toString());
            if (user == null) {
                throw new AuthenticationException(MessagesUtils.message("result.fail.login")) {
                };
            } else {
                ((StationAuthentication) authentication).setDetails(user);
            }
            return authentication;
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(StationAuthentication.class);
    }
}
