package idea.verlif.justdata.router;

import idea.verlif.justdata.item.Item;
import idea.verlif.justdata.item.ItemParser;
import idea.verlif.justdata.item.ItemParserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/8 11:35
 */
@Component
public class RouterManager implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterManager.class);

    @Autowired
    private RouterConfig routerConfig;

    @Autowired
    private ItemParserManager parserManager;

    private final Map<String, Router> routerMap;

    public RouterManager() {
        routerMap = new HashMap<>();
    }

    @Override
    public void run(ApplicationArguments args) {
        Map<String, ItemParser> parserMap = parserManager.getParserMap();
        for (ItemParser parser : parserMap.values()) {
            List<Item> list = parser.getItemList();
            String label = parser.getLabel();
            if (routerConfig.isAllowLabel(label)) {
                Router router = getRouter(label);
                if (router == null) {
                    router = new Router(label);
                    addRouter(router);
                }
                for (Item item : list) {
                    if (routerConfig.isAllowApi(item.getApi())) {
                        router.addItem(item);
                    }
                }
            }
        }
    }

    public void reloadRouter() {
        parserManager.reloadParser();
        routerMap.clear();
        run(null);
    }

    public void addRouter(Router router) {
        routerMap.put(router.getLabel(), router);
    }

    public Router getRouter(String label) {
        return routerMap.get(label);
    }

    public Set<String> labelSet() {
        return routerMap.keySet();
    }
}
