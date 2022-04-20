package idea.verlif.justdata.sql.exception;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/20 10:16
 */
public class LackOfSqlParamException extends RuntimeException {

    private final String param;

    public LackOfSqlParamException(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }
}
