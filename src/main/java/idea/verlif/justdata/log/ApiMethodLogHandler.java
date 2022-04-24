package idea.verlif.justdata.log;

import idea.verlif.justdata.api.ApiController;
import idea.verlif.spring.logging.api.ApiLogHandler;
import idea.verlif.spring.logging.api.LogIt;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;

/**
 * 与{@link ApiController ItemController}强绑定的日志处理器。<br/>
 * 当ItemController方法改变时，此处理类可能会失效。
 *
 * @author Verlif
 */
public abstract class ApiMethodLogHandler implements ApiLogHandler {

    public ApiMethodLogHandler() {
    }

    @Autowired
    private ApiLogInfoHandler infoHandler;

    @Override
    public void onLog(Method method, LogIt logIt, long time) {
        ApiLogInfo logInfo = new ApiLogInfo();
        Parameter[] parameters = method.getParameters();
        logInfo.setMethod(method());
        logInfo.setLabel(parameters[0].toString());
        logInfo.setApi(parameters[1].toString());
        logInfo.setTime(new Date(time));
        infoHandler.handle(logInfo);
    }

    /**
     * 获取api的访问方法
     *
     * @return 访问方法
     */
    protected abstract String method();

    @Override
    public void onReturn(Method method, LogIt logIt, Object o, long time) {
    }
}
