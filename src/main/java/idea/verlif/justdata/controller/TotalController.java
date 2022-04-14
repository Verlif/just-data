package idea.verlif.justdata.controller;

import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.justdata.base.result.ext.OkResult;
import idea.verlif.justdata.item.Item;
import idea.verlif.justdata.route.RouteManager;
import idea.verlif.justdata.sql.SqlExecutor;
import idea.verlif.justdata.util.MessagesUtils;
import idea.verlif.justdata.util.ResultSetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/8 11:33
 */
@RestController
@RequestMapping("/api/db")
public class TotalController {

    @Autowired
    private RouteManager routeManager;

    @Autowired
    private SqlExecutor sqlExecutor;

    @GetMapping("/{api}")
    public BaseResult<List<Map<String, Object>>> get(@PathVariable String api, HttpServletRequest request) throws SQLException {
        Item item = routeManager.get(api);
        if (item == null) {
            return new FailResult<>(MessagesUtils.message("no.such.api"));
        }
        Map<String, Object> map = getMapFromRequest(request);
        ResultSet set = sqlExecutor.exec(item, map);
        return new OkResult<>(ResultSetUtils.toList(set));
    }

    private Map<String, Object> getMapFromRequest(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Enumeration<String> keys = request.getParameterNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            map.put(key, request.getParameter(key));
            System.out.println("key: " + key + ", value" + request.getParameter(key));
        }
        return map;
    }
}
