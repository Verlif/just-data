package idea.verlif.justdata.macro;

import idea.verlif.justdata.user.LoginUser;
import idea.verlif.justdata.user.UserService;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/19 11:02
 */
@Component
public class GlobalMacroManager {

    private final Map<String, MarcoHandler> macroMap;

    public GlobalMacroManager() {
        macroMap = new ConcurrentHashMap<>();

        init();
    }

    private void init() {
        macroMap.put("userId", () -> UserService.getLoginUser().getId().toString());
    }

    public String get(String key) {
        MarcoHandler handler = macroMap.get(key);
        if (key == null) {
            return null;
        }
        return handler.get();
    }

    private interface MarcoHandler {

        String get();
    }
}
