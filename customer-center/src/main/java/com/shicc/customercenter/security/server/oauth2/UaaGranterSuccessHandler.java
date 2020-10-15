package com.shicc.customercenter.security.server.oauth2;

import com.taoqicar.uaaaccount.config.Constants;
import com.taoqicar.uaaaccount.domain.User;
import com.taoqicar.uaaaccount.service.TqLoginLogService;
import com.taoqicar.uaaaccount.service.TqTokenService;
import com.taoqicar.uaaaccount.service.UserService;
import com.taoqicar.uaaaccount.service.dto.TqLoginLogDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * @description:
 * @author: ChangFeng
 * @create: 2018-12-18 15:26
 **/
@Component
public class UaaGranterSuccessHandler implements GranterSuccessHandler {

    @Autowired
    private TqLoginLogService loginLogService;

    @Autowired
    private UserService userService;

    @Autowired
    private TqTokenService tokenService;

    @Override
    public void onGrantSuccess(String grantType, TokenRequest tokenRequest, OAuth2AccessToken oAuth2AccessToken) {
        Map<String, String> requestParameters = tokenRequest.getRequestParameters();
        this.updateLoginInfo(grantType, requestParameters, oAuth2AccessToken);
    }

    private void updateLoginInfo(String grantType, Map<String, String> requestParameters, OAuth2AccessToken oAuth2AccessToken) {
        String loginOrPhoneNum;
        if (PasswordTokenGranter.PARAM_PASSWORD.equals(grantType)) {
            loginOrPhoneNum = requestParameters.get(PasswordTokenGranter.PARAM_USERNAME);
        } else {
            loginOrPhoneNum = requestParameters.get(MobileTokenGranter.PARAM_PHONE_NUM);
        }
        Optional<User> userOptional = userService.getUserByLoginOrPhoneNum(loginOrPhoneNum);

        // 刷新token不需要登录
        if (!grantType.equals("refresh_token")) {
            // user cannot be null
            User user = userOptional.get();
            // update user
            userService.updateLoginInfo(user);

            // save login info
            TqLoginLogDTO loginLogDTO = new TqLoginLogDTO();
            loginLogDTO.setLoginTime(ZonedDateTime.now());
            loginLogDTO.setUserId(user.getId());
            loginLogDTO.setEndpoint(requestParameters.get("endpoint"));
            loginLogDTO.setDeviceId(requestParameters.get("deviceId"));
            loginLogDTO.setClientVersion(requestParameters.get("clientV"));
            loginLogDTO.setClientSystemVersion(requestParameters.get("clientOSV"));
            loginLogDTO.setClientMobileModel(requestParameters.get("mobileModel"));
            loginLogDTO.setUuid(requestParameters.get("uuid"));
            loginLogDTO.setDeleteStatus(Constants.DeleteStatus.NO);
            loginLogService.saveDTO(loginLogDTO);
        }

    }

}
