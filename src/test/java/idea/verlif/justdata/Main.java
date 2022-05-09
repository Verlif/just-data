package idea.verlif.justdata;

import idea.verlif.justdata.util.RsaUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/8 10:27
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Map<String, String> attrMap = getAttrMap(" open=\"(\" separator=\",\" close=\")\" item=\"userId\" collection=\"userIds\"");
        for (String key : attrMap.keySet()) {
            System.out.println(key + " - " + attrMap.get(key));
        }
    }
    public static Map<String, String> getAttrMap(String attrStr) {
        Map<String, String> attrMap = new HashMap<>();
        char[] chars = attrStr.toCharArray();
        boolean isKey = true, in = false;
        String key = null;
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (isKey) {
                if (c == '=') {
                    isKey = false;
                    key = sb.toString();
                    sb.setLength(0);
                } else if (c != ' ') {
                    sb.append(c);
                }
            } else {
                if (in) {
                    if (c == '\"') {
                        in = false;
                        attrMap.put(key, sb.toString());
                        sb.setLength(0);
                        isKey = true;
                    } else {
                        sb.append(c);
                    }
                } else {
                    if (c == '\"') {
                        in = true;
                    }
                }
            }
        }
        return attrMap;
    }
}
