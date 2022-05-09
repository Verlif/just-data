package idea.verlif.justdata.sql;

import idea.verlif.justdata.sql.parser.SqlPoint;

import java.util.ArrayList;

/**
 * @author Verlif
 */

public final class PreExecutingInfo {

    private boolean withParam;

    private boolean withBody;

    private boolean withMacro;

    private boolean withEncrypt;

    private boolean withEncode;

    private final ArrayList<SqlPoint> sqlPoints;

    public PreExecutingInfo() {
        sqlPoints = new ArrayList<>();
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

    public ArrayList<SqlPoint> getSqlPoints() {
        return sqlPoints;
    }
}