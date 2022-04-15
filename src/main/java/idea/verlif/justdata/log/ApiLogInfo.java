package idea.verlif.justdata.log;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * @author Verlif
 */
public class ApiLogInfo {

    private String label;

    private String api;

    private String method;

    private Date time;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ApiLogInfo{" +
                "label='" + label + '\'' +
                ", api='" + api + '\'' +
                ", method='" + method + '\'' +
                ", time=" + time +
                '}';
    }
}
