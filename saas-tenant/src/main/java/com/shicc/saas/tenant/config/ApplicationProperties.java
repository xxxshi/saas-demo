package com.shicc.saas.tenant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Properties specific to Uaa Account.
 * <p>
 * Properties are configured in the application.yml file.
 */

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private KafKaTopics kafkaTopics;

    public KafKaTopics getKafkaTopics() {
        return kafkaTopics;
    }

    public ApplicationProperties setKafkaTopics(KafKaTopics kafkaTopics) {
        this.kafkaTopics = kafkaTopics;
        return this;
    }

    public static final class KafKaTopics {
        /**
         * 数据源推送
         */
        private String tenantDataSourceInfo;


        public String getTenantDataSourceInfo() {
            return tenantDataSourceInfo;
        }

        public KafKaTopics setTenantDataSourceInfo(String tenantDataSourceInfo) {
            this.tenantDataSourceInfo = tenantDataSourceInfo;
            return this;
        }
    }
}
