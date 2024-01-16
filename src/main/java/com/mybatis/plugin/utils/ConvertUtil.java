package com.mybatis.plugin.utils;

/**
 * @author jinyongbin
 * @version 1.0
 * @since 2024/1/16
 */
public interface ConvertUtil {
    String convert(String text);

     String select(String text);
    String update(String text);
    String delete(String text);

    String insert(String text);
}
