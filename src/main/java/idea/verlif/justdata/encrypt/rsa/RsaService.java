package idea.verlif.justdata.encrypt.rsa;

/**
 * RSA密钥服务，用于管理RSA密钥对。
 * 本服务不向外提供私钥。
 *
 * @author Verlif
 * @version 1.0
 * @date 2021/12/17 14:21
 */
public interface RsaService {

    /**
     * 私钥解密
     *
     * @param pass 密文
     * @return 解密后明文
     */
    String decryptByPrivateKey(String pass);

    /**
     * 公钥加密
     *
     * @param content 明文
     * @return 加密后密文
     */
    String encryptByPublicKey(String content);

    /**
     * 公钥解密
     *
     * @param pass 密文
     * @return 解密后明文
     */
    String decryptByPublicKey(String pass);

    /**
     * 私钥加密
     *
     * @param content 明文
     * @return 加密后密文
     */
    String encryptByPrivateKey(String content);

    /**
     * 获取公钥
     *
     * @return 公钥
     */
    String getPublicKey();
}
