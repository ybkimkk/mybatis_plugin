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

    private String tableName;
    private String tableField;

    private static final String SQL_ERROR = "sql 异常";

    public String toMybatis(String sql) {
        this.sql = sql.trim();
        //检查 crud 语法是否正确
        for (CrudKeyEnum value : CrudKeyEnum.values()) {
            if (sql.trim().toUpperCase().startsWith(value.getKey())) {
                this.sqlType = value;
                break;
            }
        }
        if (Objects.isNull(this.sqlType)) {
            return SQL_ERROR;
        }

        tableName = getTableName();
        tableField = getTableField();
        //select 和 delete 语句不检查
        if (!sqlType.equals(CrudKeyEnum.SELECT) && !sqlType.equals(CrudKeyEnum.DELETE)) {
            if (Strings.isNullOrEmpty(this.tableField)) {
                return SQL_ERROR;
            }
        }

        if (Strings.isNullOrEmpty(this.tableName)) {
            return SQL_ERROR;
        }


        String returnValue;
        if (sqlType.equals(CrudKeyEnum.SELECT)) {
            returnValue = toMybatisSelect();
        } else if (sqlType.equals(CrudKeyEnum.DELETE)) {
            returnValue = toMybatisDelete();
        } else if (sqlType.equals(CrudKeyEnum.INSERT)) {
            returnValue = toMybatisInsert();
        } else if (sqlType.equals(CrudKeyEnum.UPDATE)) {
            returnValue = toMybatisUpdate();
        } else {
            return SQL_ERROR;
        }

        return returnValue;
    }

    public String toMysql(String sql) {
        return null;
    }


    /**
     * to mybatis Update
     */
    private String toMybatisUpdate() {
        StringBuilder builder = new StringBuilder();
        builder.append("update ");
        builder.append(tableName);
        builder.append(StringUtil.LINE_FEED);
        String[] split = tableField.split(",");
        StringBuilder ifBuilder = new StringBuilder();
        for (String field : split) {
            ifBuilder.append(editMybatisIfDom(field,field + " = " + editMybatisValue(field) + StringUtil.COMMA));
        }
        builder.append(String.format(MybatisFormatUtil.SET_DOM, ifBuilder));
        builder.append(getMybatisWhereFromSql());
        return String.format(MybatisFormatUtil.UPDATE_DOM, builder);
    }


    /**
     * to mybatis insert
     */
    private String toMybatisInsert() {
        StringBuilder builder = new StringBuilder();
        builder.append("insert into ");
        builder.append(tableName);
        builder.append(StringUtil.LINE_FEED);
        String[] fields = tableField.split(",");
        StringBuilder ifBuilder = new StringBuilder();

        //INSERT INTO...SET 最终组装
        if (sql.toUpperCase().contains("SET")) {
            for (String field : fields) {
                ifBuilder.append(editMybatisIfDom(field,field + " = " + editMybatisValue(field) + StringUtil.COMMA));
            }
            builder.append(String.format(MybatisFormatUtil.SET_DOM, ifBuilder));
        }
        //INSERT INTO...VALUES 最终组装
        else {
            for (String field : fields) {
                ifBuilder.append(String.format(editMybatisIfDom(field,field)));
            }
            builder.append(String.format(MybatisFormatUtil.TRIM_DOM, "(", ")", StringUtil.COMMA, ifBuilder));
            ifBuilder = new StringBuilder();
            for (String field : fields) {
                ifBuilder.append(String.format(editMybatisIfDom(field,editMybatisValue(field))));
            }
            builder.append(String.format(MybatisFormatUtil.TRIM_DOM, "values (", ")", StringUtil.COMMA, ifBuilder));
        }
        return String.format(MybatisFormatUtil.INSERT_DOM, builder);
    }


    /**
     * to mybatis delete
     */
    private String toMybatisDelete() {
        String where = getMybatisWhereFromSql();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("delete from ");
        stringBuilder.append(tableName);
        stringBuilder.append(StringUtil.LINE_FEED);
        stringBuilder.append(where);
        return String.format(MybatisFormatUtil.DELETE_DOM, stringBuilder);
    }


    /**
     * to mybatis select
     */
    private String toMybatisSelect() {
        String returnValue;
        try {
            //提取 where 前面的语句
            String sqlBeforeWhere = getSqlBeforeWhere();
            //提取 where 后面的语句
            String sqlAfterWhere = getMybatisWhereFromSql();
            //拼接 last 语句
            String sqlLast = getSqlLast();

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(sqlBeforeWhere);
            stringBuilder.append(StringUtil.LINE_FEED);
            stringBuilder.append(sqlAfterWhere);
            stringBuilder.append(StringUtil.LINE_FEED);
            stringBuilder.append(sqlLast);
            returnValue = String.format(
                    MybatisFormatUtil.SELECT_DOM,
                    stringBuilder);
        } catch (Exception e) {
            returnValue = SQL_ERROR;
        }
        return returnValue;
    }


    private String getSqlBeforeWhere() {
        Matcher matcher = regex(MybatisRegexUtil.SQL_SELECT_BEFORE_WHERE, sql);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return StringUtil.EMPTY_STRING;
    }

    private String getMybatisWhereFromSql() {
        Matcher matcher = regex(MybatisRegexUtil.SQL_SELECT_AFTER_WHERE, sql);
        String returnValue = StringUtil.EMPTY_STRING;
        if (matcher.find()) {
            String conditions = matcher.group(1).trim();
            matcher = regex(MybatisRegexUtil.SQL_SELECT_AFTER_WHERE_PARAM, conditions);
            //组装 mybatis where 语句
            StringBuilder ifBuilder = new StringBuilder();
            while (matcher.find()) {
                String logicalOperator = matcher.group(1);
                String columnName = matcher.group(2);
                String operator = matcher.group(3);
                String ifValue = (Objects.nonNull(logicalOperator) ? logicalOperator : " ") + columnName + " " + operator + editMybatisValue(columnName);
                String ifDom = editMybatisIfDom(columnName,ifValue);
                ifBuilder.append(ifDom);
            }
            returnValue = String.format(MybatisFormatUtil.WHERE_DOM, ifBuilder);
        }
        return returnValue;
    }

    private String getSqlLast() {
        Matcher matcher = regex(MybatisRegexUtil.SQL_SELECT_LAST, sql);
        if (matcher.find()) {
            return matcher.group();
        }
        return StringUtil.EMPTY_STRING;
    }

    private String getTableName() {
        Matcher matcher;
        if (CrudKeyEnum.DELETE.equals(sqlType)) {
            matcher = regex(MybatisRegexUtil.GET_DELETE_TABLE_NAME, sql);
        } else if (CrudKeyEnum.INSERT.equals(sqlType)) {
            if (sql.toUpperCase().contains("SET")) {
                matcher = regex(MybatisRegexUtil.GET_INSERT_SET_TABLE_NAME, sql);
            } else {
                matcher = regex(MybatisRegexUtil.GET_INSERT_TABLE_NAME, sql);
            }
        } else if (CrudKeyEnum.UPDATE.equals(sqlType)) {
            matcher = regex(MybatisRegexUtil.GET_UPDATE_TABLE_NAME, sql);
        } else if (CrudKeyEnum.SELECT.equals(sqlType)) {
            matcher = regex(MybatisRegexUtil.GET_SELECT_TABLE_NAME, sql);
            ;
        } else {
            matcher = null;
        }

        if (Objects.nonNull(matcher) && matcher.find()) {
            return "`" + matcher.group(1) + "`";
        }

        return StringUtil.EMPTY_STRING;
    }

    private Matcher regex(String regex, String sql) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(sql);
    }

    private String editMybatisValue(String field) {
        return "#{" + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, field) + "}";
    }

    private String editMybatisIfDom(String tableField, String condition) {
        return String.format(
                MybatisFormatUtil.IF_DOM,
                tableField,
                tableField,
                condition
        );
    }

    private String getTableField() {
        Matcher matcher;
        String returnValue = StringUtil.EMPTY_STRING;
        if (sqlType.equals(CrudKeyEnum.INSERT)) {
            if (sql.toUpperCase().contains("SET")) {
                matcher = regex("(?i)INSERT\\s+INTO\\s+`?\\w+`?\\s+SET\\s+([^;]+)", sql);
                returnValue = getSetSqlField(matcher);
            } else {
                matcher = regex("(?i)INSERT\\s+INTO\\s+`?\\w+`?\\s*\\(([^)]+)\\)\\s*VALUES", sql);
                if (matcher.find()) {
                    returnValue = matcher.group(1);
                }
            }
        } else if (sqlType.equals(CrudKeyEnum.UPDATE)) {
            matcher = regex("SET\\s+([^;]+)\\s*", sql);
            returnValue = getSetSqlField(matcher);
        }

        return returnValue;
    }

    private String getSetSqlField(Matcher matcher) {
        StringJoiner joiner = new StringJoiner(",");
        if (matcher.find()) {
            String[] split = matcher.group(1).split(",");
            for (String s : split) {
                String[] split1 = s.split("=");
                joiner.add(split1[0].trim());
            }
        }
        return joiner.toString();
    }
}
