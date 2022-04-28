package idea.verlif.justdata.datasource.connection;

import com.alibaba.druid.pool.DruidDataSource;
import idea.verlif.justdata.datasource.DataSourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/27 17:15
 */
@Service
public class ConnectionHolder {

    @Autowired
    private DataSourceConfig dataSourceConfig;

    private final Map<String, List<LockableConnection>> lockableConnectionMap;
    private final Map<String, Connection> connectionMap;

    public ConnectionHolder() {
        lockableConnectionMap = new ConcurrentHashMap<>();
        connectionMap = new HashMap<>();
    }

    public synchronized Connection getConnection(String label) throws SQLException {
        Connection connection = connectionMap.get(label);
        if (connection == null) {
            connection = dataSourceConfig.getDruidDataSource(label).getConnection();
            connectionMap.put(label, connection);
        }
        return connection;
    }

    public LockableConnection getUnlockedConnection(String label) throws SQLException, InterruptedException {
        DruidDataSource dataSource = dataSourceConfig.getDruidDataSource(label);
        List<LockableConnection> connectionList;
        synchronized (lockableConnectionMap) {
            connectionList = lockableConnectionMap.get(label);
            if (connectionList == null) {
                connectionList = new ArrayList<>();
                lockableConnectionMap.put(label, connectionList);
            }
        }
        if (connectionList.size() == 0) {
            return newLockedConnection(label, dataSource);
        } else {
            while (true) {
                for (LockableConnection lc : connectionList) {
                    if (lc.lock()) {
                        return lc;
                    }
                }
                LockableConnection connection = newLockedConnection(label, dataSource);
                if (connection != null) {
                    return connection;
                }
            }
        }
    }

    private LockableConnection newLockedConnection(String label, DruidDataSource dataSource) throws SQLException {
        int max = dataSource.getMaxActive() - 1;
        synchronized (lockableConnectionMap) {
            List<LockableConnection> connectionList = lockableConnectionMap.get(label);
            if (connectionList.size() < max) {
                Connection connection = dataSource.getConnection();
                if (connection.getAutoCommit()) {
                    connection.setAutoCommit(false);
                }
                LockableConnection lc = new LockableConnection(connection);
                connectionList.add(lc);
                lc.lock();
                return lc;
            } else {
                return null;
            }
        }
    }

}
