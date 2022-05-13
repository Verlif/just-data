package idea.verlif.justdata.sql.parser.ext;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.util.ArrayIterator;
import idea.verlif.justdata.sql.exception.LackOfSqlParamException;
import idea.verlif.justdata.sql.parser.SqlPoint;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Foreach方法
 *
 * @author Verlif
 */
public class ForeachPoint extends SqlPoint {

    public ForeachPoint() {
    }

    public ForeachPoint(String sql, Map<String, Object> params) {
        super(sql, params);
    }

    @Override
    protected String startTag() {
        return "foreach";
    }

    @Override
    protected String endTag() {
        return "foreach";
    }

    @Override
    public int order() {
        return 11;
    }

    @Override
    protected String parser(String content, Map<String, Object> params, Map<String, String> attrs) throws Exception {
        String left = attrs.get("open");
        String right = attrs.get("close");
        String separator = attrs.get("separator");
        String index = attrs.get("index");
        String itemName = attrs.get("item");
        Object items = parserObj(attrs.get("collection"), params);
        if (items == null) {
            throw new LackOfSqlParamException(attrs.get("collection"));
        }
        Iterator<?> iterator;
        if (items instanceof ArrayNode) {
            ArrayNode arrayNode = (ArrayNode) items;
            iterator = arrayNode.iterator();
        } else if (items instanceof Collection) {
            iterator = ((Collection<?>) items).iterator();
        } else {
            Object[] objs = new Object[1];
            objs[0] = items;
            iterator = new ArrayIterator<>(objs);
        }
        StringBuilder sb = new StringBuilder();
        if (left != null) {
            sb.append(left);
        }
        int i = 0;
        while (iterator.hasNext()) {
            i++;
            Object o = iterator.next();
            boolean b = false;
            if (itemName != null) {
                sb.append(content.replace("#{" + itemName + "}", o.toString()));
                b = true;
            }
            if (index != null) {
                sb.append(content.replace("#{" + index + "}", String.valueOf(i++)));
                b = true;
            }
            if (b && separator != null) {
                sb.append(separator);
            }
            sb.append(" ");
        }
        if (i > 0) {
            if (separator == null) {
                sb.setLength(sb.length() - 1);
            } else {
                sb.setLength(sb.length() - separator.length() - 1);
            }
        }
        if (right != null) {
            sb.append(right);
        }
        return sb.toString();
    }
}
