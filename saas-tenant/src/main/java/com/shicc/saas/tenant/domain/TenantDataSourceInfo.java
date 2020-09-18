package com.shicc.saas.tenant.domain;



import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;


/**
 * A TenantDataSourceInfo.
 */
@Entity
@Table(name = "tenant_data_source_info")
@Data
public class TenantDataSourceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "database")
    private String database;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "tenant_code")
    private String tenantCode;

    @Column(name = "jdbc_driver")
    private String jdbcDriver;

    @Column(name = "server_name")
    private String serverName;

    /**
     * 数据源类型 ：mysql,
     */
    @Column(name = "type")
    private String type;

    /**
     *     INIT(1, "初始化"),
     *     ENABLE(2, "有效"),
     *     DISABLE(3, "无效"),;
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private ZonedDateTime gmtCreate;

    /**
     * 修改时间
     */
    @Column(name = "gmt_modify")
    private ZonedDateTime gmtModify;

    /**
     * 是否删除
     */
    @Column(name = "delete_status")
    private Integer deleteStatus;



}
