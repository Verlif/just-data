package idea.verlif.justdata;

import idea.verlif.justdata.util.RsaUtils;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/8 10:27
 */
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println(RsaUtils.encryptByPublicKey(
                "1",
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCVww0Ih4Q9NTP+Nrs2okv4l/QGvpZHn6IsTgPd/HpYGcF8qHvznvqEkqW6Nsr4nRAv4Dw9ET4j1kFwthgYHdeYF8t+dY8aOzvc0xVYk9btkJpCzhGmhauyd9oKvsvSK5tIdWLUflvm6cdrtteKU5gGCxBfW0XkFMabG6AQZCdXvwIDAQAB").replace("/", "%2F").replace("+", "%2B"));
    }
}
