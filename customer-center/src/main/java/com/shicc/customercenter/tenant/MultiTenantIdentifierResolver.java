package com.shicc.customercenter.tenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import java.util.Optional;
/**
 * 多租户解析器
 */



public class MultiTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    /**
     * 从获取当前线程租户信息
     *
     * @return 数据源名称
     */
    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = ThreadTenantUtil.getTenant();
        return Optional.ofNullable(tenant).orElse(TenantDataSourceProvider.DEFAULT_KEY);
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}