package idea.verlif.justdata.log.api;

import idea.verlif.justdata.constant.MethodConstant;
import idea.verlif.justdata.log.ApiMethodLogHandler;
import org.springframework.stereotype.Component;

/**
 * @author Verlif
 */
@Component
public class ApiPostLogHandler extends ApiMethodLogHandler {

    @Override
    protected String method() {
        return MethodConstant.POST;
    }

}