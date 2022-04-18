package idea.verlif.justdata.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.justdata.item.Item;
import idea.verlif.justdata.route.RouteManager;
import idea.verlif.justdata.route.Router;
import idea.verlif.justdata.sql.SqlExecutor;
import idea.verlif.justdata.util.MessagesUtils;
import idea.verlif.justdata.util.RequestUtils;
import idea.verlif.justdata.util.ResultSetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/18 14:15
 */
@RestController
@RequestMapping("/special")
public class UserController {

    @Autowired
    private RouteManager routeManager;

    @Autowired
    private SqlExecutor sqlExecutor;

    @PostMapping("/login")
    public BaseResult<String> login(@RequestBody BaseUser user, HttpServletRequest request) throws SQLException, JsonProcessingException {
        Router router = routeManager.getRouter("special");
        if (router != null) {
            Item item = router.post("login");
            if (item != null) {
                Map<String, Object> map = RequestUtils.getMapFromRequest(request);
                String body = RequestUtils.getBodyFromRequest(request);
                ResultSet set = sqlExecutor.exec(item, map, body);
                String key = ResultSetUtils.toString(set);
                if (key == null) {
                    return new FailResult<>(MessagesUtils.message("no.user"));
                }
            }
        }
        return new FailResult<>(MessagesUtils.message("closed.login"));
    }
}
