package idea.verlif.justdata;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/8 10:27
 */
public class Main {

    public static void main(String[] args) {
        String url = "jdbc:mysql://gz-cynosdbmysql-grp-dxzq6on3.sql.tencentcdb.com:20273/just_station?useSSL=false&useUnicode=true&serverTimezone=Hongkong&characterEncoding=utf8&allowPublicKeyRetrieval=true";
        Pattern pattern = Pattern.compile("/.*\\?");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}
