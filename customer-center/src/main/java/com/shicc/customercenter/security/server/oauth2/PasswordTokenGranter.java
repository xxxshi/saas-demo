package com.shicc.customercenter.security.server.oauth2;

import com.shicc.customercenter.service.CustomerService;
import lombok.SneakyThrows;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @ClassName: PasswordTokenGranter
 * @Description: 密码Granter
 * @author tyjuncai
 * @date 2018/12/27 19:02
 */
public class PasswordTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "password";

    public static final String PARAM_USERNAME = "username";

    public static final String PARAM_PASSWORD = "password";

    public static final String PARAM_ENDPOINT = "endpoint";

    private final AuthenticationManager authenticationManager;

    private final CustomerService customerService;

    public PasswordTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices, ClientDetailsService
        clientDetailsService, CustomerService customerService, OAuth2RequestFactory requestFactory) {
        this(authenticationManager, tokenServices, clientDetailsService, customerService, requestFactory, GRANT_TYPE);
    }

    protected PasswordTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices, ClientDetailsService
        clientDetailsService, CustomerService customerService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
        this.customerService = customerService;
    }

    @SneakyThrows
    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String username = parameters.get(PARAM_USERNAME);
        String password = parameters.get(PARAM_PASSWORD);
        String endpoint = parameters.get(PARAM_ENDPOINT);
        // Protect from downstream leaks of password
        parameters.remove(PARAM_PASSWORD);

        Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        try {
            userAuth = authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException ase) {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            throw new Exception();
        } catch (BadCredentialsException e) {
            // If the username/password are wrong the spec says we should send 400/invalid grant
            throw new Exception();
        }
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new Exception();
        }

        // 校验业务方账号的业务方状态
//        Object securityUser = userAuth.getPrincipal();
//        if (securityUser instanceof SecurityUser) {
//            SecurityUser user = (SecurityUser) securityUser;
//            userService.checkBusinessPartByUser(user.getType(), user.getTenantCode(), user.getUserId(), endpoint);
//        } else {
//            throw UaaAccountBizException.build(ErrorCode.authenticated_fail);
//        }


        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
}
