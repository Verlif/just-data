package idea.verlif.justdata.controller;

import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.justdata.base.result.ext.OkResult;
import idea.verlif.justdata.route.RouteManager;
import idea.verlif.justdata.route.Router;
import idea.verlif.justdata.util.MessagesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author Verlif
 */
@RequestMapping("/router")
@RestController
public class RouterController {

    @Autowired
    private RouteManager routeManager;

    @GetMapping("/label/list")
    public BaseResult<Set<String>> labelList() {
        return new OkResult<>(routeManager.labelSet());
    }

    @GetMapping("/label/{label}/api")
    public BaseResult<Router.RouterInfo> apiList(@PathVariable String label) {
        Router router = routeManager.getRouter(label);
        if (router == null) {
            return new FailResult<>(MessagesUtils.message("no.such.label"));
        }
        return new OkResult<>(router.getInfo());
    }
}
