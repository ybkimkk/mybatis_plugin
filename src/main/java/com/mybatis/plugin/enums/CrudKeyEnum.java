package com.mybatis.plugin.enums;

import lombok.Getter;

/**
 * @author jinyongbin
 * @version 1.0
 * @since 2024/1/15
 */
@Getter
public enum CrudKeyEnum {
    SELECT("SELECT"),
    DELETE("DELETE"),
    UPDATE("UPDATE"),
    INSERT("INSERT");
    private final String key;

    CrudKeyEnum(String key) {
        this.key = key;
    }
}
