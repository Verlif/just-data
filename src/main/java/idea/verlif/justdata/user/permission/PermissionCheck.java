package idea.verlif.justdata.user.permission;

import idea.verlif.justdata.user.UserService;
import idea.verlif.justdata.user.login.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/19 14:30
 */
@Component
public class PermissionCheck {

    @Autowired
    private PermissionConfig permissionConfig;

    public boolean hasPermission(String targetPermission) {
        if (targetPermission == null || targetPermission.length() == 0 || !permissionConfig.isEnabled()) {
            return true;
        }
        LoginUser loginUser = UserService.getLoginUser();
        return loginUser.hasPermission(targetPermission);
    }

    public boolean hasInnerPermission() {
        return hasPermission(permissionConfig.getInnerPermission());
    }
}
