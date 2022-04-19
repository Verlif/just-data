package idea.verlif.justdata.security.token.impl;

import idea.verlif.justdata.cache.CacheHandler;
import idea.verlif.justdata.security.token.TokenConfig;
import idea.verlif.justdata.security.token.TokenService;
import idea.verlif.justdata.user.login.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/1/7 14:57
 */
public class DefaultTokenService implements TokenService {

    private final CacheHandler cacheHandler;
    private final TokenConfig tokenConfig;

    public DefaultTokenService(CacheHandler cacheHandler, TokenConfig tokenConfig) {
        this.cacheHandler = cacheHandler;
        this.tokenConfig = tokenConfig;
    }

    @Override
    public String loginUser(LoginUser loginUser) {
        // 生成登录随机Code
        loginUser.setCode(UUID.randomUUID().toString());
        String id = loginUser.getToken();
        cacheHandler.put(getCacheKey(id), loginUser, tokenConfig.getExpireTime(), TimeUnit.MILLISECONDS);
        return Jwts.builder().setSubject(id).signWith(SignatureAlgorithm.HS256, tokenConfig.getSecret()).compact();
    }

    @Override
    public boolean logout(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return false;
        }
        return cacheHandler.remove(getCacheKey(claims.getSubject()));
    }

    @Override
    public LoginUser getUserByToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        return cacheHandler.get(getCacheKey(claims.getSubject()));
    }

    @Override
    public void refreshUser(LoginUser loginUser) {
        String id = loginUser.getToken();
        cacheHandler.expire(getCacheKey(id), tokenConfig.getExpireTime(), TimeUnit.MILLISECONDS);
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    @Override
    public Claims parseToken(String token) {
        Claims body;
        try {
            body = Jwts.parser()
                    .setSigningKey(tokenConfig.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
        return body;
    }

    @Override
    public String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader(tokenConfig.getHeader());
    }

    private String getCacheKey(String id) {
        return tokenConfig.getDomain() + id;
    }
}
