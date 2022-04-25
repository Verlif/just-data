package idea.verlif.justdata.special.login.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/1/7 11:27
 */
public class NoAuthHandler implements AuthHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoAuthHandler.class);

    @Override
    public UserDetails auth(String id, String token) throws AuthenticationException {
        LOGGER.error("No such AuthHandler can auth!");
        return null;
    }
}
