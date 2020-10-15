package com.shicc.customercenter.security.server.oauth2;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.TokenRequest;

/**
 * @description:
 * @author: ChangFeng
 * @create: 2018-12-18 18:23
 **/
public interface GranterSuccessHandler {

    public void onGrantSuccess(String grantType, TokenRequest tokenRequest, OAuth2AccessToken oAuth2AccessToken);

}
