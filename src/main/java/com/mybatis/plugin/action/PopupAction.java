package com.mybatis.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.mybatis.plugin.dialog.AddNoteDialog;

/**
 * @author jinyongbin
 * @version 1.0
 * @since 2024/1/14
 */
public class PopupAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        AddNoteDialog addActionDialog = new AddNoteDialog(selectedText);
        addActionDialog.show();
    }
}
