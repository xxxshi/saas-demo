package com.shicc.customercenter.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public class CustomerDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 昵称
     */
    private String nick;

    /**
     * 密码
     */
    private String password;


    /**
     * 用户类型，0.正常注册; 1.虚拟账户; 2.三方登录; 3.合伙人; 4.导入的用户; 5.预注册,6.saas创建
     */
    private Integer type;


    /**
     * 手机号
     */
    private Long phoneNum;

    /**
     * 头像
     */
    private String headIcon;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 身份证
     */
    private String identityCard;

    /**
     * 创建时间
     */
    private ZonedDateTime gmtCreate;

    /**
     * 修改时间
     */
    private ZonedDateTime gmtModified;






}
