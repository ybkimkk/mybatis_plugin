package com.mybatis.plugin.data;

import lombok.Data;

/**
 * @author jinyongbin
 * @version 1.0
 * @since 2024/1/15
 */
@Data
public class NoteData {
    private String title;
    private String mark;
    private String content;
    private String fileName;
    private String fileType;
}
