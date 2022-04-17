package idea.verlif.justdata.exception;

import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.justdata.util.MessagesUtils;
import idea.verlif.spring.exception.ExceptionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.UUID;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/17 10:21
 */
@Configuration
@ConfigurationProperties(prefix = "just-data.exception")
public class ExceptionLogManager implements ExceptionHolder<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionLogManager.class);

    private File file = new File("exception.log");

    public synchronized String logThrowable(Throwable throwable) {
        String code = genExceptionCode(throwable);
        try (FileOutputStream fos = new FileOutputStream(file, true);
             PrintWriter writer = new PrintWriter(fos)) {
            writer.append(code).append(":\n");
            writer.flush();
            throwable.printStackTrace(writer);
            return code;
        } catch (IOException e) {
            e.printStackTrace();
            return "-1";
        }
    }

    private String genExceptionCode(Throwable throwable) {
        return throwable.getClass().getSimpleName() + "-" + UUID.randomUUID();
    }

    public String getFile() {
        return file.getPath();
    }

    public void setFile(String file) throws IOException {
        this.file = new File(file);
        if (!this.file.exists()) {
            File parent = this.file.getParentFile();
            if (!parent.exists()) {
                if (!parent.mkdirs()) {
                    throw new FileNotFoundException(parent.getPath() + " can not be created.");
                }
            }
            if (!this.file.createNewFile()) {
                throw new FileNotFoundException(this.file.getPath() + " can not be created.");
            } else {
                LOGGER.debug(this.file.getPath() + " is created.");
            }
        }
    }

    @Override
    public Class<? extends Throwable> register() {
        return Throwable.class;
    }

    @Override
    public Object handler(Throwable e) {
        String code = logThrowable(e);
        return new FailResult<String>(MessagesUtils.message("exception.print")).data(code);
    }
}
