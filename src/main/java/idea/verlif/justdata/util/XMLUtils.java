package idea.verlif.justdata.util;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.File;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/22 9:33
 */
public class XMLUtils {

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
}
