package idea.verlif.justdata.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @author Verlif
 * @version 1.0
 * @date 2021/11/16 11:04
 */
@Component
public class MessagesUtils {

    private static MessageSource ms;

    /**
     * 以注入的方式，为静态参数赋值
     *
     * @param messageSource 信息源
     */
    public MessagesUtils(@Autowired MessageSource messageSource) {
        ms = messageSource;
    }

    public static String message(String code, String... args) {
        try {
            return ms.getMessage(code, args, Locale.getDefault());
        } catch (Exception ignored) {
            return code;
        }
    }
}
