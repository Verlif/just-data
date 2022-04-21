package idea.verlif.justdata.file.exception;

import idea.verlif.justdata.base.result.ResultCode;
import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.spring.exception.ExceptionHolder;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/21 11:16
 */
@Component
public class FileNotFoundExceptionHolder implements ExceptionHolder<FileNotFoundException> {
    @Override
    public Class<? extends FileNotFoundException> register() {
        return FileNotFoundException.class;
    }

    @Override
    public Object handler(FileNotFoundException e) {
        return new FailResult<>(ResultCode.FAILURE_FILE_MISSING);
    }
}
