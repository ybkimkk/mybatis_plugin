package com.mybatis.plugin.view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author jinyongbin
 * @version 1.0
 * @since 2024/1/15
 *  创建窗口
 */
public class WindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        Window window = new Window(project, toolWindow);
        ContentFactory instance = ContentFactory.getInstance();
        Content content = instance.createContent(window.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
