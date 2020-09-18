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


    @GetMapping("/tenant-data-source-infos/serverName")
//    @Timed
    public ResponseEntity<List<TenantDataSourceInfoDTO>> getTenantDataSourceInfoListByServerName(@RequestParam("serverName") String serverName) {
        log.debug("request to getTenantDataSourceInfoListByServerName : {}", serverName);
        List<TenantDataSourceInfoDTO> result = tenantDataSourceInfoService.getTenantDataSourceInfoByServerName(serverName);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/tenant-data-source-infos/{id}")
//    @Timed
    public ResponseEntity<TenantDataSourceInfoDTO> getTenantDataSourceInfo(@PathVariable Long id) {
        log.debug("REST request to get TenantDataSourceInfo : {}", id);
        Optional<TenantDataSourceInfoDTO> tenantDataSourceInfoDTO = tenantDataSourceInfoService.findOne(id);
        return ResponseEntity.ok(tenantDataSourceInfoDTO.get());
    }


    @DeleteMapping("/tenant-data-source-infos/{id}")
//    @Timed
    public ResponseEntity<Void> deleteTenantDataSourceInfo(@PathVariable Long id) {
        log.debug("REST request to delete TenantDataSourceInfo : {}", id);
        tenantDataSourceInfoService.delete(id);
        return ResponseEntity.ok().build();
    }
}
