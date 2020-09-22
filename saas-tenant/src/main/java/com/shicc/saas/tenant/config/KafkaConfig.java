//package com.shicc.saas.tenant.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Primary;
//import org.springframework.kafka.core.KafkaTemplate;
//
//public class KafkaConfig {
//    //这个是我们之前编写的KafkaTemplate代码，加入@Primary注解
//    @Bean
//    @Primary
//    public KafkaTemplate<String, Object> kafkaTemplate() {
//        KafkaTemplate template = new KafkaTemplate<Integer, String>();
//        return template;
//    }
//}
