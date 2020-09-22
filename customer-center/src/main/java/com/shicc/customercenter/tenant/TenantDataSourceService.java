package com.shicc.customercenter.tenant;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;

@Service
@EnableScheduling
public class TenantDataSourceService {
    private static final Logger log = LoggerFactory.getLogger(TenantDataSourceService.class);

    @Autowired
    private TenantDataSourceProvider tenantDataSourceProvider;

    private static final ParameterizedTypeReference<List<TenantDataSourceInfoDTO>> LIST_DATA_SOURCE_INFO = new ParameterizedTypeReference<List<TenantDataSourceInfoDTO>>() {
    };

    private static final ParameterizedTypeReference<TenantDataSourceInfoDTO> DATA_SOURCE_INFO = new ParameterizedTypeReference<TenantDataSourceInfoDTO>() {
    };

    private static final String SERVER_NAME = "customerCenter";


    /**
     * 初始化远程数据源
     */
    public void initRemoteDataSource() {
        if (StringUtils.isEmpty(SERVER_NAME)) {
            log.warn("server Name is null can't init remote dataSource");
            return;
        }
        try {
            List<TenantDataSourceInfoDTO> dataSourceInfoList = this.getRemoteDataSource();
            if (CollectionUtils.isEmpty(dataSourceInfoList)) {
                log.warn("remote dataSource is empty");
                return;
            }
            dataSourceInfoList.forEach(dataSourceInfo -> {
                addDataSource(dataSourceInfo);
            });
        } catch (Exception e) {
            log.error("init remote dataSource error. {}", e);
        }
    }

    /**
     * 添加数据源
     *
     * @param dataSourceInfo
     */
    public void addDataSource(TenantDataSourceInfoDTO dataSourceInfo) {
        log.info("addDataSource :{} ", dataSourceInfo);
        if (null == dataSourceInfo) {
            log.warn("remote datasource is empty.");
            return;
        }
        if ("mysql".equalsIgnoreCase(dataSourceInfo.getType())) {
            tenantDataSourceProvider.addDataSource(dataSourceInfo);
        }
    }

    /**
     * 初始化数据源
     *
     * @param dataSourceInfo
     */
    public void initDatabase(TenantDataSourceInfoDTO dataSourceInfo) {
        log.info("init multi database :{} ", dataSourceInfo);
        try {
            addDataSource(dataSourceInfo);
        } catch (Exception e) {
            log.error("initDatabase failed,{}", e);
        }
    }


    /**
     * 根据服务名称从远程获取数据源
     *
     * @return
     */
    public  List<TenantDataSourceInfoDTO> getRemoteDataSource() {
        Map<String, Object> parm = new HashMap();
        parm.put("serverName", SERVER_NAME);

        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate restTemplate = restTemplateBuilder.build();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> formEntity = new HttpEntity<>(null, headers);

        String url = "http://localhost:8002/api/tenant-data-source-infos/serverName?serverName={serverName}";

        List<TenantDataSourceInfoDTO> result = restTemplate.exchange(url, GET, formEntity, LIST_DATA_SOURCE_INFO, parm).getBody();
        return result;
    }



}
