package com.shicc.saas.tenant.constant;

import java.util.HashMap;
import java.util.Map;


public enum TenantDataSourceStatus {

    INIT(1, "初始化"),
    ENABLE(2, "有效"),
    DISABLE(3, "无效"),;

    private int value;
    private String name;

    private static Map<Integer, TenantDataSourceStatus> valueMap = new HashMap<>(TenantDataSourceStatus.values().length);

    static {
        valueMap = new HashMap<>();
        for (TenantDataSourceStatus type : TenantDataSourceStatus.values()) {
            valueMap.put(type.getValue(), type);
        }
    }

    TenantDataSourceStatus(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static TenantDataSourceStatus fromValue(Integer value) {
        return valueMap.get(value);
    }


}
