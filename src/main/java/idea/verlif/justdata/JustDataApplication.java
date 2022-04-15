package idea.verlif.justdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/8 9:11
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class JustDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(JustDataApplication.class, args);
    }
}
