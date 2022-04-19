package idea.verlif.justdata.user.login.exception;

import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ResultCode;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.spring.exception.ExceptionHolder;
import org.springframework.stereotype.Component;

/**
 * @author Verlif
 * @version 1.0
 * @date 2021/12/14 16:10
 */
@Component
public class CustomExceptionHolder implements ExceptionHolder<CustomException> {

    @Override
    public Class<CustomException> register() {
        return CustomException.class;
    }

    @Override
    public BaseResult<?> handler(CustomException e) {
        return new FailResult<>(ResultCode.FAILURE_NOT_LOGIN);
    }
}
