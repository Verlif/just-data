package idea.verlif.justdata.log.impl;

import idea.verlif.justdata.log.ApiLogInfo;
import idea.verlif.justdata.log.ApiLogInfoHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Verlif
 */
@Component
public class ApiLogInfoHandlerImpl implements ApiLogInfoHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiLogInfoHandlerImpl.class);

    @Override
    public void handle(ApiLogInfo logInfo) {
        LOGGER.debug(logInfo.toString());
    }
}
