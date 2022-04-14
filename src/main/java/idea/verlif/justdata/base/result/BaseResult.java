package idea.verlif.justdata.base.result;

/**
 * @author Verlif
 * @version 1.0
 * @date 2021/11/9 9:17
 */
public class BaseResult<T> {

    /**
     * 自定义返回码
     */
    protected Integer code;

    /**
     * 返回信息描述
     */
    protected String msg;

    /**
     * 返回数据
     */
    protected T data;

    public BaseResult() {
    }

    public BaseResult(ResultCode code) {
        this.code = code.getCode();
        this.msg = code.getMsg();
    }

    public BaseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseResult<T> code(Integer code) {
        this.code = code;
        return this;
    }

    public BaseResult<T> msg(Object msg) {
        this.msg = msg.toString();
        return this;
    }

    public BaseResult<T> appendMsg(String append) {
        this.msg += append;
        return this;
    }

    public BaseResult<T> withParam(String param) {
        this.msg += " - " + param;
        return this;
    }

    public BaseResult<T> data(T data) {
        this.data = data;
        return this;
    }

    public BaseResult<T> code(ResultCode code) {
        this.code = code.getCode();
        this.msg = code.getMsg();
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 判断结果Code是否与目标相同
     *
     * @param code 目标结果
     * @return 是否相同
     */
    public boolean equals(ResultCode code) {
        return code.getCode().equals(this.code);
    }
}
