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

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/8 11:35
 */
@Component
public class RouteManager implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteManager.class);

    private final HashMap<String, Item> getMap;
    private final HashMap<String, Item> postMap;
    private final HashMap<String, Item> deleteMap;
    private final HashMap<String, Item> putMap;

    @Autowired
    private RouteConfig routeConfig;

    public RouteManager() {
        this.getMap = new HashMap<>();
        this.postMap = new HashMap<>();
        this.deleteMap = new HashMap<>();
        this.putMap = new HashMap<>();
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
                for (Item item : list) {
                    switch (item.getMethod()) {
                        case "GET":
                            getMap.put(item.getApi(), item);
                            break;
                        case "PUT":
                            putMap.put(item.getApi(), item);
                            break;
                        case "DELETE":
                            deleteMap.put(item.getApi(), item);
                            break;
                        default:
                            postMap.put(item.getApi(), item);
                    }
                }
            }
        }
        LOGGER.debug("Api-   get\tload\t" + getMap.size());
        LOGGER.debug("Api-  post\tload\t" + postMap.size());
        LOGGER.debug("Api-   put\tload\t" + putMap.size());
        LOGGER.debug("Api-delete\tload\t" + deleteMap.size());
    }

    public Item get(String api) {
        return getMap.get(api);
    }

    public Item post(String api) {
        return postMap.get(api);
    }

    public Item put(String api) {
        return putMap.get(api);
    }

    public Item delete(String api) {
        return deleteMap.get(api);
    }
}
