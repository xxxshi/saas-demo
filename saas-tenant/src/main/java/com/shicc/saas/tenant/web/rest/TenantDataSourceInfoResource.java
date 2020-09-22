package com.shicc.saas.tenant.web.rest;

import com.shicc.saas.tenant.dto.TenantDataSourceInfoDTO;
import com.shicc.saas.tenant.service.TenantDataSourceInfoService;
import com.shicc.saas.tenant.web.rest.errors.BadRequestAlertException;
import org.apache.tomcat.util.http.HeaderUtil;
import org.apache.tomcat.util.http.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing TenantDataSourceInfo.
 */
@RestController
@RequestMapping("/api")
public class TenantDataSourceInfoResource {

    private final Logger log = LoggerFactory.getLogger(TenantDataSourceInfoResource.class);

    private static final String ENTITY_NAME = "tenantDataSourceInfo";

    @Autowired
    private TenantDataSourceInfoService tenantDataSourceInfoService;



    @PostMapping("/tenant-data-source-infos")
//    @Timed
    public ResponseEntity<TenantDataSourceInfoDTO> createTenantDataSourceInfo(@RequestBody TenantDataSourceInfoDTO tenantDataSourceInfoDTO) throws URISyntaxException {
        log.debug("REST request to save TenantDataSourceInfo : {}", tenantDataSourceInfoDTO);
        if (tenantDataSourceInfoDTO.getId() != null) {
            throw new BadRequestAlertException("A new tenantDataSourceInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TenantDataSourceInfoDTO result = tenantDataSourceInfoService.save(tenantDataSourceInfoDTO);
        return ResponseEntity.ok(result);
    }


    @PutMapping("/tenant-data-source-infos")
//    @Timed
    public ResponseEntity<TenantDataSourceInfoDTO> updateTenantDataSourceInfo(@RequestBody TenantDataSourceInfoDTO tenantDataSourceInfoDTO){
        log.debug("REST request to update TenantDataSourceInfo : {}", tenantDataSourceInfoDTO);
        if (tenantDataSourceInfoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TenantDataSourceInfoDTO result = tenantDataSourceInfoService.save(tenantDataSourceInfoDTO);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/tenant-data-source-infos/all")
//    @Timed
    public List<TenantDataSourceInfoDTO> getAllTenantDataSourceInfos() {
        log.debug("REST request to get all TenantDataSourceInfos");
        return tenantDataSourceInfoService.findAll();
    }


    /**
     * 根据服务名称获得mysql连接信息
     * @param serverName
     * @return
     */
    @GetMapping("/tenant-data-source-infos/serverName")
//    @Timed
    public ResponseEntity<List<TenantDataSourceInfoDTO>> getTenantDataSourceInfoListByServerName(@RequestParam("serverName") String serverName) {
        log.debug("request to getTenantDataSourceInfoListByServerName : {}", serverName);
        List<TenantDataSourceInfoDTO> result = tenantDataSourceInfoService.getTenantDataSourceInfoByServerName(serverName);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据服务名称,租户标志获得mysql连接信息
     * @param serverName
     * @return
     */
    @GetMapping("/tenant-data-source-infos/serverName/tenantCode")
//    @Timed
    public ResponseEntity<TenantDataSourceInfoDTO> getTenantDataSourceInfoListByServerNameAndTenant(@RequestParam("serverName") String serverName,@RequestParam("tenantCode") String tenantCode) {
        log.debug("request to getTenantDataSourceInfoListByServerName : {},{}", serverName, tenantCode);
        TenantDataSourceInfoDTO result = tenantDataSourceInfoService.getTenantDataSourceInfoByTenantAndServerName(tenantCode, serverName);
        return ResponseEntity.ok(result);
    }


    @PutMapping("/tenant-data-source-infos/push")
    public ResponseEntity<Boolean> pushTenantDataSourceInfo(@RequestParam("serverName") String serverName, @RequestParam("tenantCode") String tenantCode) {
        log.debug("REST request to get pushTenantDataSourceInfo : {}, {}", serverName, tenantCode);
        return ResponseEntity.ok(tenantDataSourceInfoService.push(tenantCode, serverName));
    }



}
