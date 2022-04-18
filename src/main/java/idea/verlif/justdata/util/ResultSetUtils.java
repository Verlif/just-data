package idea.verlif.justdata.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/14 17:12
 */
public class ResultSetUtils {

    public static Map<String, Object> toMap(ResultSet set) throws SQLException {
        ResultSetMetaData metaData = set.getMetaData();
        int size = metaData.getColumnCount();
        Map<String, Object> map = new HashMap<>(size);
        if (set.next()) {
            for (int i = 0; i < size; i++) {
                map.put(metaData.getColumnName(i), set.getObject(i));
            }
        }
        return map;
    }

    public static List<Map<String, Object>> toMapList(ResultSet set) throws SQLException {
        ResultSetMetaData metaData = set.getMetaData();
        int size = metaData.getColumnCount();
        List<Map<String, Object>> list = new ArrayList<>();
        while (set.next()) {
            Map<String, Object> map = new HashMap<>(size);
            for (int i = 1; i <= size; i++) {
                map.put(metaData.getColumnName(i), set.getObject(i));
            }
            list.add(map);
        }
        return list;
    }

    public static List<String> toStringList(ResultSet set) throws SQLException {
        List<String> list = new ArrayList<>();
        while (set.next()) {
            list.add(set.getString(1));
        }
        return list;
    }

    public static String toString(ResultSet set) throws SQLException {
        if (set.next()) {
            return set.getString(1);
        } else {
            return null;
        }
    }
}
