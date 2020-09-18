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

    private final String SERVER_NAME = "customerCenter";


    /**
     * 初始化远程数据源
     */
    public void initRemoteDataSource() {
        if (StringUtils.isEmpty(SERVER_NAME)) {
            log.warn("server Name is null can't init remote dataSource");
            return;
        }
        try {
            List<TenantDataSourceInfoDTO> dataSourceInfoList = this.getRemoteDataSource(SERVER_NAME);
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
            DataSource dataSource = null;
            //尝试连接租户指定数据源
            dataSource = tenantDataSourceProvider.genDataSource(dataSourceInfo);
            dataSource.getConnection();
            //initDatabaseBySpringLiquibase(dataSource);
        } catch (Exception e) {
            log.error("initDatabase failed,{}", e);
        }
    }


    /**
     * 从远程获取数据源
     *
     * @return
     */
    public static List<TenantDataSourceInfoDTO> getRemoteDataSource(String serverName) {
        Map<String, Object> parm = new HashMap();
        parm.put("serverName", serverName);

        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate restTemplate = restTemplateBuilder.build();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> formEntity = new HttpEntity<>(null, headers);

        String url = "http://localhost:8002/api/tenant-data-source-infos/serverName?serverName={serverName}";

        List<TenantDataSourceInfoDTO> result = restTemplate.exchange(url, GET, formEntity, LIST_DATA_SOURCE_INFO, parm).getBody();
        return result;
    }



}
