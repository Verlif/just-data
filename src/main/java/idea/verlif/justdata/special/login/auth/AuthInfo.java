package idea.verlif.justdata.special.login.auth;

/**
 * 认证信息 <br/>
 * 用于校验来访用户身份
 *
 * @author Verlif
 * @version 1.0
 * @date 2022/1/7 10:04
 */
public interface AuthInfo {

    /**
     * 登录标识，用于区别登录者或是登录方式
     *
     * @return 登录标识，可以是用户名或是用户ID，也可以是登录类型
     */
    String getId();

    /**
     * 登录口令，用于验证登录合法性
     *
     * @return 登录口令，可以是登录标识对应的密码，也可以是登录随机码
     */
    String getToken();
}
