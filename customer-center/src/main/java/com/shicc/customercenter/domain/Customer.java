package com.shicc.customercenter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A User.
 */
@Entity
@Table(name = "customer")
@Data
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    /**
     * 昵称
     */
    @Column(name = "nick", nullable = true)
    private String nick;

    /**
     * 密码
     */
    @Column(name = "password", nullable = true)
    private String password;


    /**
     * 用户类型，0.正常注册; 1.虚拟账户; 2.三方登录; 3.合伙人; 4.导入的用户; 5.预注册,6.saas创建
     */
    @Column(name = "type", nullable = false)
    private Integer type;


    /**
     * 手机号
     */
    @Column(name = "phone_num", unique = true)
    private Long phoneNum;

    /**
     * 头像
     */
    @Column(name = "head_icon")
    private String headIcon;

    /**
     * 姓名
     */
    @Column(name = "real_name")
    private String realName;

    /**
     * 身份证
     */
    @Column(name = "identity_card")
    private String identityCard;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create", nullable = true)
    private ZonedDateTime gmtCreate;

    /**
     * 修改时间
     */
    @Column(name = "gmt_modified", nullable = false)
    private ZonedDateTime gmtModified;






}
