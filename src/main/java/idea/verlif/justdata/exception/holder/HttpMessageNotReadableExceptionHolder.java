package idea.verlif.justdata.exception.holder;

import idea.verlif.justdata.base.result.ResultCode;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.spring.exception.ExceptionHolder;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;

/**
 * @author Verlif
 */
@Component
public class HttpMessageNotReadableExceptionHolder implements ExceptionHolder<HttpMessageNotReadableException> {
    @Override
    public Class<? extends HttpMessageNotReadableException> register() {
        return HttpMessageNotReadableException.class;
    }

    @Override
    public Object handler(HttpMessageNotReadableException e) {
        return new FailResult<>(ResultCode.FAILURE_PARAMETER_LACK);
    }
}
