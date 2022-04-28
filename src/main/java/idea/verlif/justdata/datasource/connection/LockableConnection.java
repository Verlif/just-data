package idea.verlif.justdata.datasource.connection;

import java.sql.Connection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/28 11:12
 */
public class LockableConnection {

    private final Connection connection;

    private final Lock lock;

    public LockableConnection(Connection connection) {
        this.connection = connection;
        this.lock = new ReentrantLock();
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean lock() {
        return lock.tryLock();
    }

    public void release() {
        lock.unlock();
    }
}
