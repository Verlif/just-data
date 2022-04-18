package idea.verlif.justdata.cache.mem;

import idea.verlif.spring.taskservice.anno.TaskTip;
import idea.verlif.spring.taskservice.TaskType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import java.util.concurrent.TimeUnit;

/**
 * @author Verlif
 * @version 1.0
 * @date 2021/12/31 15:28
 */
@ConditionalOnBean(MemCache.class)
@TaskTip(type = TaskType.REPEAT_DELAY, delay = 5, interval = 3, unit = TimeUnit.SECONDS)
public class MemCacheClearer implements Runnable {

    private final MemCache memCache;

    public MemCacheClearer(MemCache memCache) {
        this.memCache = memCache;
    }

    @Override
    public void run() {
        memCache.recycle();
    }
}
