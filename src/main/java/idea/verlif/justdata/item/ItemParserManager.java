package idea.verlif.justdata.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
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
            LOGGER.debug("Found xml file " + files.length + " : " + Arrays.toString(Arrays.stream(files).map(File::getName).toArray()));
            for (File file : files) {
                ItemParser parser = new ItemParser(file);
                parserMap.put(file.getPath(), parser);
            }
        } else if (dir.isFile()) {
            ItemParser parser = new ItemParser(dir);
            parserMap.put(dir.getPath(), parser);
            LOGGER.debug("Found xml file 1 : [" + dir.getPath() + "]");
        }
    }

    public void reloadParser() {
        parserMap.clear();
        loadParser(itemConfig.getPath());
    }

    public Map<String, ItemParser> getParserMap() {
        return parserMap;
    }
}
