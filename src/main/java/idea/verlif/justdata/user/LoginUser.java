package idea.verlif.justdata.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 登录的用户
 *
 * @author Verlif
 * @version 1.0
 * @date 2022/4/18 14:12
 */
public class LoginUser {

    private final Object id;

    private final Set<String> permissions;

    public LoginUser(Object id) {
        this.id = id;
        this.permissions = new HashSet<>();
    }

    /**
     * 添加访问权限列表
     *
     * @param collection 访问权限列表
     */
    public void addPermission(Collection<String> collection) {
        permissions.addAll(collection);
    }

    public Object getId() {
        return id;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
}
