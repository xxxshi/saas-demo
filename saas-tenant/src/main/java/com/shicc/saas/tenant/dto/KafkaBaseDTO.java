package com.shicc.saas.tenant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * kafka基础数据传输对象
 *
 * @author ziyan
 * @email zhengmengyan@taoqicar.com
 * @date 2019年01月03日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class KafkaBaseDTO {

    /**
     * 租户号 | 业务方id
     */
    private String tenantCode;

}
