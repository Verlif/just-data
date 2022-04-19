package idea.verlif.justdata.log.api;

import idea.verlif.justdata.base.constant.MethodConstant;
import idea.verlif.justdata.log.ApiMethodLogHandler;
import org.springframework.stereotype.Component;

/**
 * @author Verlif
 */
@Component
public class ApiDeleteLogHandler extends ApiMethodLogHandler {

    @Override
    protected String method() {
        return MethodConstant.DELETE;
    }

}
