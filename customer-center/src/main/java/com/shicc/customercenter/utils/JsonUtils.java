package com.shicc.customercenter.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;


public class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);
    }

    private JsonUtils() {
    }

    public static JsonNode readTree(String jsonString) {
        try {
            return mapper.readTree(jsonString);
        } catch (Exception e) {
            log.error("parse json to object failed, " + e.getMessage());
            return NullNode.instance;
        }
    }

    public static <T> T stringToObject(String jsonString, Class<T> className) {
        if (null != jsonString) {
            if (!StringUtils.isEmpty(jsonString)) {
                try {
                    return mapper.readValue(jsonString, className);
                } catch (Exception e) {
                    log.error("parse json to object failed, " + e.getMessage());
                }
            }
            log.info("the message body is empty");
        }
        return null;
    }

    public static <T> T byteToObject(byte[] bytes, Class<T> className) {
        if (null != bytes) {
            try {
                return mapper.readValue(bytes, className);
            } catch (Exception e) {
                log.error("parse json to object failed, " + e.getMessage());
            }
            log.info("the message body is empty");
        }
        return null;
    }

    public static String objectToJsonString(Object object) {
        if (null != object) {
            try {
                return mapper.writeValueAsString(object);
            } catch (JsonProcessingException e) {
                log.error("parse object to json failed, " + e.getMessage());
            }
        }
        return null;
    }

    public static JsonNode stringToJsonNode(String jsonString) {
        if (null != jsonString) {
            if (!StringUtils.isEmpty(jsonString)) {
                try {
                    return mapper.readTree(jsonString);
                } catch (Exception e) {
                    log.error("parse json to object failed, " + e.getMessage());
                }
            }
            log.info("the message body is empty");
        }
        return null;
    }

    public static <T> Object stringToCollectionObject(String jsonString, Class<?> collectionClass, Class<T> className) {
        if (null != jsonString) {
            if (!StringUtils.isEmpty(jsonString)) {
                try {
                    JavaType javaType = mapper.getTypeFactory().constructParametricType(collectionClass, className);
                    return mapper.readValue(jsonString, javaType);
                } catch (Exception e) {
                    log.error("parse json to object failed, " + e.getMessage());
                }
            }
            log.info("the message body is empty");
        }
        return null;
    }


}
