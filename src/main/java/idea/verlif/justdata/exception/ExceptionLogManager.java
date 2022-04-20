package idea.verlif.justdata.exception;

import idea.verlif.justdata.base.result.ext.FailResult;
import idea.verlif.spring.exception.ExceptionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.Locale;
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

    private static final String TYPE_SPLIT = ",";

    private int output = 0;

    private File file = new File("exception.log");

    public synchronized String logThrowable(Throwable throwable) {
        if ((output & OutputType.ON_CONSOLE) > 0) {
            throwable.printStackTrace();
        }
        if ((output & OutputType.ON_FILE) > 0) {
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
        return null;
    }

    private String genExceptionCode(Throwable throwable) {
        return throwable.getClass().getSimpleName() + "-" + UUID.randomUUID();
    }

    public int getOutput() {
        return output;
    }

    public void addType(int type) {
        this.output = type;
    }

    public void addType(String type) {
        switch (type.trim().toUpperCase(Locale.ROOT)) {
            case "CLIENT":
                this.output = this.output | OutputType.ON_CLIENT;
                break;
            case "FILE":
                this.output = this.output | OutputType.ON_FILE;
                break;
            default:
                this.output = this.output | OutputType.ON_CONSOLE;
        }
    }

    public void setOutput(String output) {
        if (output == null || output.length() == 0) {
            this.output = OutputType.ON_CONSOLE;
        } else {
            String[] ss = output.split(TYPE_SPLIT);
            for (String s : ss) {
                addType(s);
            }
        }
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
        boolean onClient = (output & OutputType.ON_CLIENT) > 0;
        FailResult<String> result = onClient ? new FailResult<>(e.getMessage()) : new FailResult<>();
        if (code == null) {
            return result;
        } else {
            return result.data(code);
        }
    }

    private interface OutputType {
        int ON_CONSOLE = 1;
        int ON_FILE = 1 << 1;
        int ON_CLIENT = 1 << 2;
    }
}
