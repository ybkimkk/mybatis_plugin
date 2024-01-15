package com.mybatis.plugin.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorTextField;
import com.mybatis.plugin.data.NoteData;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author jinyongbin
 * @version 1.0
 * @since 2024/1/14
 */
public class AddNoteDialog extends DialogWrapper {
    EditorTextField title = new EditorTextField("笔记标题");
    EditorTextField context;

    public AddNoteDialog(String context) {
        super(true);
        setTitle("添加笔记");
        this.context = new EditorTextField(context);
        this.context.setPreferredSize(new Dimension(200, 100));
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(title, BorderLayout.NORTH);
        jPanel.add(context, BorderLayout.CENTER);
        return jPanel;
    }

    @Override
    protected JComponent createSouthPanel() {
        JPanel jPanel = new JPanel();
        JButton jButton = new JButton("添加笔记");
        jButton.addActionListener(e -> {
            String titleText = title.getText();
            String contextText = context.getText();
            System.out.println(titleText + ":" + contextText);

            NoteData noteData = new NoteData();
            noteData.setTitle(titleText);
            noteData.setMark(contextText);
        });
        jPanel.add(jButton);
        return jPanel;
    }
}
