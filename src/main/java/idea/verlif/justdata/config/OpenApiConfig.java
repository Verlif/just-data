package idea.verlif.justdata.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Verlif
 * @version 1.0
 * @date 2021/11/9 9:44
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springShopOpenApi() {
        return new OpenAPI()
                .info(new Info().title("JustData")
                        .description("后台接口自动生成框架")
                        .version("v1.0.0")
                        .contact(new Contact().name("Verlif").email("920767796@qq.com"))
                        .license(new License().name("Apache License 2.0").url("https://github.com/Verlif/JustData")));
    }
}
