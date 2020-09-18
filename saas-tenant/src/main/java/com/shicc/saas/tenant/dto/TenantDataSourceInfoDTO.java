package com.shicc.saas.tenant.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the TenantDataSourceInfo entity.
 */
@Data
public class TenantDataSourceInfoDTO implements Serializable {

    private Long id;

    private String url;

    private String database;

    private String username;

    private String password;

    private String tenantCode;

    private String jdbcDriver;

    private String serverName;

    private Integer status;

    private String type;

    private ZonedDateTime gmtCreate;

    private ZonedDateTime gmtModify;

    private Integer deleteStatus;




}
