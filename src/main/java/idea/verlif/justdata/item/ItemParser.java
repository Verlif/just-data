package idea.verlif.justdata.item;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.xml.DomUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据项解析器
 *
 * @author Verlif
 * @version 1.0
 * @date 2022/4/14 10:28
 */
public class ItemParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemParser.class);

    private String dbname;

    private String label;

    private final File file;

    public ItemParser(File file) {
        this.file = file;
    }

    public List<Item> parser() {
        List<Item> items = new ArrayList<>();
        Document document = load(file);
        if (document != null) {
            // 获取根节点
            Element root = document.getRootElement();
            // 加载数据库信息
            dbname = root.attributeValue("name");
            if (dbname == null) {
                return items;
            }
            label = root.attributeValue("label");
            if (label == null) {
                label = dbname;
            }
            // 加载操作项
            for (Object obj : root.elements()) {
                if (obj instanceof Element) {
                    Element element = (Element) obj;
                    Item item = new Item();
                    item.setDbname(dbname);
                    // 获取操作项名称
                    String itemName = element.attributeValue("name");
                    if (itemName == null) {
                        item.setName(label + "-" + items.size());
                    } else {
                        item.setName(itemName);
                    }
                    // 获取API
                    String api = element.elementText("api");
                    if (api == null) {
                        api = itemName;
                    }
                    item.setApi(api);
                    // 获取Method
                    String method = element.elementText("method");
                    if (method == null) {
                        method = "POST";
                    }
                    item.setMethod(method);
                    // 获取API
                    String sql = element.elementText("sql");
                    if (sql == null) {
                        LOGGER.error("No sql in " + item.getName());
                        continue;
                    }
                    item.setSql(sql);
                    // 将操作项添加到列表
                    items.add(item);
                }
            }
        }
        return items;
    }

    public static Document load(File file) {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return document;
    }

    public String getLabel() {
        return label;
    }

    public String getDbname() {
        return dbname;
    }
}
