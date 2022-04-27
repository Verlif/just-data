package idea.verlif.justdata.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

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

    public static Document load(String xmlStr) {
        try {
            return DocumentHelper.parseText(xmlStr);
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}
