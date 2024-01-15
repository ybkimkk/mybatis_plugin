package com.mybatis.plugin.utils;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.mybatis.plugin.enums.CrudKeyEnum;
import io.netty.util.internal.StringUtil;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jinyongbin
 * @version 1.0
 * @since 2024/1/15
 */
public class ConvertUtil {

    private String sql;

    private CrudKeyEnum sqlType;

    public String toMybatis(String sql) {
        this.sql = sql.trim();
        if (sql.trim().toUpperCase().startsWith(CrudKeyEnum.SELECT.getKey())) {
            sqlType = CrudKeyEnum.SELECT;
            return toMybatisSelect();
        } else if (sql.trim().toUpperCase().startsWith(CrudKeyEnum.DELETE.getKey())) {
            sqlType = CrudKeyEnum.DELETE;
            return toMybatisDelete();
        } else if (sql.trim().toUpperCase().startsWith(CrudKeyEnum.INSERT.getKey())) {
            sqlType = CrudKeyEnum.INSERT;
            return toMybatisInsert();
        }

        return null;
    }

    public String toMysql(String sql) {
        return null;
    }


    /**
     * to mybatis insert
     */
    private String toMybatisInsert() {
        String tableName = getTableName(CrudKeyEnum.INSERT.getKey());

        StringBuilder builder = new StringBuilder();
        builder.append("insert into ");
        builder.append(tableName);
        String tableField = getTableField();
        if (Strings.isNullOrEmpty(tableField)) {
            return null;
        }

        //INSERT INTO...SET 最终组装
        if (sql.toUpperCase().contains("SET")) {
            builder.append(StringUtil.LINE_FEED);
            String[] split = tableField.split(",");
            StringBuilder ifBuilder = new StringBuilder();
            for (String field : split) {
                ifBuilder.append(String.format(MybatisFormatUtil.IF_DOM, field, field, field + " = " + editMybatisValue(field) + StringUtil.COMMA));
            }
            builder.append(String.format(MybatisFormatUtil.SET_DOM, ifBuilder));
            return String.format(MybatisFormatUtil.INSERT_DOM, builder);
        }
        //INSERT INTO...VALUES 最终组装
        else {
//            builder.append(String.format(MybatisFormatUtil.TRIM_DOM, "(", ")", StringUtil.COMMA, ifBuilder));
        }

        return null;
    }

    private String getTableField() {
        Matcher regex;
        if (sqlType.equals(CrudKeyEnum.SELECT)) {
            regex = regex("(?i)SELECT\\\\s+([\\\\w,\\\\s]+)\\\\s+FROM", sql);
        } else if (sqlType.equals(CrudKeyEnum.INSERT)) {
            if (sql.toUpperCase().contains("SET")) {
                //INSERT INTO...SET 正则需要进一步获取值
                regex = regex("(?i)INSERT\\s+INTO\\s+`?\\w+`?\\s+SET\\s+([^;]+)", sql);
                StringJoiner joiner = new StringJoiner(",");
                if (regex.find()) {
                    String[] split = regex.group(1).split(",");
                    for (String s : split) {
                        String[] split1 = s.split("=");
                        joiner.add(split1[0].trim());
                    }
                }
                return joiner.toString();
            } else {
                //INSERT INTO...VALUES语句;
                regex = regex("(?i)INSERT\\s+INTO\\s+`?\\w+`?\\s*\\(([^)]+)\\)\\s*VALUES", sql);
                if (regex.find()) {
                    return regex.group();
                }
            }
        } else if (sqlType.equals(CrudKeyEnum.UPDATE)) {
            return null;
        }

        return null;
    }


    private String getInsertSqlAfterSet() {
        return null;
    }


    /**
     * to mybatis delete
     */
    private String toMybatisDelete() {
        String tableName = getTableName(CrudKeyEnum.DELETE.getKey());
        return String.format(MybatisFormatUtil.DELETE_DOM, tableName, getSelectSqlAfterWhere());
    }


    /**
     * to mybatis select
     */
    private String toMybatisSelect() {
        try {
            //提取 where 前面的语句
            String sqlBeforeWhere = getSqlBeforeWhere();
            //提取 where 后面的语句
            String sqlAfterWhere = getSelectSqlAfterWhere();
            //拼接 last 语句
            String sqlLast = getSqlLast();

            return String.format(
                    MybatisFormatUtil.SELECT_DOM,
                    sqlBeforeWhere,
                    sqlAfterWhere,
                    sqlLast);
        } catch (Exception e) {
            e.printStackTrace();
            return "sql 异常";
        }
    }


    private String getSqlBeforeWhere() {
        Matcher matcher = regex(MybatisRegexUtil.SQL_SELECT_BEFORE_WHERE, sql);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return StringUtil.EMPTY_STRING;
    }

    private String getSelectSqlAfterWhere() {
        Matcher matcher = regex(MybatisRegexUtil.SQL_SELECT_AFTER_WHERE, sql);
        if (matcher.find()) {
            String conditions = matcher.group(1).trim();
            matcher = regex(MybatisRegexUtil.SQL_SELECT_AFTER_WHERE_PARAM, conditions);
            //组装 mybatis where 语句
            StringBuilder ifBuilder = new StringBuilder();
            while (matcher.find()) {
                String logicalOperator = matcher.group(1);
                String columnName = matcher.group(2);
                String operator = matcher.group(3);
                String format = String.format(MybatisFormatUtil.IF_DOM,
                        columnName,
                        columnName,
                        (Objects.nonNull(logicalOperator) ? logicalOperator : " ") + columnName + " " + operator + editMybatisValue(columnName)
                );
                ifBuilder.append(format);
            }
            return String.format(MybatisFormatUtil.WHERE_DOM, ifBuilder);
        }
        return StringUtil.EMPTY_STRING;
    }

    private String getSqlLast() {
        Matcher matcher = regex(MybatisRegexUtil.SQL_SELECT_LAST, sql);
        if (matcher.find()) {
            return matcher.group();
        }
        return StringUtil.EMPTY_STRING;
    }

    private String getTableName(String type) {
        Matcher regex;
        if (CrudKeyEnum.DELETE.getKey().equals(type)) {
            regex = regex(MybatisRegexUtil.GET_DELETE_TABLE_NAME, sql);
        } else if (CrudKeyEnum.INSERT.getKey().equals(type)) {
            if (sql.toUpperCase().contains("SET")) {
                regex = regex(MybatisRegexUtil.GET_INSERT_SET_TABLE_NAME, sql);
            } else {
                regex = regex(MybatisRegexUtil.GET_INSERT_TABLE_NAME, sql);
            }
            if (regex.find()) {
                return regex.group(1);
            }
        } else if (CrudKeyEnum.UPDATE.getKey().equals(type)) {
            return null;
        }

        return null;
    }

    private Matcher regex(String regex, String sql) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(sql);
    }

    private String editMybatisValue(String field) {
        return "#{" + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, field) + "}";
    }
}
