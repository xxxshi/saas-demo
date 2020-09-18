package com.shicc.saas.tenant.service;

import com.shicc.saas.tenant.constant.TenantDataSourceStatus;
import com.shicc.saas.tenant.domain.TenantDataSourceInfo;
import com.shicc.saas.tenant.dto.TenantDataSourceInfoDTO;
import com.shicc.saas.tenant.repository.TenantDataSourceInfoRepository;
import com.shicc.saas.tenant.service.mapper.TenantDataSourceInfoMapper;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing TenantDataSourceInfo.
 */
@Service
@Transactional
public class TenantDataSourceInfoService {

    private final Logger log = LoggerFactory.getLogger(TenantDataSourceInfoService.class);

    @Autowired
    private  TenantDataSourceInfoRepository tenantDataSourceInfoRepository;

    @Autowired
    private TenantDataSourceInfoMapper tenantDataSourceInfoMapper;



    /**
     * Save a tenantDataSourceInfo.
     *
     * @param tenantDataSourceInfoDTO the entity to save
     * @return the persisted entity
     */
    public TenantDataSourceInfoDTO save(TenantDataSourceInfoDTO tenantDataSourceInfoDTO) {
        log.debug("Request to save TenantDataSourceInfo : {}", tenantDataSourceInfoDTO);

        TenantDataSourceInfo tenantDataSourceInfo = tenantDataSourceInfoMapper.toEntity(tenantDataSourceInfoDTO);
        tenantDataSourceInfo = tenantDataSourceInfoRepository.save(tenantDataSourceInfo);
        return tenantDataSourceInfoMapper.toDto(tenantDataSourceInfo);
    }

    /**
     * Get all the tenantDataSourceInfos.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<TenantDataSourceInfoDTO> findAll() {
        log.debug("Request to get all TenantDataSourceInfos");
        return tenantDataSourceInfoRepository.findByDeleteStatus(0).stream()
                .map(tenantDataSourceInfoMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one tenantDataSourceInfo by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<TenantDataSourceInfoDTO> findOne(Long id) {
        log.debug("Request to get TenantDataSourceInfo : {}", id);
        return tenantDataSourceInfoRepository.findById(id)
            .map(tenantDataSourceInfoMapper::toDto);
    }

    /**
     * Delete the tenantDataSourceInfo by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete TenantDataSourceInfo : {}", id);
        tenantDataSourceInfoRepository.deleteById(id);
    }

    /**
     * 根据服务查询数据源列表
     *
     * @param serverName
     * @return
     */
    public List<TenantDataSourceInfoDTO> getTenantDataSourceInfoByServerName(String serverName) {
        log.debug("Request to get TenantDataSourceInfo by server name : {}", serverName);
        return tenantDataSourceInfoRepository.findByServerNameAndStatusAndDeleteStatus(serverName, TenantDataSourceStatus.ENABLE.getValue(), NumberUtils.INTEGER_ZERO)
            .stream().map(tenantDataSourceInfoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * 根据租户编码和服务名称查询数据源
     *
     * @param tenantCode
     * @param serverName
     * @return
     */
    public TenantDataSourceInfoDTO getTenantDataSourceInfoByTenantAndServerName(String tenantCode, String serverName, String type) {
        log.debug("Request to get TenantDataSourceInfo by tenant code and server name  : {}, {}, type", tenantCode, serverName, type);
        return tenantDataSourceInfoMapper.toDto(tenantDataSourceInfoRepository.findTopByTenantCodeAndServerNameAndTypeAndDeleteStatus(tenantCode, serverName,type, NumberUtils.INTEGER_ZERO));
    }

    /**
     * 根据租户编码查询
     *
     * @param tenantCode
     * @return
     */
    public List<TenantDataSourceInfoDTO> getByTenantCode(String tenantCode) {
        log.debug("Request to get TenantDataSourceInfo by tenant code: {}", tenantCode);
        return tenantDataSourceInfoRepository.findByTenantCodeAndDeleteStatus(tenantCode, NumberUtils.INTEGER_ZERO)
            .stream().map(tenantDataSourceInfoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * 根据租户编码和状态获取
     *
     * @param tenantCode
     * @param status
     * @return
     */
    public List<TenantDataSourceInfoDTO> getByTenantCodeAndStatus(String tenantCode, Integer status) {
        log.debug("Request to get TenantDataSourceInfo by tenant code and status: {}", tenantCode, status);
        return tenantDataSourceInfoRepository.findByTenantCodeAndStatusAndDeleteStatus(tenantCode, status, NumberUtils.INTEGER_ZERO)
            .stream().map(tenantDataSourceInfoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }
}
