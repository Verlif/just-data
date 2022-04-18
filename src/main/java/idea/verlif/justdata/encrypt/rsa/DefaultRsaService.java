package idea.verlif.justdata.encrypt.rsa;

import idea.verlif.justdata.util.RsaUtils;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Map;

/**
 * 一次性RSA密钥服务。
 * 获取了私钥后，此密钥对销毁。<br/>
 * <p>
 * 服务逻辑为：<br/>
 * 1. 生产密钥Key，此时会生成密钥对，并将私钥以KeyID作为缓存key存入cache中。<br/>
 * 2. 每次需要使用私钥时，就会通过cache中取出来。
 *
 * @author Verlif
 * @version 1.0
 * @date 2021/12/17 14:28
 */
@Component
public class DefaultRsaService implements RsaService {

    private final String privateKey;
    private final String publicKey;

    public DefaultRsaService() throws Exception {
        Map<String, Key> map = RsaUtils.genKeyPair();
        privateKey = RsaUtils.getPrivateKey(map);
        publicKey = RsaUtils.getPublicKey(map);
    }

    @Override
    public String decryptByPrivateKey(String pass) {
        try {
            return RsaUtils.decryptByPrivateKey(pass, privateKey);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String encryptByPublicKey(String content) {
        try {
            return RsaUtils.encryptByPublicKey(content, publicKey);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String decryptByPublicKey(String pass) {
        try {
            return RsaUtils.decryptByPublicKey(pass, publicKey);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String encryptByPrivateKey(String content) {
        try {
            return RsaUtils.encryptByPrivateKey(content, privateKey);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getPublicKey() {
        return publicKey;
    }
}
