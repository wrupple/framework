package com.wrupple.muba.worker.shared.widgets;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.desktop.client.widgets.TaskWindow;

public interface HumanTaskWindow extends AcceptsOneWidget, TaskWindow {


    String REDRAW_FLAG = "toolbarRedraw";
    String ActivityPresenterToolbarHeight = "activityPresenterToolbarHeight";

    void setMainTaskProcessor(HumanTaskProcessor<? extends JavaScriptObject, ?> ui);

    Toolbar getToolbarById(String toolbarId);

    boolean isToolbarVisible(String toolbarId);

    void focusToolbar(String toolbarId);

    void focusToolbar(Toolbar activityToolbar);

    void addToolbar(Toolbar toolbar, JavaScriptObject properties);

    void setUnit(String layoutUnit);

    enum ToolbarDirection {
        NORTH, EAST, SOUTH, WEST, CENTER, LINE_START, LINE_END, WIDE_START, WIDE_END, SHORT_START, SHORT_END
    }


}
