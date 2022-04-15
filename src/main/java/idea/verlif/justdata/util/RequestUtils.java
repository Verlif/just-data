package idea.verlif.justdata.util;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Verlif
 */
public class RequestUtils {

    public static Map<String, Object> getMapFromRequest(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Enumeration<String> keys = request.getParameterNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            map.put(key, request.getParameter(key));
        }
        return map;
    }

    public static String getBodyFromRequest(HttpServletRequest request) {
        String temp;
        StringBuilder sb = new StringBuilder();
        try (InputStream inputStream = request.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            temp = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            temp = "";
        }
        return temp;
    }
}
