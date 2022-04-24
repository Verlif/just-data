package idea.verlif.justdata.special.login.auth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 认证处理器
 *
 * @author Verlif
 * @version 1.0
 * @date 2022/1/7 10:27
 */
public interface AuthHandler {

    /**
     * 认证方法
     *
     * @param id    认证ID
     * @param token 认证令牌
     * @return 认证对象信息。认证失败请抛出相应异常，异常应继承自{@link AuthenticationException}，也可返回null。
     */
    UserDetails auth(String id, String token) throws AuthenticationException;
}
