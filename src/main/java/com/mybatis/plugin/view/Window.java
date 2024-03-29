package com.mybatis.plugin.view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.mybatis.plugin.tip.Tips;
import com.mybatis.plugin.utils.FocusUtil;
import com.mybatis.plugin.utils.MybatisConvertUtil;
import com.mybatis.plugin.utils.SqlConvertUtil;
import lombok.Getter;

import javax.swing.*;

/**
 * @author jinyongbin
 * @version 1.0
 * @since 2024/1/15
 */
@Getter
public class Window {
    private JPanel contentPanel;
    private JTabbedPane tabbedPane1;

    private JTextArea inputText;
    private JTextArea resultText;

    private JButton toMysqlButton;
    private JButton toMybatisButton;


    public Window(Project project, ToolWindow toolWindow) {
        MybatisConvertUtil mybatisUtil = new MybatisConvertUtil();
        SqlConvertUtil sqlConvertUtil = new SqlConvertUtil();

        toMybatisButton.addActionListener(actionEvent -> {
            String text = inputText.getText();
            String convert = mybatisUtil.convert(text);
            resultText.setText(convert);
        });

        toMysqlButton.addActionListener(actionEvent -> {
            String text = inputText.getText();
            String convert = sqlConvertUtil.convert(text);
            resultText.setText(convert);
        });
        //绑定text_area 焦点事件
        FocusUtil focusUtil = new FocusUtil();
        focusUtil.bindFocusEvent(inputText, Tips.PLEASE_INPUT_MYSQL);
    }

}
