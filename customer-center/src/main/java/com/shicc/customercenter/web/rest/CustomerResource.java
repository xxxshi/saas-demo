package com.shicc.customercenter.web.rest;

import com.shicc.customercenter.dto.CustomerDTO;
import com.shicc.customercenter.service.CustomerService;
import com.shicc.customercenter.tenant.ThreadTenantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class CustomerResource {
    private final Logger log = LoggerFactory.getLogger(CustomerResource.class);

    @Autowired
    private CustomerService customerService;


    @GetMapping("/customer/realName")
    public ResponseEntity<CustomerDTO> getCustomerByRealName(String realName, String tenantCode) {
        log.debug("REST request to getCustomerByRealName : {},{}", realName, tenantCode);
        CustomerDTO result = customerService.getCustomerByRealName(realName);
        return ResponseEntity.ok(result);
    }

}
