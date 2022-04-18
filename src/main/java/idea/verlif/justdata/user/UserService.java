package idea.verlif.justdata.user;

import idea.verlif.justdata.cache.CacheHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/18 15:55
 */
@Service
public class UserService {

    private static final String HEADER_TOKEN = "auth";

    @Autowired
    private CacheHandler cacheHandler;

}
