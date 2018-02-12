package com.wrupple.muba.worker.shared.widgets;

import com.wrupple.muba.desktop.client.widgets.TaskWindow;

public interface HumanTaskWindow extends AcceptsOneWidget, TaskWindow {


    String REDRAW_FLAG = "toolbarRedraw";
    String ActivityPresenterToolbarHeight = "activityPresenterToolbarHeight";

    void setMainTaskProcessor(HumanTaskProcessor<Object> ui);//.setWidget(transactionView);

    Toolbar getToolbarById(String toolbarId);

    boolean isToolbarVisible(String toolbarId);

    void focusToolbar(String toolbarId);

    void focusToolbar(Toolbar activityToolbar);

    void addToolbar(Toolbar toolbar, Object properties);

    void setUnit(String layoutUnit);

    enum ToolbarDirection {
        NORTH, EAST, SOUTH, WEST, CENTER, LINE_START, LINE_END, WIDE_START, WIDE_END, SHORT_START, SHORT_END
    }


}
