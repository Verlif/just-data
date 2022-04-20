package idea.verlif.justdata.base.result;

import idea.verlif.justdata.util.MessagesUtils;

/**
 * @author Verlif
 * @version 1.0
 * @date 2021/11/9 9:17
 */
public enum ResultCode {

    /**
     * 成功返回码
     */
    SUCCESS(200, MessagesUtils.message("result.ok")),
    /**
     * 失败返回码
     */
    FAILURE(500, MessagesUtils.message("result.fail")),
    /**
     * Token错误
     */
    FAILURE_TOKEN(501, MessagesUtils.message("result.fail.token")),
    /**
     * 权限不足错误
     */
    FAILURE_UNAVAILABLE(504, MessagesUtils.message("result.fail.unavailable")),
    /**
     * 参数错误
     */
    FAILURE_PARAMETER(510, MessagesUtils.message("result.fail.parameter")),
    /**
     * 缺少参数
     */
    FAILURE_PARAMETER_LACK(511, MessagesUtils.message("result.fail.parameter.lack")),
    /**
     * 文件操作失败
     */
    FAILURE_FILE(520, MessagesUtils.message("result.fail.file")),
    /**
     * 文件上传失败
     */
    FAILURE_FILE_UPLOAD(521, MessagesUtils.message("result.fail.file.upload")),
    /**
     * 文件下载失败
     */
    FAILURE_FILE_DOWNLOAD(522, MessagesUtils.message("result.fail.file.download")),
    /**
     * 文件未找到
     */
    FAILURE_FILE_MISSING(523, MessagesUtils.message("result.fail.file.missing")),
    /**
     * 用户登录失败
     */
    FAILURE_LOGIN_FAIL(530, MessagesUtils.message("result.fail.login")),
    /**
     * 用户不存在
     */
    FAILURE_LOGIN_MISSING(531, MessagesUtils.message("result.fail.login.missing")),
    /**
     * 未登录错误
     */
    FAILURE_NOT_LOGIN(532, MessagesUtils.message("result.fail.login.not")),
    /**
     * 访问受限
     */
    FAILURE_LIMIT(533, MessagesUtils.message("result.fail.limit")),
    /**
     * 没有相关数据
     */
    FAILURE_DATA_MISSING(540, MessagesUtils.message("request.data.missing")),
    /**
     * 未找到标签
     */
    FAILURE_NO_LABEL(610, MessagesUtils.message("request.fail.no.label")),
    /**
     * 未找到API
     */
    FAILURE_NO_API(620, MessagesUtils.message("request.fail.no.api")),
    /**
     * 未找到API方法
     */
    FAILURE_NO_API_METHOD(630, MessagesUtils.message("request.fail.no.api.method")),
    /**
     * 未开启登录
     */
    FAILURE_DISABLED_LOGIN(640, MessagesUtils.message("disabled.login")),
    /**
     * 服务器错误
     */
    FAILURE_ERROR(999, MessagesUtils.message("error.default"));

    private final Integer code;

    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
