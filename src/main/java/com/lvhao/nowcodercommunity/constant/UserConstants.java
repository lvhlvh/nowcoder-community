package com.lvhao.nowcodercommunity.constant;

public class UserConstants {

    public static final int ACTIVATION_SUCCESS = 0;

    public static final int ACTIVATION_REPEAT = 1;

    public static final int ACTIVATION_FAILURE = 2;

    public static final String ACTIVATION_SUCCESS_MSG = "激活成功,您的账号已经可以正常使用了!";

    public static final String ACTIVATION_REPEAT_MSG = "无效操作,该账号已经激活过了!";

    public static final String ACTIVATION_FAILURE_MSG = "激活失败,您提供的激活码不正确!";

    public static final String ACCOUNT_EXISTS = "该账号已存在!";

    public static final String EMAIL_EXISTS = "该邮箱已被注册!";

    public static final String ACCOUNT_NOT_EXISTS = "该账号不存在!";

    public static final String ACCOUNT_NOT_ACTIVATED = "该账号未激活!";

    public static final String ACCOUNT_NOT_LOGIN = "用户未登录!";

    public static final String WRONG_PASSWORD = "密码不正确!";

    public static final String KAPTCHA_IN_SESSION = "kaptcha";

    public static final String USER_IN_SESSION = "loginUser";

    public static final String USER_IN_MV = "loginUser";

    public static final String REG_SUCCESS = "注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!";

    public static final String WRONG_ACTIVATION_CODE = "验证码不正确!";

    /** 12小时 */
    public static final int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /** 7天 */
    public static final int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 7;

}
