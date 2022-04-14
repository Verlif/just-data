package idea.verlif.justdata.route;

import idea.verlif.justdata.item.Item;
import idea.verlif.justdata.item.ItemParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
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
public class RouteManager implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteManager.class);

    @Autowired
    private RouteConfig routeConfig;

    private final Map<String, Router> routerMap;

    public RouteManager() {
        routerMap = new HashMap<>();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        File dir = new File(routeConfig.getPath());
        File[] files = dir.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".xml"));
        if (files != null) {
            LOGGER.debug("Found xml file " + files.length);
            for (File file : files) {
                LOGGER.debug("Loading xml - " + file.getName());
                ItemParser parser = new ItemParser(file);
                List<Item> list = parser.parser();
                String label = parser.getLabel();
                if (routeConfig.isAllowLabel(label)) {
                    Router router = getRouter(label);
                    if (router == null) {
                        router = new Router(label);
                        addRouter(router);
                    }
                    for (Item item : list) {
                        if (routeConfig.isAllowApi(item.getApi())) {
                            router.addItem(item);
                        }
                    }
                }
            }
        }
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
