package idea.verlif.justdata.router;

import idea.verlif.justdata.item.Item;
import idea.verlif.justdata.item.ItemConfig;
import idea.verlif.justdata.item.ItemParser;
import idea.verlif.justdata.item.ItemParserManager;
import idea.verlif.justdata.sql.SqlExecutor;
import idea.verlif.spring.taskservice.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
public class RouterManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterManager.class);

    @Autowired
    private RouterConfig routerConfig;

    @Autowired
    private ItemParserManager parserManager;

    @Autowired
    private SqlExecutor sqlExecutor;

    @Autowired
    private TaskService taskService;

    private final Map<String, Router> routerMap;

    public RouterManager() {
        routerMap = new HashMap<>();
    }

    @PostConstruct
    public void autoReloadRouter() {
        ItemConfig config = parserManager.getItemConfig();
        if (config.isAutoReload() && config.getPath() != null) {
            taskService.getSchedule().scheduleAtFixedRate(new ReloadRouterRunner(), config.getPeriod());
        } else {
            loadRouter();
        }
    }

    public void loadRouter() {
        parserManager.reloadParser();
        routerMap.clear();
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
                        // SQL预处理
                        sqlExecutor.preExecutingItem(item);
                        router.addItem(item);
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

    private final class ReloadRouterRunner implements Runnable {

        private final File file;
        private long last;

        public ReloadRouterRunner() {
            ItemConfig config = parserManager.getItemConfig();
            this.file = new File(config.getPath());
            this.last = 0;
        }

        @Override
        public void run() {
            long nowTime = file.lastModified();
            if (last != nowTime) {
                last = nowTime;
                loadRouter();
            }
        }
    }
}
