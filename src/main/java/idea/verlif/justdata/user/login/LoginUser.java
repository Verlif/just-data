package idea.verlif.justdata.user.login;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 登录的用户
 *
 * @author Verlif
 * @version 1.0
 * @date 2022/4/18 14:12
 */
public class LoginUser implements Serializable {

    /**
     * 登录Code
     */
    protected String code = "*";

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 用户ID
     */
    private Object id;

    /**
     * 用户权限
     */
    private final Set<String> permissions;

    public LoginUser() {
        this.permissions = new HashSet<>();
    }

    public LoginUser(Object id) {
        this.id = id;
        this.permissions = new HashSet<>();
    }

    /**
     * 用户登录Token
     */
    @JsonIgnore
    public String getToken() {
        return id + ":" + code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * 添加访问权限列表
     *
     * @param collection 访问权限列表
     */
    public void addPermission(Collection<String> collection) {
        permissions.addAll(collection);
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getId() {
        return id;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
}
