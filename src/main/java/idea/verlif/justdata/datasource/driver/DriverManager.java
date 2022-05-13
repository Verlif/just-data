package idea.verlif.justdata.datasource.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/5/13 10:33
 */
@Configuration
@ConfigurationProperties(prefix = "just-data")
public class DriverManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DriverManager.class);

    private DriverInfo[] drivers;

    private final Map<String, Driver> driverMap;

    public DriverManager() {
        driverMap = new HashMap<>();
    }

    public DriverInfo[] getDrivers() {
        return drivers;
    }

    public void setDrivers(DriverInfo[] drivers) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.drivers = drivers;
        for (DriverInfo driverInfo : drivers) {
            URL u = new URL("file:" + driverInfo.getDriverFile());
            URLClassLoader ucl = new URLClassLoader(new URL[]{u});
            Driver driver = (Driver) Class.forName(driverInfo.getDriverName(), true, ucl).newInstance();
            driverMap.put(driverInfo.getDriverName(), driver);
        }
        LOGGER.info("Already loaded drivers - " + Arrays.toString(driverMap.keySet().toArray()));
    }

    public Driver getDriver(String driverName) {
        return driverMap.get(driverName);
    }
}
