package com.shicc.customercenter.security.server.oauth2;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * @description:
 * @author: ChangFeng
 * @create: 2018-12-13 10:52
 **/
public class UaaAuthenticationKeyGenerator extends DefaultAuthenticationKeyGenerator {

    private static final String CLIENT_ID = "client_id";

    private static final String SCOPE = "scope";

    private static final String USERNAME = "username";

    private static final String ENDPOINT = "endpoint";

    @Override
    public String extractKey(OAuth2Authentication authentication) {
        Map<String, String> values = new LinkedHashMap<String, String>();
        OAuth2Request authorizationRequest = authentication.getOAuth2Request();
        if (!authentication.isClientOnly()) {
            values.put(USERNAME, authentication.getName());
        }
        values.put(CLIENT_ID, authorizationRequest.getClientId());
        if (authorizationRequest.getScope() != null) {
            values.put(SCOPE, OAuth2Utils.formatParameterList(new TreeSet<String>(authorizationRequest.getScope())));
        }
        // add endpoint param
        String endpoint = authentication.getOAuth2Request().getRequestParameters().get(ENDPOINT);
        if (StringUtils.isBlank(endpoint)) {
            throw new InvalidRequestException("endpoint can not be null");
        }
        values.put(ENDPOINT, endpoint);
        return generateKey(values);
    }

}
