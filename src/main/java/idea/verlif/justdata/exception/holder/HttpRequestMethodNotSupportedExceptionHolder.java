package idea.verlif.justdata.exception.holder;

import idea.verlif.justdata.base.result.ResultCode;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.justdata.util.ServletUtils;
import idea.verlif.spring.exception.ExceptionHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/19 10:12
 */
@Component
public class HttpRequestMethodNotSupportedExceptionHolder implements ExceptionHolder<HttpRequestMethodNotSupportedException> {

    @Override
    public Class<? extends HttpRequestMethodNotSupportedException> register() {
        return HttpRequestMethodNotSupportedException.class;
    }

    @Override
    public Object handler(HttpRequestMethodNotSupportedException e) {
        return new FailResult<>(ResultCode.FAILURE_NO_API_METHOD);
    }
}
