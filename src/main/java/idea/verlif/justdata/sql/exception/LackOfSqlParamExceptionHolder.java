package idea.verlif.justdata.sql.exception;

import idea.verlif.justdata.base.result.ResultCode;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.justdata.util.MessagesUtils;
import idea.verlif.spring.exception.ExceptionHolder;
import org.springframework.stereotype.Component;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/20 10:18
 */
@Component
public class LackOfSqlParamExceptionHolder implements ExceptionHolder<LackOfSqlParamException> {
    @Override
    public Class<? extends LackOfSqlParamException> register() {
        return LackOfSqlParamException.class;
    }

    @Override
    public Object handler(LackOfSqlParamException e) {
        return new FailResult<>(ResultCode.FAILURE_PARAMETER_LACK).withParam(e.getParam());
    }
}
