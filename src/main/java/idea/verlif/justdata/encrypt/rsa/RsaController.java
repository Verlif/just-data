package idea.verlif.justdata.encrypt.rsa;

import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ext.OkResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/18 11:13
 */
@Tag(name = "RSA密钥管理")
@RestController
@RequestMapping("/special/rsa")
public class RsaController {

    @Autowired
    private RsaService rsaService;

    @Operation(summary = "获取RSA公钥")
    @GetMapping
    public BaseResult<String> getPublicKey() {
        return new OkResult<>(rsaService.getPublicKey());
    }

    @Operation(summary = "公钥加密数据", description = "一般情况下仅作为数据测试，请勿在公开网络环境中传递敏感数据明文")
    @GetMapping("/encrypt")
    public BaseResult<String> encrypt(@Parameter(name = "需要加密的数据") @RequestParam String data) {
        return new OkResult<>(rsaService.encryptByPublicKey(data));
    }
}
