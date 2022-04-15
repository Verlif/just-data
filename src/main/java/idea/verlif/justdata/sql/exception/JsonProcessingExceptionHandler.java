package idea.verlif.justdata.sql.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.spring.exception.ExceptionHolder;
import org.springframework.stereotype.Component;

/**
 * @author Verlif
 */
@Component
public class JsonProcessingExceptionHandler implements ExceptionHolder<JsonProcessingException> {

    @Override
    public Class<? extends JsonProcessingException> register() {
        return JsonProcessingException.class;
    }

    @Override
    public Object handler(JsonProcessingException e) {
        return new FailResult<>(e.getMessage());
    }
}
