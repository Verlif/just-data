package idea.verlif.justdata.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/27 17:15
 */
@Service
public class ConnectionHolder {

    @Autowired
    private DataSourceConfig dataSourceConfig;

    private final Map<String, List<Connection>> connectionMap;
    private final Map<String, List<Lock>> lockMap;

    public ConnectionHolder() {
        connectionMap = new ConcurrentHashMap<>();
        lockMap = new ConcurrentHashMap<>();
    }

    // TODO: 增加动态获取未使用连接的方法，以修复事务bug
//
//    public Connection getTreadLocalConnection(String label) {
//        DruidDataSource dataSource = dataSourceConfig.getDruidDataSource(label);
//        List<Connection> connectionList = connectionMap.get(label);
//        if (connectionList == null) {
//            connectionList = new ArrayList<>();
//            connectionMap.put(label, connectionList);
//            lockMap.put(label, new ArrayList<>());
//        }
//        List<Lock> lockList = lockMap.get(label);
//        // 当可以增加新的连接时
//        if (dataSource.getMaxActive() > connectionList.size()) {
//            try {
//                Connection connection = dataSource.getConnection();
//                connectionList.add(connection);
//                Lock lock = new ReentrantLock();
//                lockList.add(lock);
//            } catch (SQLException ignored) {
//                return null;
//            }
//        } else {
//
//        }
//    }
//
//    public Connection releaseConnection(String label) {
//
//    }
}
