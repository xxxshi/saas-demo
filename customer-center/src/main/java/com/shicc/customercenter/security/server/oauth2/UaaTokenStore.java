package com.shicc.customercenter.security.server.oauth2;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @description:
 **/
@Component
public class UaaTokenStore implements TokenStore, InitializingBean {

    private TokenStore delegator;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

//    @Autowired
//    private TqTokenService tokenService;

    @Override
    public void afterPropertiesSet() throws Exception {
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setAuthenticationKeyGenerator(new UaaAuthenticationKeyGenerator());
        delegator = redisTokenStore;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        OAuth2Authentication oAuth2Authentication = delegator.readAuthentication(token);
//        if (null == oAuth2Authentication) {
//            tokenService.invalidateToken(token.getValue());
//        }
        return oAuth2Authentication;
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        OAuth2Authentication oAuth2Authentication = delegator.readAuthentication(token);
//        if (null == oAuth2Authentication) {
//            tokenService.invalidateToken(token);
//        }
        return oAuth2Authentication;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        delegator.storeAccessToken(token, authentication);

        Map<String, String> requestParameters = authentication.getOAuth2Request().getRequestParameters();
        Object principal = authentication.getUserAuthentication().getPrincipal();
//        if (principal instanceof SecurityUser) {
//            SecurityUser securityUser = (SecurityUser) principal;
//            Date expiration = token.getExpiration();
//            Instant instant = Instant.ofEpochMilli(expiration.getTime());
//            ZonedDateTime expireTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
//            // create TqToken
//            tokenService.create(securityUser.getUserId(), token.getValue(), requestParameters.get("endpoint"), expireTime);
//        }
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        OAuth2AccessToken oAuth2AccessToken = delegator.readAccessToken(tokenValue);
//        if (null == oAuth2AccessToken) {
//            tokenService.invalidateToken(tokenValue);
//        }
        return oAuth2AccessToken;
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        delegator.removeAccessToken(token);
//        tokenService.invalidateToken(token.getValue());
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        delegator.storeRefreshToken(refreshToken, authentication);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        return delegator.readRefreshToken(tokenValue);
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return delegator.readAuthenticationForRefreshToken(token);
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        delegator.removeRefreshToken(token);
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        delegator.removeAccessTokenUsingRefreshToken(refreshToken);
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return delegator.getAccessToken(authentication);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        return delegator.findTokensByClientIdAndUserName(clientId, userName);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return delegator.findTokensByClientId(clientId);
    }
}
