package com.shicc.saas.tenant.service.mapper;


import com.shicc.saas.tenant.domain.TenantDataSourceInfo;
import com.shicc.saas.tenant.dto.TenantDataSourceInfoDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity TenantDataSourceInfo and its DTO TenantDataSourceInfoDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TenantDataSourceInfoMapper extends EntityMapper<TenantDataSourceInfoDTO, TenantDataSourceInfo> {



    default TenantDataSourceInfo fromId(Long id) {
        if (id == null) {
            return null;
        }
        TenantDataSourceInfo tenantDataSourceInfo = new TenantDataSourceInfo();
        tenantDataSourceInfo.setId(id);
        return tenantDataSourceInfo;
    }
}
