package idea.verlif.justdata.sql;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/19 14:24
 */
public class Sql {

    private String label;
    private String sql;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public boolean isEnabled() {
        return label != null && sql != null;
    }

}
