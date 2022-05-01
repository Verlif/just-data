package idea.verlif.justdata.sql;

import idea.verlif.justdata.sql.parser.SqlPoint;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Verlif
 */

public final class PreExecutingInfo {

    private boolean withParam;

    private boolean withBody;

    private boolean withMacro;

    private boolean withEncrypt;

    private boolean withEncode;

    private final Set<SqlPoint> sqlPoints;

    public PreExecutingInfo() {
        sqlPoints = new HashSet<>();
    }

    public boolean isWithParam() {
        return withParam;
    }

    public void setWithParam(boolean withParam) {
        this.withParam = withParam;
    }

    public boolean isWithBody() {
        return withBody;
    }

    public void setWithBody(boolean withBody) {
        this.withBody = withBody;
    }

    public boolean isWithMacro() {
        return withMacro;
    }

    public void setWithMacro(boolean withMacro) {
        this.withMacro = withMacro;
    }

    public boolean isWithEncrypt() {
        return withEncrypt;
    }

    public void setWithEncrypt(boolean withEncrypt) {
        this.withEncrypt = withEncrypt;
    }

    public boolean isWithEncode() {
        return withEncode;
    }

    public void setWithEncode(boolean withEncode) {
        this.withEncode = withEncode;
    }

    public Set<SqlPoint> getSqlPoints() {
        return sqlPoints;
    }
}