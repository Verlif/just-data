package idea.verlif.justdata.cache.mem;

import idea.verlif.justdata.cache.CacheHandler;
import idea.verlif.spring.taskservice.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * HashMap做的简易缓存，暂不支持持久化 </br>
 * 对于有效时间做的就是懒删除 <br/>
 * 对于Key值的模糊匹配就是用的遍历
 *
 * @author Verlif
 * @version 1.0
 * @date 2021/12/14 10:56
 */
public class MemCache implements CacheHandler, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemCache.class);

    private final Map<String, Object> map;
    private final Map<String, Long> deadMap;

    public MemCache(TaskService taskService) {
        map = new ConcurrentHashMap<>();
        deadMap = new ConcurrentHashMap<>();

        // 设定任务执行器
        taskService.insert(new MemCacheClearer(this));
    }

    @Override
    public <T> void put(String key, T value, long timeout, TimeUnit timeUnit) {
        map.put(key, value);
        if (timeout > 0 && timeUnit != null) {
            deadMap.put(key, System.currentTimeMillis() + timeUnit.toMillis(timeout));
        }
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        if (deadMap.containsKey(key)) {
            deadMap.put(key, System.currentTimeMillis() + unit.toMillis(timeout));
            return true;
        }
        return false;
    }

    @Override
    public <T> T get(String key) {
        Long t = deadMap.get(key);
        if (t != null && t < System.currentTimeMillis()) {
            map.remove(key);
            deadMap.remove(key);
            return null;
        }
        return (T) map.get(key);
    }

    @Override
    public boolean remove(String key) {
        map.remove(key);
        deadMap.remove(key);
        return !map.containsKey(key);
    }

    @Override
    public int removeByMatch(String match) {
        match = match.replaceAll("\\*", ".*");
        Pattern pattern = Pattern.compile(match);
        int c = 0;
        Set<String> s = new HashSet<>(map.keySet());
        for (String key : s) {
            if (pattern.matcher(key).matches()) {
                remove(key);
                c++;
            }
        }
        return c;
    }

    @Override
    public Set<String> findKeyByMatch(String match) {
        match = match.replaceAll("\\*", ".*");
        Pattern pattern = Pattern.compile(match);
        Set<String> s = new HashSet<>();
        Set<String> keySet = new HashSet<>(map.keySet());
        for (String key : keySet) {
            if (pattern.matcher(key).matches() && get(key) != null) {
                s.add(key);
            }
        }
        return s;
    }

    /**
     * 回收失效数据
     */
    public void recycle() {
        int count = 0;
        long now = System.currentTimeMillis();
        Set<String> keySet = new HashSet<>(map.keySet());
        for (String key : keySet) {
            Long t = deadMap.get(key);
            if (t != null && t < now) {
                count++;
                map.remove(key);
                deadMap.remove(key);
            }
        }
        if (count > 0) {
            LOGGER.debug("memCache recycled " + count);
        }
    }

}
