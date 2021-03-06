package com.shicc.saas.tenant.repository;

import com.shicc.saas.tenant.domain.TenantDataSourceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the TenantDataSourceInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TenantDataSourceInfoRepository extends JpaRepository<TenantDataSourceInfo, Long> {

    List<TenantDataSourceInfo> findByServerNameAndTypeAndStatusAndDeleteStatus(String serverName, String type, Integer Status, Integer deleteStatus);

    TenantDataSourceInfo findTopByTenantCodeAndServerNameAndTypeAndStatusAndDeleteStatus(String tenantCode, String serverName, String type,Integer Status, Integer deleteStatus);

    List<TenantDataSourceInfo> findByTenantCodeAndDeleteStatus(String tenantCode, Integer deleteStatus);

    List<TenantDataSourceInfo> findByTenantCodeAndStatusAndDeleteStatus(String tenantCode, Integer status, Integer integerZero);

    List<TenantDataSourceInfo> findByDeleteStatus(Integer deleteStatus);

}
