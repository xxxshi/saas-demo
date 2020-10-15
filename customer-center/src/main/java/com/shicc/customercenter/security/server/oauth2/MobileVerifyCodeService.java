package com.shicc.customercenter.security.server.oauth2;

/**
 * @description:
 * @author: ChangFeng
 * @create: 2018-12-12 14:08
 **/
public interface MobileVerifyCodeService {

    /**
     * 根据手机号查找验证码
     *
     * @param phone
     * @param type  即end-point
     * @return
     */
    String loadVerifyCodeByPhoneAndBusiness(String phone, String type);

    void removeLoadVerifyCodeByPhoneAndBusiness(String phone, String type);
}
