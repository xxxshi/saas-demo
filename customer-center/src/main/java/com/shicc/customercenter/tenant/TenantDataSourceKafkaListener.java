package com.shicc.customercenter.tenant;


import com.shicc.customercenter.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnProperty("spring.kafka.bootstrap-servers")
public class TenantDataSourceKafkaListener {

    private final Logger log = LoggerFactory.getLogger(TenantDataSourceKafkaListener.class);

    @Value("${eureka.instance.appname}")
    private String APP_NAME;

    @Value("${spring.kafka.topics.tenant-data-source-info}")
    private String tenantDataSourceInfo;

    @Autowired
    private TenantDataSourceService tenantDataSourceService;

    /**
     * 监听通道信息 id生成 兼容低版本 group与高版本groupId
     *
     * @param record
     */
    @KafkaListener(
            id = "tenantDataSourceKafkaListener-${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topics.tenant-data-source-info}")
    public void tenantDataSourceKafkaListener(ConsumerRecord<?, String> record) {
        log.info("tenantDataSourceKafkaListener:{}.", record);
        Optional<String> kafkaMessage = Optional.ofNullable(record.value());
        kafkaMessage.ifPresent(message -> initTenantInfo(JsonUtils.stringToObject(message, TenantDataSourceInfoDTO.class)));
    }

    /**
     * 初始化
     *
     * @param dataSourceInfo
     */
    public void initTenantInfo(TenantDataSourceInfoDTO dataSourceInfo) {
        if (null == dataSourceInfo) {
            log.debug("notify dataSourceInfo is empty.");
            return;
        }
        if (!APP_NAME.equalsIgnoreCase(dataSourceInfo.getServerName())) {
            log.debug("is not this server:{} notify.", APP_NAME);
            return;
        }
//        if (tenantDataSourceService.checkDataSource(dataSourceInfo.getTenantCode(), dataSourceInfo.getType())) {
//            log.info("tenant datasource is exist");
//            return;
//        }
        tenantDataSourceService.initDatabase(dataSourceInfo);

    }



}