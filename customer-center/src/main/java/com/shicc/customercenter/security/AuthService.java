package com.shicc.customercenter.security;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 登录service
 */
@Service
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String PASSWORD_GRANT_TYPE = "password";
    private static final String GRANT_TYPE_KEY = "grantType";
    private static final String PRODUCT_CODE_KEY = "productCode";
    private static final String TRANSACTION_NO_KEY = "transactionNo";
    private static final String VERIFY_CAPTCHA_KEY = "verifyCaptcha";

    @Autowired
    private OAuth2AuthenticationService authenticationService;


    /**
     * 登录
     *
     * @param request
     * @param response
     * @param params
     * @return
     */
    public ResponseEntity<OAuth2AccessToken> authenticate(HttpServletRequest request, HttpServletResponse response, @RequestBody
            Map<String, String> params) {
        checkVerifyCaptcha(params);
        return authenticationService.authenticate(request, response, params);
    }

    /**
     * 校验验证码
     *
     * @param params
     */
    private void checkVerifyCaptcha(Map<String, String> params) {

    }

    /**
     * 登出
     *
     * @param request
     * @param response
     * @return
     */
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("logging out user {}", SecurityContextHolder.getContext().getAuthentication().getName());
        authenticationService.logout(request, response);
        return ResponseEntity.noContent().build();
    }

}
