package com.wrupple.muba.desktop.client.services.presentation.layout;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.wrupple.muba.desktop.client.activity.widgets.panels.TransitionPanel;
import com.wrupple.muba.desktop.domain.ToolbarConfiguration;

public interface LayoutPositioner {
	public static enum ToolbarJoining {
		STACK,VERTICAL, HORIZONTAL 
	}
	void initialize(LayoutPanel main,TransitionPanel container);

	void addAtPosition(ToolbarConfiguration config);

	void animate(int duration);
	
	void setUnit(Unit u);
	
	public void setJoinLineStartToolbars(ToolbarJoining joinLineStartToolbars);
	
	
	public void setLineStartCollapsible(boolean lineStartCollapsible) ;
	public void setLineEndCollapsible(boolean lineEndCollapsible) ;

	public void setJoinLineEndToolbars(ToolbarJoining joinLineEndToolbars);

}