package idea.verlif.justdata;

import idea.verlif.spring.exception.EnableExceptionCapture;
import idea.verlif.spring.logging.EnableLogService;
import idea.verlif.spring.taskservice.EnableTaskService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/8 9:11
 */
@EnableTaskService
@EnableLogService
@EnableExceptionCapture
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class JustDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(JustDataApplication.class, args);
    }
}
