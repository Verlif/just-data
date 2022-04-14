package idea.verlif.justdata.base.result.ext;

import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ResultCode;

/**
 * @author Verlif
 * @version 1.0
 * @date 2021/11/9 14:06
 */
public class OkResult<T> extends BaseResult<T> {

    private static final OkResult<Object> RESULT_OK = new OkResult<Object>() {
        @Override
        public void setCode(Integer code) {
        }

        @Override
        public void setData(Object data) {
        }

        @Override
        public void setMsg(String msg) {
        }
    };

    public OkResult() {
        super(ResultCode.SUCCESS);
    }

    public OkResult(T data) {
        this();
        this.data = data;
    }

    /**
     * 获取无数据成功结果，减少新建对象次数。
     *
     * @return 无数据成功结果，无法改变其中的数据
     */
    public static <T> OkResult<T> empty() {
        return (OkResult<T>) RESULT_OK;
    }
}
