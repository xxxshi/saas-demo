package com.shicc.customercenter.tenant;

/**
 * 租户远程查询方法
 *
 */
public interface TenantRemote {

    interface TenantMethods {
        /**
         * 查询全部数据源
         */
        String QUERY_ALL_DATA_SOURCE = "/api/tenant-data-source-infos/serverName/%s";

    }

}
