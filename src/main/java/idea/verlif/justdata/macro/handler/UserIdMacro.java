package idea.verlif.justdata.macro.handler;

import idea.verlif.justdata.macro.MarcoHandler;
import idea.verlif.justdata.special.user.UserService;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/24 16:46
 */
public class UserIdMacro implements MarcoHandler {

    @Override
    public String getKey() {
        return "userId";
    }

    @Override
    public String getValue() {
        if (UserService.isOnline()) {
            return UserService.getLoginUser().getId().toString();
        } else {
            return "";
        }
    }
}
