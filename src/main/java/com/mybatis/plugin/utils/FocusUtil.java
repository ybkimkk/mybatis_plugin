package com.mybatis.plugin.utils;

import io.netty.util.internal.StringUtil;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * @author jinyongbin
 * @version 1.0
 * @since 2024/1/15
 */
public class FocusUtil {
    public void bindFocusEvent(JTextArea area,String tip) {
        area.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (area.getText().equals(tip)) {
                    area.setText(StringUtil.EMPTY_STRING);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (area.getText().isEmpty()) {
                    area.setText(tip);
                }
            }
        });
    }
}
