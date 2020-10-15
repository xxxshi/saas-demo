package com.shicc.customercenter.captcha;

import com.google.code.kaptcha.Constants;
import com.shicc.customercenter.dto.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class KaptchaService {
    private final Logger log = LoggerFactory.getLogger(KaptchaService.class);

    public Result checkVerifyCode(String verifyCode, HttpServletRequest request) {
        Result result = new Result();
        String kaptchaSessionKey = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        System.out.println("Session  verifyCode " + kaptchaSessionKey + " form verityCode " + kaptchaSessionKey);
        if (!StringUtils.equals(verifyCode, kaptchaSessionKey)) {
            log.debug("验证码错误");
            result.setSuccess(Boolean.FALSE);
            result.setMsg(("验证码错误"));
        } else {
            result.setSuccess(Boolean.TRUE);
            //校验成功，则清除session里的验证码
            request.getSession().removeAttribute(Constants.KAPTCHA_SESSION_KEY);
            log.debug("登录成功");
        }
        return result;
    }

}
