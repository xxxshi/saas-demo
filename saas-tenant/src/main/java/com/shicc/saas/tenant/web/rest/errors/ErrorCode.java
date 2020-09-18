package com.shicc.saas.tenant.web.rest.errors;

/**
 * @author ziyan
 * @email zhengmengyan@taoqicar.com
 * @date 2018年12月19日
 */
public enum ErrorCode {
    error(-1, ""),
    system_error(0, "系统错误"),
    invalid_params(1, "非法参数"),
    proc_failed(2, "处理失败"),
    bad_credentials(3, "账号或密码错误"),
    bad_verify_code(4, "短信验证码错误"),
    account_status_error(5, "该账号无法使用，请联系管理员"),
    authenticated_fail(6, "认证失败"),
    business_part_not_exist(7, "业务方不存在"),
    business_part_not_disabled(8, "业务方不可用"),
    business_part_not_auth(9, "业务方没有授权角色或者没有配置权限"),
    role_not_auth(10, "该账号角色没有配置权限"),
    user_has_in_other_business_part(11, "该账号已在其他业务方"),
    fail_get_logo(12, "logo获取失败"),
    ;

    ErrorCode(int code, String message) {
        this.code = code + offset;
        this.message = message;
    }

    private int code;
    private String message;
    private static final int offset = 10000;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
