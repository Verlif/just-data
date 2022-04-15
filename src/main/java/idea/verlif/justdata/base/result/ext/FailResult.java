package idea.verlif.justdata.base.result.ext;

import idea.verlif.justdata.base.result.BaseResult;
import idea.verlif.justdata.base.result.ResultCode;

/**
 * @author Verlif
 * @version 1.0
 * @date 2021/11/9 14:06
 */
public class FailResult<T> extends BaseResult<T> {

    private static final FailResult<?> RESULT_FAIL = new FailResult<Object>() {
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

    public FailResult() {
        super(ResultCode.FAILURE);
    }

    public FailResult(String msg) {
        this();
        this.msg = msg;
    }

    public FailResult(ResultCode code) {
        super(code);
    }

    /**
     * 获取无数据失败结果，减少新建对象次数。
     *
     * @return 无数据失败结果，无法改变其中的数据
     */
    public static <T> FailResult<T> empty() {
        return (FailResult<T>) RESULT_FAIL;
    }
}
