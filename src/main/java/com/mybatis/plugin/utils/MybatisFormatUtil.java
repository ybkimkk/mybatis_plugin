package com.mybatis.plugin.utils;

/**
 * @author jinyongbin
 * @version 1.0
 * @since 2024/1/15
 */

public class MybatisFormatUtil {
    public static final String SELECT_DOM = """
                <select id="your_id" parameterType="your_dao" resultType="your_type">
                    %s
                    %s
                    %s
                </select>
            """;
    public static final String WHERE_DOM = """
                <where>
                    %s
                </where>
            """;
    public static final String IF_DOM = """
                <if test="%s != null and %s != ''">
                    %s
                </if>
            """;

    public static final String INSERT_DOM = """
                <insert id="your_id" keyProperty="your_key_property_id">
                     %s
                </insert>
            """;

    public static final String SET_DOM = """
                <set>
                    %s
                </set>
            """;

    public static final String TRIM_DOM = """
                <trim prefix="%s" suffix="%s" suffixOverrides="%s">
                    %s
                </trim>
            """;

    public static final String DELETE_DOM = """
                <delete id="your_id">
                     delete from %s
                     %s
                </delete>
            """;
}
