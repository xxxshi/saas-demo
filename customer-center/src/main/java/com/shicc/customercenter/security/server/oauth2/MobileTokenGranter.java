package com.shicc.customercenter.security.server.oauth2;

import com.taoqicar.uaaaccount.security.SecurityUser;
import com.taoqicar.uaaaccount.service.UserService;
import com.taoqicar.uaaaccount.service.dto.MobileAuthCredentials;
import com.taoqicar.uaaaccount.web.rest.errors.ErrorCode;
import com.taoqicar.uaaaccount.web.rest.errors.UaaAccountBizException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @description:
 * @author: ChangFeng
 * @create: 2018-12-12 14:05
 **/
public class MobileTokenGranter extends AbstractTokenGranter {

    public static final String GRANT_TYPE = "verify_code";

    public static final String PARAM_PHONE_NUM = "phone_num";

    public static final String PARAM_VERIFY_CODE = "verify_code";

    public static final String PARAM_ENDPOINT = "endpoint";

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    public MobileTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                              UserService userService, OAuth2RequestFactory requestFactory) {
        this(authenticationManager, tokenServices, clientDetailsService, userService, requestFactory, GRANT_TYPE);
    }

    protected MobileTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices,
                                 ClientDetailsService clientDetailsService, UserService userService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<String, String>(tokenRequest.getRequestParameters());
        String phoneNum = parameters.get(PARAM_PHONE_NUM);
        String verifyCode = parameters.get(PARAM_VERIFY_CODE);
        String endpoint = parameters.get(PARAM_ENDPOINT);

        Authentication userAuth = new MobileAuthentication(phoneNum, new MobileAuthCredentials(phoneNum, verifyCode, endpoint));
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        try {
            userAuth = authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException ase) {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            throw UaaAccountBizException.build(ErrorCode.account_status_error);
        } catch (BadCredentialsException e) {
            // If the username/password are wrong the spec says we should send 400/invalid grant
            throw UaaAccountBizException.build(ErrorCode.bad_verify_code);
        }
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw UaaAccountBizException.build(ErrorCode.authenticated_fail);
        }

        // 校验业务方账号的业务方状态
        Object securityUser = userAuth.getPrincipal();
        if (securityUser instanceof SecurityUser) {
            SecurityUser user = (SecurityUser) securityUser;
            userService.checkBusinessPartByUser(user.getType(), user.getTenantCode(), user.getUserId(), endpoint);
        } else {
            throw UaaAccountBizException.build(ErrorCode.authenticated_fail);
        }

        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
}
