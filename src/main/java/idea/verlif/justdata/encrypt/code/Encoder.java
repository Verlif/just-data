package idea.verlif.justdata.encrypt.code;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/22 10:45
 */
@Component
public class Encoder {

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    public String encode(String target) {
        return ENCODER.encode(target);
    }

    public boolean matches(String raw, String encode) {
        return ENCODER.matches(raw, encode);
    }
}
