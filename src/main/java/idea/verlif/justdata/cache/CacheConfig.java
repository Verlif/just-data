package idea.verlif.justdata.cache;

import idea.verlif.justdata.cache.mem.MemCache;
import idea.verlif.spring.taskservice.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Verlif
 * @version 1.0
 * @date 2021/12/17 16:02
 */
@Configuration
public class CacheConfig {

    @Bean
    @ConditionalOnMissingBean(CacheHandler.class)
    public CacheHandler cacheHandler(@Autowired TaskService taskService) {
        return new MemCache(taskService);
    }
}
