package com.mybatis.plugin.utils;

/**
 * @author jinyongbin
 * @version 1.0
 * @since 2024/1/15
 */
public class MybatisRegexUtil {
    public static final String SQL_SELECT_BEFORE_WHERE = "(.*?)\\bwhere\\b";
    public static final String SQL_SELECT_AFTER_WHERE = "\\bwhere\\b\\s*(.*)";
    public static final String SQL_WHERE = "\\bWHERE\\b\\s+(.*?)\\s+(\\bGROUP\\b|\\bORDER\\b|\\bLIMIT\\b|$)";
    public static final String SQL_SELECT_AFTER_WHERE_PARAM = "\\b(AND|OR)?\\s*(\\w+)\\s*([!=<>]+)\\s*([\\w']+)";
    public static final String SQL_SELECT_LAST = "\\b(?:limit|group\\s+by|order\\s+by)\\b\\s*(.*)$";


    public static final String GET_SELECT_TABLE_NAME = "\\bFROM\\s+(\\w+)";
    public static final String GET_DELETE_TABLE_NAME = "DELETE\\s+FROM\\s+`?([^`\\s]+)`?\\s*WHERE.*";
    public static final String GET_UPDATE_TABLE_NAME = "UPDATE\\s+`?([^`\\s]+)`?\\s+SET.*";
    public static final String GET_INSERT_SET_TABLE_NAME = "INSERT\\s+INTO\\s+`?([^`\\s]+)`?\\s*SET.*;?";
    public static final String GET_INSERT_TABLE_NAME = "INSERT\\s+INTO\\s+`?([^`\\s]+)`?\\s*\\(.*\\)\\s*VALUES\\s*\\(.*\\);?";
}
