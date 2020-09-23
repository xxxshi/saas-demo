package com.shicc.customercenter.tenant;


import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


/**
 * @Author tanglh
 * @Date 2018/12/18 14:06
 */
@Component
public class TenantConfigProperties {
    public static final HttpHeaders DEFAULT_HEADERS = new HttpHeaders();
    public static final HttpEntity<Void> DEFAULT_REQUEST = new HttpEntity<>(DEFAULT_HEADERS);


    @Value("${eureka.instance.appname}")
    private String appName;

    /**
     * UAA
     */
    @Value("${saas-tenant-uri:http://saastenant}")
    private String saasTenantBaseUrl;

    @Value("${saas-tenant.allowed-ribbon:true}")
    private boolean saasTenantAllowedRibbon;

    /**
     * 多租户映射服务
     */
    @Value("${tenancy.client.uri:http://tenancy}")
    private String tenancyBaseUrl;

    @Value("${tenancy.client.allowed-ribbon:true}")
    private boolean tenancyAllowedRibbon;
    @Autowired
    @Qualifier("tenantLoadBalancedRestTemplate")
    private RestTemplate tenantLoadBalancedRestTemplate;
    @Autowired
    @Qualifier("tenantVanillaRestTemplate")
    private RestTemplate tenantVanillaRestTemplate;


    public String getAppName() {
        return appName;
    }

    public String getSaasTenantBaseUrl() {
        return saasTenantBaseUrl;
    }

    public boolean isSaasTenantAllowedRibbon() {
        return saasTenantAllowedRibbon;
    }


    public String getTenancyBaseUrl() {
        return tenancyBaseUrl;
    }

    public boolean isTenancyAllowedRibbon() {
        return tenancyAllowedRibbon;
    }

    public RestTemplate getUaaRestTemplate() {
        return genRestTemplate(saasTenantBaseUrl, saasTenantAllowedRibbon);
    }

    public RestTemplate getTenancyRestTemplate() {
        return genRestTemplate(tenancyBaseUrl, tenancyAllowedRibbon);
    }

    private RestTemplate genRestTemplate(String baseUrl, Boolean allowedRibbon) {
        if (BooleanUtils.isTrue(allowedRibbon)) {
            return tenantLoadBalancedRestTemplate;
        } else {
            return tenantVanillaRestTemplate;
        }
    }
}
