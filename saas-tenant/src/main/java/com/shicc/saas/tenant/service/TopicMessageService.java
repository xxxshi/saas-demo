package com.shicc.saas.tenant.service;


import com.shicc.saas.tenant.config.MyKafkaSender;
import com.shicc.saas.tenant.dto.KafkaBaseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Service
@ConditionalOnProperty("spring.kafka.bootstrap-servers")
public class TopicMessageService {

    private final Logger log = LoggerFactory.getLogger(TopicMessageService.class);

    @Autowired
    private MyKafkaSender<KafkaBaseDTO> myKafkaSender;

    /**
     * 同步 发送
     *
     * @param topic
     * @param t
     */
    public <T extends KafkaBaseDTO> void sendSync(String topic, T t) {
        log.debug("sync send message:{},{},{}", topic, t.getTenantCode(), t);
        myKafkaSender.sendMessageSync(topic, t);
    }

    /**
     * 异步 发送
     *
     * @param topic
     * @param t
     */
    public <T extends KafkaBaseDTO> void sendAsync(String topic, T t) {
        log.debug("sync send message:{},{},{}", topic, t.getTenantCode(), t);
        myKafkaSender.sendMessageAsync(topic, t);
    }

    /**
     * 同步 发送
     *
     * @param topic
     * @param t
     */
    public <T extends KafkaBaseDTO> void sendSync(String topic, String tenantCode, T t) {
        log.debug("sync send message:{},{},{}", topic, tenantCode, t);
        myKafkaSender.sendMessageSync(topic, tenantCode, t);
    }

    /**
     * 异步 发送
     *
     * @param topic
     * @param t
     */
    public <T extends KafkaBaseDTO> void sendAsync(String topic, String tenantCode, T t) {
        log.debug("async send message:{},{},{}", topic, tenantCode, t);
        myKafkaSender.sendMessageAsync(topic, tenantCode, t);
    }

}
