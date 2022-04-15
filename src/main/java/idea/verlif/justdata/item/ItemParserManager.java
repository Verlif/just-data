package idea.verlif.justdata.item;

import idea.verlif.justdata.route.RouteManager;
import idea.verlif.justdata.route.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/15 15:47
 */
@Component
public class ItemParserManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemParserManager.class);

    private final ItemConfig itemConfig;
    private final Map<String, ItemParser> parserMap;

    public ItemParserManager(@Autowired ItemConfig itemConfig) {
        this.parserMap = new HashMap<>();

        this.itemConfig = itemConfig;
        loadParser(itemConfig.getPath());
    }

    public void loadParser(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".xml"));
        if (files != null) {
            LOGGER.debug("Found xml file " + files.length);
            for (File file : files) {
                LOGGER.debug("Loading xml - " + file.getName());
                ItemParser parser = new ItemParser(file);
                parserMap.put(file.getPath(), parser);
            }
        }
    }

    public Map<String, ItemParser> getParserMap() {
        return parserMap;
    }
}