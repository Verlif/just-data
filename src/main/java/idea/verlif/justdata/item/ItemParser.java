package idea.verlif.justdata.item;

import idea.verlif.justdata.datasource.DataSourceItem;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private String label;

    private final File file;

    public ItemParser(File file) {
        this.file = file;
    }

    public List<DataSourceItem> getDataSourceItemList() {
        List<DataSourceItem> list = new ArrayList<>();
        Document document = load(file);
        if (document == null) {
            return list;
        }
        // 获取根节点
        Element root = document.getRootElement();
        Element dbs = root.element("dbs");
        if (dbs == null) {
            return list;
        }
        List dbList = dbs.elements("db");
        if (dbList == null) {
            return list;
        }
        for (Object o : dbList) {
            if (o instanceof Element) {
                Element db = (Element) o;
                DataSourceItem item = new DataSourceItem();
                String label = db.elementText("label");
                if (label == null) {
                    continue;
                }
                item.setLabel(label);
                item.setUrl(db.elementText("url"));
                item.setUsername(db.elementText("username"));
                item.setPassword(db.elementText("password"));
                item.setDriver(db.elementText("driver"));
                list.add(item);
            }
        }
        return list;
    }

    public List<Item> getItemList() {
        List<Item> list = new ArrayList<>();
        Document document = load(file);
        if (document != null) {
            // 获取根节点
            Element root = document.getRootElement();
            Element items = root.element("items");
            if (items == null) {
                return list;
            }
            List itemList = items.elements("item");
            if (itemList == null) {
                return list;
            }
            // 加载标签信息
            label = items.attributeValue("label");
            if (label == null) {
                return list;
            }
            // 加载操作项
            for (Object obj : itemList) {
                if (obj instanceof Element) {
                    Element element = (Element) obj;
                    Item item = new Item();
                    item.setLabel(label);
                    // 获取操作项名称
                    String itemName = element.attributeValue("name");
                    if (itemName == null) {
                        item.setName(label + "-" + itemList.size());
                    } else {
                        item.setName(itemName);
                    }
                    // 获取API
                    String api = element.elementText("api");
                    if (api == null) {
                        api = item.getName();
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
                    // 获取访问权限
                    String permissionStr = element.elementText("permission");
                    if (permissionStr != null) {
                        item.setPermission(permissionStr);
                    }
                    // 将操作项添加到列表
                    list.add(item);
                }
            }
        }
        return list;
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

}
