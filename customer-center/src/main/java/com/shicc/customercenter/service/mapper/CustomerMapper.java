package com.shicc.customercenter.service.mapper;


import com.shicc.customercenter.domain.Customer;
import com.shicc.customercenter.dto.CustomerDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity TenantDataSourceInfo and its DTO TenantDataSourceInfoDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface CustomerMapper extends EntityMapper<CustomerDTO, Customer> {



    default Customer fromId(Long id) {
        if (id == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setId(id);
        return customer;
    }

}
