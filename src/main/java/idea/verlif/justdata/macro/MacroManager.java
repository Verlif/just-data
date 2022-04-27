package idea.verlif.justdata.macro;

import idea.verlif.justdata.macro.handler.UserIdMacro;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/19 11:02
 */
@Component
public class MacroManager {

    private final Map<String, MarcoHandler> macroMap;

    public MacroManager() {
        macroMap = new ConcurrentHashMap<>();

        init();
    }

    private void init() {
        addMacro(new UserIdMacro());
    }

    public void addMacro(MarcoHandler handler) {
        macroMap.put(handler.getKey(), handler);
    }

    public String get(String key) {
        MarcoHandler handler = macroMap.get(key);
        if (key == null || handler == null) {
            return null;
        }
        return handler.getValue();
    }

}
