package com.shicc.customercenter.security.server.oauth2;

import com.taoqicar.uaaaccount.security.SecurityUser;
import com.taoqicar.uaaaccount.service.util.RedisService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.*;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * @description:
 * @author: ChangFeng
 * @create: 2018-12-13 10:47
 **/
@Component
public class UaaTokenServices extends DefaultTokenServices implements InitializingBean {

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private TokenEnhancer accessTokenEnhancer;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private RedisService redisService;

    /**
     * 重写创建accessToken方法，
     * 一个用户可以拥有多个Token(每个端一个)，
     * 同端只能有一个有效Token
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        OAuth2AccessToken existingAccessToken = tokenStore.getAccessToken(authentication);
        OAuth2RefreshToken refreshToken = null;
        if (null != existingAccessToken) {
            boolean allowMoreEndPoint = false;
            if (authentication.getPrincipal() instanceof SecurityUser) {
                SecurityUser principal = (SecurityUser) authentication.getPrincipal();
                SetOperations<String, Object> stringObjectSetOperations = redisService.getSetOperations();
                allowMoreEndPoint = stringObjectSetOperations.isMember(RedisService.REDIS_KEY_ALLOW_USER_MORE_ONE_ENDPOINT, Optional.ofNullable(principal.getPhoneNum()).orElse("-1"));
            }
            if (!allowMoreEndPoint) {
                refreshToken = existingAccessToken.getRefreshToken();
                dealExistingAccessToken(existingAccessToken);
            }
        }

        if (refreshToken == null) {
            refreshToken = createRefreshToken(authentication);
        } else if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken expiring = (ExpiringOAuth2RefreshToken) refreshToken;
            if (System.currentTimeMillis() > expiring.getExpiration().getTime()) {
                refreshToken = createRefreshToken(authentication);
            }
        }

        OAuth2AccessToken accessToken = createAccessToken(authentication, refreshToken);
        tokenStore.storeAccessToken(accessToken, authentication);
        // In case it was modified
        refreshToken = accessToken.getRefreshToken();
        if (refreshToken != null) {
            tokenStore.storeRefreshToken(refreshToken, authentication);
        }
        return accessToken;
    }

    protected OAuth2RefreshToken createRefreshToken(OAuth2Authentication authentication) {
        if (!isSupportRefreshToken(authentication.getOAuth2Request())) {
            return null;
        }
        int validitySeconds = getRefreshTokenValiditySeconds(authentication.getOAuth2Request());
        String value = UUID.randomUUID().toString();
        if (validitySeconds > 0) {
            return new DefaultExpiringOAuth2RefreshToken(value, new Date(System.currentTimeMillis()
                + (validitySeconds * 1000L)));
        }
        return new DefaultOAuth2RefreshToken(value);
    }

    protected OAuth2AccessToken createAccessToken(OAuth2Authentication authentication, OAuth2RefreshToken refreshToken) {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
        int validitySeconds = getAccessTokenValiditySeconds(authentication.getOAuth2Request());
        if (validitySeconds > 0) {
            token.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
        }
        token.setRefreshToken(refreshToken);
        token.setScope(authentication.getOAuth2Request().getScope());

        return accessTokenEnhancer != null ? accessTokenEnhancer.enhance(token, authentication) : token;
    }

    protected void dealExistingAccessToken(OAuth2AccessToken existingAccessToken) {
        Optional.ofNullable(existingAccessToken).ifPresent(accessToken -> {
            tokenStore.removeAccessToken(accessToken);
            Optional.ofNullable(accessToken.getRefreshToken()).ifPresent(refreshToken -> {
                tokenStore.removeRefreshToken(refreshToken);
            });
        });
    }

    @Override
    public void setTokenStore(TokenStore tokenStore) {
        super.setTokenStore(tokenStore);
        this.tokenStore = tokenStore;
    }

    public void setAccessTokenEnhancer(TokenEnhancer accessTokenEnhancer) {
        this.accessTokenEnhancer = accessTokenEnhancer;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.setSupportRefreshToken(true);
        this.setClientDetailsService(clientDetailsService);
        this.setAuthenticationManager(authenticationManager);
        this.setTokenStore(tokenStore);
        Assert.notNull(tokenStore, "tokenStore must be set");
    }
}
