package idea.verlif.justdata.special.login;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/18 14:16
 */
@Schema(name = "用户基础信息", description = "这里的用户信息需要提供一个用户唯一标识与一个对应的验证密钥")
public class BaseUser {

    @Schema(name = "用户唯一标识", description = "例如用户ID、account等的都可以，主要由使用者的业务有关")
    private Object id;

    @Schema(name = "用户验证密钥", description = "例如密码、登陆码等")
    private String key;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + ((id instanceof Number) ? id : "\"" + id + "\"") +
                ", \"key\":\"" + key + "\"" +
                '}';
    }
}
