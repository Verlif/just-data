package idea.verlif.justdata.sql.exception;

import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.spring.exception.ExceptionHolder;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * @author Verlif
 */
@Component
public class SQLExceptionHandler implements ExceptionHolder<SQLException> {

    @Override
    public Class<? extends SQLException> register() {
        return SQLException.class;
    }

    @Override
    public Object handler(SQLException e) {
        return new FailResult<>(e.getMessage());
    }
}
