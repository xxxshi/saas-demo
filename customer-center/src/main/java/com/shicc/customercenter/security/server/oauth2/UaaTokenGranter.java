package com.shicc.customercenter.security.server.oauth2;

import com.taoqicar.uaaaccount.service.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: ChangFeng
 * @create: 2018-12-18 18:19
 **/
@Component
public class UaaTokenGranter implements TokenGranter, InitializingBean {

    private TokenGranter delegator;

    @Autowired
    private GranterSuccessHandler granterSuccessHandler;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UaaTokenServices tokenServices;

    @Autowired
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
        OAuth2AccessToken oAuth2AccessToken = delegator.grant(grantType, tokenRequest);
        granterSuccessHandler.onGrantSuccess(grantType, tokenRequest, oAuth2AccessToken);
        return oAuth2AccessToken;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(granterSuccessHandler, "granterSuccessHandler can not be null");
        Assert.notNull(clientDetailsService, "clientDetailsService can not be null");
        Assert.notNull(authenticationManager, "authenticationManager can not be null");
        Assert.notNull(tokenServices, "tokenServices can not be null");
        OAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
        List<TokenGranter> tokenGranters = new ArrayList();
        tokenGranters.add(new PasswordTokenGranter(authenticationManager, tokenServices, clientDetailsService, userService, requestFactory));
        tokenGranters.add(new RefreshTokenGranter(tokenServices, clientDetailsService, requestFactory));
        tokenGranters.add(new ClientCredentialsTokenGranter(tokenServices, clientDetailsService, requestFactory));
        tokenGranters.add(new MobileTokenGranter(authenticationManager, tokenServices, clientDetailsService, userService, requestFactory));
        CompositeTokenGranter compositeTokenGranter = new CompositeTokenGranter(tokenGranters);
        this.delegator = compositeTokenGranter;
    }
}
