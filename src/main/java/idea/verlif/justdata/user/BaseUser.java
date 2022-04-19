package idea.verlif.justdata.user;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/18 14:16
 */
public class BaseUser {

    private Object id;

    private String key;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + ((id instanceof Number) ? id : "\"" + id + "\"") +
                ", \"key\":\"" + key + "\"" +
                '}';
    }
}
