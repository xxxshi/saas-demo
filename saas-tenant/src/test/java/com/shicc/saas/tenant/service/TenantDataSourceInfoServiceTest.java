package com.shicc.saas.tenant.service;

import com.shicc.saas.tenant.dto.TenantDataSourceInfoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TenantDataSourceInfoServiceTest {

    @Autowired
    private TenantDataSourceInfoService tenantDataSourceInfoService;

    @Test
    void findAll() {
        List<TenantDataSourceInfoDTO> all = tenantDataSourceInfoService.findAll();

        System.out.println(all);
    }
}
