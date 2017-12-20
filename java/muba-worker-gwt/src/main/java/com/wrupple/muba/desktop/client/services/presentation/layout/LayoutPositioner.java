package com.wrupple.muba.desktop.client.services.presentation.layout;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.wrupple.muba.desktop.client.activity.widgets.panels.TransitionPanel;
import com.wrupple.muba.desktop.domain.ToolbarConfiguration;

public interface LayoutPositioner {
    void setJoinLineStartToolbars(ToolbarJoining joinLineStartToolbars);

    void initialize(LayoutPanel main,TransitionPanel container);

	void addAtPosition(ToolbarConfiguration config);

	void animate(int duration);
	
	void setUnit(Unit u);

    void setLineStartCollapsible(boolean lineStartCollapsible);

    void setLineEndCollapsible(boolean lineEndCollapsible);

    void setJoinLineEndToolbars(ToolbarJoining joinLineEndToolbars);

    enum ToolbarJoining {
        STACK, VERTICAL, HORIZONTAL
    }

}