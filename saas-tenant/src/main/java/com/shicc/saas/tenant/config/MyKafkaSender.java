package com.shicc.saas.tenant.config;

import com.shicc.saas.tenant.dto.KafkaBaseDTO;
import com.shicc.saas.tenant.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 消息发送
 *
 */
@Component
@ConditionalOnProperty("spring.kafka.bootstrap-servers")
@EnableKafka
public class MyKafkaSender<T extends KafkaBaseDTO> {

    private final Logger log = LoggerFactory.getLogger(MyKafkaSender.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private KafkaTopicProperties kafkaTopicProperties;

    @Autowired
    private KafkaProperties kafkaProperties;

    /**
     * 创建已配置的topic
     */
    public void initKafkaTopics() {
        log.debug("init kafka topics from kafkaProperties:{}", kafkaTopicProperties);
        List<NewTopic> topicList = new ArrayList<>();

        Map<String, Object> props = new HashMap<>(1);
        //配置Kafka实例的连接地址
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        if (null != kafkaTopicProperties && null != kafkaTopicProperties.getTopics()) {
            kafkaTopicProperties.getTopics().values().stream().forEach(value ->
                    topicList.add(new NewTopic(String.valueOf(value), NumberUtils.INTEGER_ONE, NumberUtils.SHORT_ONE))
            );
            AdminClient adminClient = AdminClient.create(props);
            adminClient.createTopics(topicList);
            adminClient.close();
            log.debug("init kafka topics success");
        } else {
            log.debug("kafka topics is empty");
        }
    }

    /**
     * 同步发送
     *
     * @param topic
     * @param object
     */
    public void sendMessageSync(String topic, T object) {
        log.info("sendMessageSync:{},{}", topic, object);
        this.sendMessage(topic, object);
    }

    /**
     * 异步发送
     *
     * @param topic
     * @param object
     */
    @Async
    public void sendMessageAsync(String topic, T object) {
        log.info("sendMessageAsync:{},{}", topic, object);
        this.sendMessage(topic, object);
    }

    /**
     * 同步发送
     *
     * @param topic
     * @param object
     */
    public void sendMessageSync(String topic, String tenantCode, T object) {
        log.info("sendMessageSync:{},{}", topic, object);
        if (object != null) {
            object.setTenantCode(tenantCode);
        }
        this.sendMessage(topic, object);
    }

    /**
     * 异步发送
     *
     * @param topic
     * @param object
     */
    @Async
    public void sendMessageAsync(String topic, String tenantCode, T object) {
        log.info("sendMessageAsync:{},{}", topic, object);
        if (object != null) {
            object.setTenantCode(tenantCode);
        }
        this.sendMessage(topic, object);
    }

    /**
     * kafka 发送消息
     *
     * @param topic  主题
     * @param object 消息对象
     */
    private void sendMessage(String topic, T object) {
        try {
            String jsonString = JsonUtils.objectToJsonString(object);
            if (StringUtils.isBlank(jsonString)) {
                log.warn("message is empty");
                return;
            }
            //发送消息
            kafkaTemplate.send(topic, jsonString).addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
                @Override
                public void onFailure(Throwable throwable) {
                    log.warn("Producer: The message failed to be sent.{}", throwable.getMessage());
                }

                @Override
                public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                    log.info("Producer: The message was sent successfully.");
                }
            });
        } catch (Exception e) {
            log.error("Producer: The message send error.{}", e);
        }
    }
}
