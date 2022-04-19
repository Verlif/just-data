package idea.verlif.justdata.security.token;

import idea.verlif.justdata.cache.CacheHandler;
import idea.verlif.justdata.security.token.impl.DefaultTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Token配置
 *
 * @author Verlif
 * @version 1.0
 * @date 2021/11/9 12:58
 */
@Configuration
@ConfigurationProperties(prefix = "just-data.token")
public class TokenConfig {

    /**
     * token存储名称
     */
    public static final String TOKEN_NAME = "justdata:token:";

    /**
     * Token在请求中header的属性名
     */
    private String header = "Authorization";

    /**
     * TokenKey命名头
     */
    private String domain = "justdata:token:";

    /**
     * Token密钥
     */
    private String secret = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Token存活时间（单位：毫秒）
     */
    private Long expireTime = 3600000L;

    /**
     * 记住登录时间（单位：毫秒）
     */
    private Long remember = 14515200000L;

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime * 60000;
    }

    public void setRemember(Long remember) {
        this.remember = remember * 86400000;
    }

    @Bean
    @ConditionalOnMissingBean(TokenService.class)
    public TokenService tokenService(
            @Autowired CacheHandler cacheHandler) {
        return new DefaultTokenService(cacheHandler, this);
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public Long getRemember() {
        return remember;
    }
}
